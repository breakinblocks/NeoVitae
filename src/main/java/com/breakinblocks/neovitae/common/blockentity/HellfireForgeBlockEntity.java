package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.sideconfig.OutputFaceConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.OutputPusher;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigResourceHandler;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HellfireForgeBlockEntity extends BaseBlockEntity implements MenuProvider, SideConfigurable, OutputFaceConfigurable, IRitualAccelerated {
    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(SLOT_COUNT); }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index == OUTPUT_SLOT) return false;
            if (index == GEM_SLOT && !resource.has(NVDataComponents.SPIRITUS_AMOUNT)) return false;
            return true;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (!absorbing) {
                idleThrottle.wake();
            }
        }

        public ItemStack getStackInSlot(int slot) {
            ItemResource r = getResource(slot);
            return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    // one day mojang is going to change Direction. But not today
    public static final int SOUTH = Direction.SOUTH.get2DDataValue(); // 0
    public static final int WEST = Direction.WEST.get2DDataValue(); // 1
    public static final int NORTH = Direction.NORTH.get2DDataValue(); // 2
    public static final int EAST = Direction.EAST.get2DDataValue(); // 3

    public static final int GEM_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;
    public static final int SLOT_COUNT = 6;

    private static final boolean[][] DEFAULT_SIDE_CONFIG = new boolean[][] {
            { false, false, false, true, false, false },
            { false, false, false, false, true, false },
            { false, false, true, false, false, false },
            { false, false, false, false, false, true },
            { false, true, false, false, false, false },
            { true, false, false, false, false, false }
    };

    private final SlotSideConfig sideConfig = new SlotSideConfig(SLOT_COUNT, DEFAULT_SIDE_CONFIG);
    public int activeSlot = -1;
    @Nullable
    private Direction outputFace = null;

    public static final int MAX_PROGRESS = 100;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_STATUS = 1;
    public static final int DATA_REQUIRED_SPIRITUS = 2;
    public static final int DATA_STORED_SPIRITUS = 3;
    public static final int DATA_COUNT = 4;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_CRAFTING = 1;
    public static final int STATUS_NEEDS_SPIRITUS = 2;

    protected int progress = 0;
    private final RitualAcceleration ritualAcceleration = new RitualAcceleration();
    private final IdleThrottle idleThrottle = new IdleThrottle();
    private boolean absorbing = false;
    private final RecipeManager.CachedCheck<ForgeInput, ForgeRecipe> recipeCheck = RecipeManager.createCheck(NVRecipes.HELLFIRE_FORGE_TYPE.get());
    private final RecipeManager.CachedCheck<ForgeInput, ForgeRecipe> craftingGemRecipeCheck = RecipeManager.createCheck(NVRecipes.HELLFIRE_FORGE_TYPE.get());
    protected int status = STATUS_IDLE;
    protected int requiredSpiritus = 0;
    protected int storedSpiritus = 0;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> progress;
                case DATA_STATUS -> status;
                case DATA_REQUIRED_SPIRITUS -> requiredSpiritus;
                case DATA_STORED_SPIRITUS -> storedSpiritus;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> progress = value;
                case DATA_STATUS -> status = value;
                case DATA_REQUIRED_SPIRITUS -> requiredSpiritus = value;
                case DATA_STORED_SPIRITUS -> storedSpiritus = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public HellfireForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(NVTiles.HELLFIRE_FORGE_TYPE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HellfireForgeBlockEntity tile) {
        if (level.isClientSide()) {
            if (tile.progress > 0) {
                LoopSoundManager.tryStartLoop(
                        NVSounds.HELLFIRE_FORGE_AMBIENT.get(), 0.2f, level, pos,
                        be -> be instanceof HellfireForgeBlockEntity forge && forge.progress > 0
                );
            }
            return;
        }

        absorbSpiritusFromChunk(level, pos, tile);

        if (OutputPusher.shouldPush(level)) {
            OutputPusher.push(level, pos, tile.outputFace, tile.inv, OUTPUT_SLOT, OUTPUT_SLOT);
        }

        if (tile.idleThrottle.shouldSkip()) {
            return;
        }
        tile.idleThrottle.rest();

        ForgeInput input = tile.getInput();
        Optional<RecipeHolder<ForgeRecipe>> recipeOptional = findRecipe(level, input, tile.recipeCheck);

        if (recipeOptional.isEmpty() || !recipeOptional.get().value().hasEnoughSpiritus(input)) {
            ForgeInput craftingGemInput = tile.getCraftingGemInput();
            if (craftingGemInput.getGemIndex() != input.getGemIndex()) {
                Optional<RecipeHolder<ForgeRecipe>> fallback = findRecipe(level, craftingGemInput, tile.craftingGemRecipeCheck);
                if (fallback.isPresent() && fallback.get().value().hasEnoughSpiritus(craftingGemInput)) {
                    input = craftingGemInput;
                    recipeOptional = fallback;
                }
            }
        }

        if (recipeOptional.isEmpty()) {
            tile.abortCraft(STATUS_IDLE, 0, 0);
            return;
        }

        ForgeRecipe recipe = recipeOptional.get().value();
        if (!recipe.hasEnoughSpiritus(input)) {
            tile.idleThrottle.wake();
            tile.abortCraft(STATUS_NEEDS_SPIRITUS, recipe.minSpiritus,
                    input.getGem().getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D));
            return;
        }

        ItemStack output = recipe.assemble(input);
        if (output.isEmpty()) {
            tile.abortCraft(STATUS_IDLE, 0, 0);
            return;
        }

        ItemStack currentOutput = tile.inv.getStackInSlot(OUTPUT_SLOT);
        if (!currentOutput.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, output) ||
                    currentOutput.getCount() + output.getCount() > currentOutput.getMaxStackSize()) {
                tile.abortCraft(STATUS_IDLE, 0, 0);
                return;
            }
        }

        tile.idleThrottle.wake();
        tile.status = STATUS_CRAFTING;
        if (tile.progress == 0) {
            level.playSound(null, pos, NVSounds.HELLFIRE_FORGE_CRAFT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        tile.progress += tile.ritualAcceleration.factor(level);
        tile.ritualAcceleration.markWorked(level);
        if (tile.progress < MAX_PROGRESS) {
            if (level.getGameTime() % 4 == 0) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xFF4400), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 2, 0.1, 0, 0.1, 0.02);
            }
            return;
        }

        level.playSound(null, pos, NVSounds.HELLFIRE_FORGE_COMPLETE.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFF4400), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0);

        NeoVitaeCraftedEvent.Forge event = new NeoVitaeCraftedEvent.Forge(output, input.asArray());
        NeoForge.EVENT_BUS.post(event);

        if (input.getGemIndex() == GEM_SLOT) {
            ItemStack gemStack = tile.inv.getStackInSlot(GEM_SLOT);
            if (!gemStack.isEmpty()) {
                double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                will -= recipe.usedSpiritus;
                if (will <= 0 && gemStack.is(NVItems.RAW_SPIRITUS)) {
                    tile.inv.setStackInSlot(GEM_SLOT, ItemStack.EMPTY);
                } else {
                    gemStack.set(NVDataComponents.SPIRITUS_AMOUNT, Math.max(0, will));
                    tile.inv.setStackInSlot(GEM_SLOT, gemStack);
                }
            }
        }

        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack item = tile.inv.getStackInSlot(i);
            if (item.isEmpty()) {
                continue;
            }
            var template = item.getCraftingRemainder();
            ItemStack remainder = template != null ? template.create() : ItemStack.EMPTY;
            if (!remainder.isEmpty()) {
                tile.inv.setStackInSlot(i, remainder);
                continue;
            }
            item.shrink(1);
            tile.inv.setStackInSlot(i, item);
        }

        if (currentOutput.isEmpty()) {
            tile.inv.setStackInSlot(OUTPUT_SLOT, event.getOutput());
        } else {
            currentOutput.grow(event.getOutput().getCount());
            tile.inv.setStackInSlot(OUTPUT_SLOT, currentOutput);
        }

        tile.progress = 0;
        tile.status = STATUS_IDLE;
        tile.setChanged();
    }

    private static Optional<RecipeHolder<ForgeRecipe>> findRecipe(Level level, ForgeInput input,
                                                                 RecipeManager.CachedCheck<ForgeInput, ForgeRecipe> check) {
        return level instanceof ServerLevel serverLevel ? check.getRecipeFor(input, serverLevel) : Optional.empty();
    }

    private void abortCraft(int newStatus, double required, double stored) {
        boolean wasCrafting = progress > 0;
        progress = 0;
        status = newStatus;
        requiredSpiritus = (int) Math.ceil(required);
        storedSpiritus = (int) Math.floor(stored);
        if (wasCrafting) {
            setChanged();
        }
    }

    private static void absorbSpiritusFromChunk(Level level, BlockPos pos, HellfireForgeBlockEntity tile) {
        ItemStack gemStack = tile.inv.getStackInSlot(GEM_SLOT);
        if (absorbIntoGem(level, pos, gemStack)) {
            tile.absorbing = true;
            tile.inv.setStackInSlot(GEM_SLOT, gemStack);
            tile.absorbing = false;
        }
    }

    public static boolean absorbIntoGem(Level level, BlockPos pos, ItemStack gemStack) {
        if (gemStack.isEmpty() || !SpiritusHelper.isRechargeable(gemStack)) {
            return false;
        }
        boolean changed = false;
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkSpiritus = WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);
            if (currentChunkSpiritus <= 0) continue;

            double fillAmount = Math.min(VasMaleficumBlockEntity.GEM_DRAIN_RATE, currentChunkSpiritus);
            double canFill = SpiritusHelper.fillSpiritus(gemStack, type, fillAmount, false);
            if (canFill > 0) {
                double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, type, canFill);
                if (drained > 0) {
                    SpiritusHelper.fillSpiritus(gemStack, type, drained, true);
                    changed = true;
                }
            }
        }
        return changed;
    }

    public ForgeInput getInput() {
        return buildInput(true);
    }

    public ForgeInput getCraftingGemInput() {
        return buildInput(false);
    }

    private ForgeInput buildInput(boolean preferGemSlot) {
        ItemStack fuelGem = inv.getStackInSlot(GEM_SLOT);
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack craftingGem = ItemStack.EMPTY;
        int craftingGemIndex = -1;
        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            stacks.add(testStack);
            if (testStack.is(NVTags.Items.SPIRITUS_GEM)) {
                craftingGem = testStack;
                craftingGemIndex = i;
            }
        }

        if (preferGemSlot && !fuelGem.isEmpty()) {
            return new ForgeInput(stacks, fuelGem, GEM_SLOT);
        }
        if (craftingGemIndex >= 0) {
            return new ForgeInput(stacks, craftingGem, craftingGemIndex);
        }
        return new ForgeInput(stacks, fuelGem, GEM_SLOT);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.child("inventory").ifPresent(inv::deserialize);
        progress = tag.getIntOr("progress", 0);
        sideConfig.load(tag);
        outputFace = OutputPusher.load(tag);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        inv.serialize(tag.child("inventory"));
        tag.putInt("progress", progress);
        sideConfig.save(tag);
        OutputPusher.save(tag, outputFace);
    }

    @Override
    public SlotSideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    @Nullable
    public Direction getOutputFace() {
        return outputFace;
    }

    @Override
    public void setOutputFace(@Nullable Direction face) {
        outputFace = face;
    }

    public @Nullable ResourceHandler<ItemResource> getInventory(@Nullable Direction side) {
        if (side == null) {
            return inv;
        }
        return new SideConfigResourceHandler<>(inv, sideConfig, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.hellfire_forge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HellfireForgeMenu(containerId, playerInventory, this);
    }

    public int getProgress() { return progress; }

    public double getProgressForGui() {
        return (double) progress / (double) MAX_PROGRESS;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) {
            BlockEntityHelper.dropContents(this.level, pos, inv);
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }

    @Override
    public RitualAcceleration getRitualAcceleration() {
        return ritualAcceleration;
    }
}
