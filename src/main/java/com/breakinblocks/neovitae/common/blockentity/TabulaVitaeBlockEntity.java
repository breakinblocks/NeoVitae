package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.menu.TabulaVitaeMenu;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeInput;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskInput;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import net.minecraft.world.item.crafting.RecipeHolder;

public class TabulaVitaeBlockEntity extends BaseBlockEntity implements MenuProvider, SideConfigurable {
    public enum IdleReason {
        NONE, NO_RECIPE, NO_ORB, ORB_UNBOUND, TIER_TOO_LOW, NOT_ENOUGH_EV, OUTPUT_BLOCKED;

        private static final IdleReason[] VALUES = values();

        public static IdleReason byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : NONE;
        }
    }

    private IdleReason idleReason = IdleReason.NONE;

    public static final int ORB_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;
    public static final int SLOT_COUNT = 8;

    private static final boolean[][] DEFAULT_SIDE_CONFIG = new boolean[][] {
            { false, false, true, true, true, true },
            { false, false, true, true, true, true },
            { false, false, true, true, true, true },
            { false, false, true, true, true, true },
            { false, false, true, true, true, true },
            { false, false, true, true, true, true },
            { false, true, false, false, false, false },
            { true, false, false, false, false, false }
    };

    public Direction direction = Direction.NORTH;
    public boolean isSlave = false;
    public int burnTime = 0;
    public int ticksRequired = 1;
    public BlockPos connectedPos = BlockPos.ZERO;
    public int activeSlot = -1;

    private final SlotSideConfig sideConfig = new SlotSideConfig(SLOT_COUNT, DEFAULT_SIDE_CONFIG);

    private TabulaVitaeRecipe cachedRecipe = null;
    private FlaskRecipe cachedFlaskRecipe = null;
    private int flaskSlot = -1;
    private boolean checkedSlaveInventory = false;

    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(8); }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (resource.isEmpty()) return true;
            if (index == OUTPUT_SLOT) return false;
            if (index == ORB_SLOT) return resource.value() instanceof BloodOrbItem;
            return true;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            if (index != OUTPUT_SLOT) {
                cachedRecipe = null;
                cachedFlaskRecipe = null;
                flaskSlot = -1;
            }
        }

        public ItemStack getStackInSlot(int slot) {
            ItemResource r = getResource(slot);
            return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            set(slot, ItemResource.of(stack), stack.getCount());
        }

        public int getSlots() { return size(); }

        public boolean isItemValid(int slot, ItemStack stack) {
            return isValid(slot, ItemResource.of(stack));
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemResource r = getResource(slot);
            if (r.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            try (Transaction tx = Transaction.openRoot()) {
                int extracted = extract(slot, r, amount, tx);
                if (extracted <= 0) return ItemStack.EMPTY;
                if (!simulate) tx.commit();
                return r.toStack(extracted);
            }
        }

        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = insert(slot, ItemResource.of(stack), stack.getCount(), tx);
                if (!simulate) tx.commit();
                if (inserted >= stack.getCount()) return ItemStack.EMPTY;
                ItemStack rem = stack.copy();
                rem.shrink(inserted);
                return rem;
            }
        }
    }

    public TabulaVitaeBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.TABULA_VITAE_TYPE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        direction = Direction.from3DDataValue(tag.getIntOr("direction", 0));
        isSlave = tag.getBooleanOr("isSlave", false);
        burnTime = tag.getIntOr("burnTime", 0);
        ticksRequired = tag.getIntOr("ticksRequired", 0);
        connectedPos = new BlockPos(tag.getIntOr("connectedX", 0), tag.getIntOr("connectedY", 0), tag.getIntOr("connectedZ", 0));
        tag.child("inventory").ifPresent(inv::deserialize);
        sideConfig.load(tag);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("direction", direction.get3DDataValue());
        tag.putBoolean("isSlave", isSlave);
        tag.putInt("burnTime", burnTime);
        tag.putInt("ticksRequired", ticksRequired);
        tag.putInt("connectedX", connectedPos.getX());
        tag.putInt("connectedY", connectedPos.getY());
        tag.putInt("connectedZ", connectedPos.getZ());
        inv.serialize(tag.child("inventory"));
        sideConfig.save(tag);
    }

    @Override
    public SlotSideConfig getSideConfig() {
        return sideConfig;
    }

    public void setInitialTableParameters(Direction direction, boolean isSlave, BlockPos connectedPos) {
        this.direction = direction;
        this.isSlave = isSlave;
        this.connectedPos = connectedPos;
        invalidateCapabilities();
        setChanged();
    }

    public boolean isSlave() {
        return isSlave;
    }

    @Nullable
    public TabulaVitaeBlockEntity getMaster() {
        if (!isSlave) return this;
        TabulaVitaeBlockEntity partner = getPartner();
        return partner != null && !partner.isSlave ? partner : null;
    }

    @Nullable
    private TabulaVitaeBlockEntity getPartner() {
        if (level == null || connectedPos.distManhattan(worldPosition) != 1 || !level.hasChunkAt(connectedPos)) {
            return null;
        }
        return level.getBlockEntity(connectedPos) instanceof TabulaVitaeBlockEntity partner ? partner : null;
    }

    public BlockPos getConnectedPos() {
        return connectedPos;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TabulaVitaeBlockEntity tile) {
        tile.tick();
    }

    public void tick() {
        if (level != null && level.isClientSide()) {
            if (!isSlave && burnTime > 0) {
                LoopSoundManager.tryStartLoop(
                        NVSounds.TABULA_VITAE_CRAFT.get(), 0.3f, level, worldPosition,
                        be -> be instanceof TabulaVitaeBlockEntity tv && !tv.isSlave && tv.burnTime > 0
                );
            }
            return;
        }
        if (level == null || level.isClientSide() || isSlave) return;

        if (!checkedSlaveInventory) {
            checkedSlaveInventory = true;
            reclaimSlaveInventory();
        }

        ItemStack orbStack = inv.getStackInSlot(ORB_SLOT);
        int orbTier = getOrbTier(orbStack);

        Optional<FlaskRecipe> flaskRecipeOpt = getFlaskRecipe();
        if (flaskRecipeOpt.isPresent()) {
            FlaskRecipe flaskRecipe = flaskRecipeOpt.get();
            ticksRequired = flaskRecipe.getTicks();

            if (orbTier < flaskRecipe.getMinimumTier()) {
                idleReason = IdleReason.TIER_TOO_LOW;
                burnTime = 0;
                return;
            }

            ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
            if (!currentOutput.isEmpty()) {
                idleReason = IdleReason.OUTPUT_BLOCKED;
                burnTime = 0;
                return;
            }

            if (!syphonEV(orbStack, flaskRecipe.getSyphon(), flaskRecipe.getTicks())) {
                return;
            }
            idleReason = IdleReason.NONE;

            if (burnTime == 0) {
                level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_ACTIVATE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            }
            burnTime++;

            if (burnTime % 5 == 0) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.01);
            }

            if (burnTime >= ticksRequired) {
                craftFlaskItem(flaskRecipe);
                burnTime = 0;
            }

            setChangedNoSync();
            return;
        }

        Optional<TabulaVitaeRecipe> recipeOpt = getRecipe();
        if (recipeOpt.isEmpty()) {
            idleReason = hasAnyInput() ? IdleReason.NO_RECIPE : IdleReason.NONE;
            burnTime = 0;
            return;
        }

        TabulaVitaeRecipe recipe = recipeOpt.get();
        ticksRequired = recipe.getTicks();

        if (orbTier < recipe.getMinimumTier()) {
            idleReason = IdleReason.TIER_TOO_LOW;
            burnTime = 0;
            return;
        }

        ItemStack output = recipe.getOutput();
        ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
        if (!currentOutput.isEmpty() && (!ItemStack.isSameItemSameComponents(currentOutput, output) || currentOutput.getCount() + output.getCount() > currentOutput.getMaxStackSize())) {
            idleReason = IdleReason.OUTPUT_BLOCKED;
            burnTime = 0;
            return;
        }

        if (!syphonEV(orbStack, recipe.getSyphon(), recipe.getTicks())) {
            return;
        }
        idleReason = IdleReason.NONE;

        if (burnTime == 0) {
            level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_ACTIVATE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        burnTime++;

        if (burnTime % 5 == 0) {
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.01);
        }

        double speedMultiplier = getInputCuttingFluidSpeed();
        if (burnTime * speedMultiplier >= ticksRequired) {
            craftItem(recipe);
            burnTime = 0;
        }

        setChangedNoSync();
    }

    private void reclaimSlaveInventory() {
        TabulaVitaeBlockEntity slave = getPartner();
        if (slave == null || !slave.isSlave) return;
        for (int i = 0; i < slave.inv.getSlots(); i++) {
            ItemStack stack = slave.inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            slave.inv.setStackInSlot(i, ItemStack.EMPTY);
            for (int target = 0; target < ORB_SLOT && !stack.isEmpty(); target++) {
                stack = inv.insertItem(target, stack, false);
            }
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), stack);
            }
        }
    }

    private boolean syphonEV(ItemStack orbStack, int totalSyphon, int totalTicks) {
        if (totalSyphon <= 0) return true;
        if (!(orbStack.getItem() instanceof BloodOrbItem)) {
            idleReason = IdleReason.NO_ORB;
            return false;
        }

        Binding binding = orbStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty()) {
            idleReason = IdleReason.ORB_UNBOUND;
            return false;
        }

        Anima network = AnimaHelper.getAnima(binding);
        if (network == null) {
            idleReason = IdleReason.NOT_ENOUGH_EV;
            return false;
        }

        int ticks = Math.max(1, totalTicks);
        int elapsed = Math.min(Math.max(burnTime, 0), ticks - 1);
        long drained = (long) totalSyphon * elapsed / ticks;
        int syphonThisTick = (int) ((long) totalSyphon * (elapsed + 1) / ticks - drained);
        if (syphonThisTick <= 0) return true;

        if (network.getCurrentEV() < syphonThisTick) {
            idleReason = IdleReason.NOT_ENOUGH_EV;
            return false;
        }
        if (network.syphon(AnimaTicket.create(syphonThisTick)) < syphonThisTick) {
            idleReason = IdleReason.NOT_ENOUGH_EV;
            return false;
        }
        return true;
    }

    private void craftItem(TabulaVitaeRecipe recipe) {
        int cuttingFluidSlot = -1;
        double bonusOutputChance = 0;
        for (int i = 0; i < ORB_SLOT; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(NVTags.Items.CUTTING_FLUIDS)) {
                cuttingFluidSlot = i;
                bonusOutputChance = Math.max(0, stack.getOrDefault(NVDataComponents.ARC_CHANCE.get(), 1.0) - 1.0);
                break;
            }
        }

        List<Ingredient> ingredients = new ArrayList<>(recipe.getInput());
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < ingredients.size(); j++) {
                if (ingredients.get(j).test(stack)) {
                    if (i == cuttingFluidSlot) {
                        damageCuttingFluid(i, stack);
                    } else {
                        consumeOne(i, stack);
                    }
                    ingredients.remove(j);
                    break;
                }
            }
        }

        ItemStack output = recipe.getOutput().copy();
        if (bonusOutputChance > 0 && Math.random() < bonusOutputChance) {
            output.grow(1);
        }
        ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            inv.setStackInSlot(OUTPUT_SLOT, output);
        } else {
            currentOutput.grow(output.getCount());
            inv.setStackInSlot(OUTPUT_SLOT, currentOutput);
        }

        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_COMPLETE.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0);
        }

        cachedRecipe = null;
        cachedFlaskRecipe = null;
        flaskSlot = -1;
    }

    private void craftFlaskItem(FlaskRecipe recipe) {
        int slot = flaskSlot;
        if (slot < 0) return;

        ItemStack flaskStack = inv.getStackInSlot(slot);
        List<EffectHolder> flaskEffects = ItemAlchemyFlask.getEffectHolders(flaskStack);

        ItemStack output = recipe.getOutput(flaskStack, flaskEffects);

        List<Ingredient> ingredients = new ArrayList<>(recipe.getInput());
        for (int i = 0; i < 6; i++) {
            if (i == slot) continue; // Skip the flask

            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < ingredients.size(); j++) {
                if (ingredients.get(j).test(stack)) {
                    consumeOne(i, stack);
                    ingredients.remove(j);
                    break;
                }
            }
        }

        inv.setStackInSlot(slot, ItemStack.EMPTY);
        inv.setStackInSlot(OUTPUT_SLOT, output);

        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_COMPLETE.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0);
        }

        cachedFlaskRecipe = null;
        flaskSlot = -1;
    }

    private Optional<FlaskRecipe> getFlaskRecipe() {
        int foundFlaskSlot = -1;
        ItemStack flaskStack = ItemStack.EMPTY;

        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof ItemAlchemyFlask) {
                foundFlaskSlot = i;
                flaskStack = stack;
                break;
            }
        }

        if (foundFlaskSlot < 0) {
            cachedFlaskRecipe = null;
            flaskSlot = -1;
            return Optional.empty();
        }

        if (cachedFlaskRecipe != null && flaskSlot == foundFlaskSlot) {
            FlaskInput input = createFlaskInput(foundFlaskSlot, flaskStack);
            if (cachedFlaskRecipe.matches(input, level)) {
                return Optional.of(cachedFlaskRecipe);
            }
        }

        FlaskInput input = createFlaskInput(foundFlaskSlot, flaskStack);
        List<EffectHolder> flaskEffects = ItemAlchemyFlask.getEffectHolders(flaskStack);

        FlaskRecipe bestRecipe = null;
        int bestPriority = Integer.MIN_VALUE;

        Collection<RecipeHolder<FlaskRecipe>> flaskHolders = level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().recipeMap().byType(NVRecipes.FLASK_TYPE.get())
                : Collections.emptyList();
        for (RecipeHolder<FlaskRecipe> holder : flaskHolders) {
            FlaskRecipe recipe = holder.value();
            if (recipe.matches(input, level)) {
                int priority = recipe.getPriority(flaskEffects);
                if (priority > bestPriority) {
                    bestPriority = priority;
                    bestRecipe = recipe;
                }
            }
        }

        if (bestRecipe != null) {
            cachedFlaskRecipe = bestRecipe;
            flaskSlot = foundFlaskSlot;
            return Optional.of(bestRecipe);
        }

        cachedFlaskRecipe = null;
        flaskSlot = -1;
        return Optional.empty();
    }

    private FlaskInput createFlaskInput(int flaskSlotIndex, ItemStack flaskStack) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (i == flaskSlotIndex) continue; // Skip the flask
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        List<EffectHolder> flaskEffects = ItemAlchemyFlask.getEffectHolders(flaskStack);
        return new FlaskInput(items, flaskStack, flaskEffects, getOrbTier(inv.getStackInSlot(ORB_SLOT)));
    }

    private Optional<TabulaVitaeRecipe> getRecipe() {
        if (cachedRecipe != null) {
            TabulaVitaeInput input = createInput();
            if (cachedRecipe.matches(input, level)) {
                return Optional.of(cachedRecipe);
            }
        }

        TabulaVitaeInput input = createInput();
        Optional<TabulaVitaeRecipe> recipe = level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipeFor(NVRecipes.TABULA_VITAE_TYPE.get(), input, serverLevel).map(RecipeHolder::value)
                : Optional.empty();

        recipe.ifPresent(r -> cachedRecipe = r);
        return recipe;
    }

    private boolean hasAnyInput() {
        for (int i = 0; i < ORB_SLOT; i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }
    private TabulaVitaeInput createInput() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        return new TabulaVitaeInput(items, getOrbTier(inv.getStackInSlot(ORB_SLOT)));
    }

    private int getOrbTier(ItemStack orbStack) {
        if (orbStack.getItem() instanceof BloodOrbItem orbItem) {
            return orbItem.getOrbTier(orbStack);
        }
        return 0;
    }

    public void dropItems() {
        if (level != null && !level.isClientSide()) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.tabula_vitae");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TabulaVitaeMenu(containerId, playerInventory, this);
    }

    public IdleReason getIdleReason() {
        return idleReason;
    }

    public void setIdleReasonFromNetwork(int index) {
        this.idleReason = IdleReason.byIndex(index);
    }
    public double getProgressForGui() {
        if (ticksRequired <= 0) return 0;
        return (burnTime * getInputCuttingFluidSpeed()) / (double) ticksRequired;
    }

    private double getInputCuttingFluidSpeed() {
        for (int i = 0; i < ORB_SLOT; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(NVTags.Items.CUTTING_FLUIDS)) {
                return stack.getOrDefault(NVDataComponents.ARC_SPEED.get(), 1.0);
            }
        }
        return 1.0;
    }

    private void damageCuttingFluid(int slot, ItemStack stack) {
        if (stack.has(DataComponents.MAX_DAMAGE)) {
            int newDamage = stack.getOrDefault(DataComponents.DAMAGE, 0) + 1;
            if (newDamage >= stack.getMaxDamage()) {
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                stack.set(DataComponents.DAMAGE, newDamage);
                inv.setStackInSlot(slot, stack);
            }
        } else {
            consumeOne(slot, stack);
        }
    }

    private void consumeOne(int slot, ItemStack stack) {
        ItemStackTemplate remainder = stack.getCraftingRemainder();
        ItemStack container = remainder != null ? remainder.create() : ItemStack.EMPTY;
        stack.shrink(1);
        if (!stack.isEmpty()) {
            inv.setStackInSlot(slot, stack);
        } else {
            inv.setStackInSlot(slot, container);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        dropItems();
        super.preRemoveSideEffects(pos, state);
    }

}
