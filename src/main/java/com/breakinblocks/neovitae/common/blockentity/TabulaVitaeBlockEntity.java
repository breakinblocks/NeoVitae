package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
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
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import net.minecraft.world.item.crafting.RecipeHolder;

public class TabulaVitaeBlockEntity extends BaseBlockEntity implements MenuProvider {
    public static final int ORB_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;

    public Direction direction = Direction.NORTH;
    public boolean isSlave = false;
    public int burnTime = 0;
    public int ticksRequired = 1;
    public BlockPos connectedPos = BlockPos.ZERO;

    private TabulaVitaeRecipe cachedRecipe = null;
    private FlaskRecipe cachedFlaskRecipe = null;
    private int flaskSlot = -1; // Slot containing the flask for flask recipes

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
    }

    public void setInitialTableParameters(Direction direction, boolean isSlave, BlockPos connectedPos) {
        this.direction = direction;
        this.isSlave = isSlave;
        this.connectedPos = connectedPos;
        setChanged();
    }

    public boolean isSlave() {
        return isSlave;
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
                com.breakinblocks.neovitae.client.sound.LoopSoundManager.tryStartLoop(
                        NVSounds.TABULA_VITAE_CRAFT.get(), 0.3f, level, worldPosition,
                        be -> be instanceof TabulaVitaeBlockEntity tv && !tv.isSlave && tv.burnTime > 0
                );
            }
            return;
        }
        if (level == null || level.isClientSide() || isSlave) return;

        ItemStack orbStack = inv.getStackInSlot(ORB_SLOT);
        int orbTier = getOrbTier(orbStack);

        Optional<FlaskRecipe> flaskRecipeOpt = getFlaskRecipe();
        if (flaskRecipeOpt.isPresent()) {
            FlaskRecipe flaskRecipe = flaskRecipeOpt.get();
            ticksRequired = flaskRecipe.getTicks();

            if (orbTier < flaskRecipe.getMinimumTier()) {
                burnTime = 0;
                return;
            }

            ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
            if (!currentOutput.isEmpty()) {
                burnTime = 0;
                return;
            }

            if (!syphonEV(orbStack, flaskRecipe.getSyphon(), flaskRecipe.getTicks())) {
                return;
            }

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

            setChanged();
            return;
        }

        Optional<TabulaVitaeRecipe> recipeOpt = getRecipe();
        if (recipeOpt.isEmpty()) {
            burnTime = 0;
            return;
        }

        TabulaVitaeRecipe recipe = recipeOpt.get();
        ticksRequired = recipe.getTicks();

        if (orbTier < recipe.getMinimumTier()) {
            burnTime = 0;
            return;
        }

        ItemStack output = recipe.getOutput();
        ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
        if (!currentOutput.isEmpty() && (!ItemStack.isSameItemSameComponents(currentOutput, output) || currentOutput.getCount() + output.getCount() > currentOutput.getMaxStackSize())) {
            burnTime = 0;
            return;
        }

        if (!syphonEV(orbStack, recipe.getSyphon(), recipe.getTicks())) {
            return;
        }

        if (burnTime == 0) {
            level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_ACTIVATE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        burnTime++;

        if (burnTime % 5 == 0) {
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.01);
        }

        if (burnTime >= ticksRequired) {
            craftItem(recipe);
            burnTime = 0;
        }

        setChanged();
    }

    private boolean syphonEV(ItemStack orbStack, int totalSyphon, int totalTicks) {
        int syphonPerTick = totalSyphon / Math.max(1, totalTicks);
        if (syphonPerTick > 0 && orbStack.getItem() instanceof BloodOrbItem) {
            Binding binding = orbStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
            if (!binding.isEmpty()) {
                Anima network = AnimaHelper.getAnima(binding);
                if (network != null) {
                    int syphoned = network.syphon(AnimaTicket.create(syphonPerTick));
                    if (syphoned < syphonPerTick) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void craftItem(TabulaVitaeRecipe recipe) {
        List<Ingredient> ingredients = new ArrayList<>(recipe.getInput());
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < ingredients.size(); j++) {
                if (ingredients.get(j).test(stack)) {
                    ItemStack container = stack.getCraftingRemainder().create();
                    stack.shrink(1);
                    if (stack.isEmpty() && !container.isEmpty()) {
                        inv.setStackInSlot(i, container);
                    }
                    ingredients.remove(j);
                    break;
                }
            }
        }

        ItemStack output = recipe.getOutput().copy();
        ItemStack currentOutput = inv.getStackInSlot(OUTPUT_SLOT);
        if (currentOutput.isEmpty()) {
            inv.setStackInSlot(OUTPUT_SLOT, output);
        } else {
            currentOutput.grow(output.getCount());
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
        if (flaskSlot < 0) return;

        ItemStack flaskStack = inv.getStackInSlot(flaskSlot);
        List<EffectHolder> flaskEffects = ItemAlchemyFlask.getEffectHolders(flaskStack);

        ItemStack output = recipe.getOutput(flaskStack, flaskEffects);

        List<Ingredient> ingredients = new ArrayList<>(recipe.getInput());
        for (int i = 0; i < 6; i++) {
            if (i == flaskSlot) continue; // Skip the flask

            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < ingredients.size(); j++) {
                if (ingredients.get(j).test(stack)) {
                    ItemStack container = stack.getCraftingRemainder().create();
                    stack.shrink(1);
                    if (stack.isEmpty() && !container.isEmpty()) {
                        inv.setStackInSlot(i, container);
                    }
                    ingredients.remove(j);
                    break;
                }
            }
        }

        inv.setStackInSlot(flaskSlot, ItemStack.EMPTY);
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
        if (level != null && !level.isClientSide() && !isSlave) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
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

    public double getProgressForGui() {
        if (ticksRequired <= 0) return 0;
        return (double) burnTime / (double) ticksRequired;
    }
}
