package com.breakinblocks.neovitae.common.blockentity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.common.block.AthanorBlock;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipeInput;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.AthanorOutputHandler;
import net.minecraft.sounds.SoundSource;

import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AthanorBlockEntity extends BaseBlockEntity implements MenuProvider {

    public static final int TOOL_SLOT = 0;
    public static final int INPUT_START = 1;
    public static final int NUM_INPUTS = 6;
    public static final int INPUT_BUCKET_SLOT = INPUT_START + NUM_INPUTS; // 7
    public static final int OUTPUT_BUCKET_SLOT = INPUT_BUCKET_SLOT + 1;   // 8
    public static final int OUTPUT_SLOT = OUTPUT_BUCKET_SLOT + 1;         // 9

    public static final int NUM_OUTPUTS = 5;

    private double progress = 0;
    public static final double DEFAULT_SPEED = 0.005;

    private Map<SpiritusType, Double> currentRecipeWillCost = Map.of();
    private final double[] chunkWill = new double[SpiritusType.values().length];
    private final double[] chunkWillMax = new double[SpiritusType.values().length];
    private boolean willBlocked = false;

    private final List<ItemStack> tempBucketList = new ArrayList<>(1);

    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickSmelting;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickBlasting;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickSmoking;
    private final RecipeManager.CachedCheck<AthanorRecipeInput, AthanorRecipe> quickAthanor;

    public AthanorBlockEntity(BlockPos pos, BlockState blockState) {
        super(NVTiles.ATHANOR_TYPE.get(), pos, blockState);
        quickSmelting = createCookingLookup(RecipeType.SMELTING);
        quickBlasting = createCookingLookup(RecipeType.BLASTING);
        quickSmoking = createCookingLookup(RecipeType.SMOKING);
        quickAthanor = RecipeManager.createCheck(NVRecipes.ATHANOR_TYPE.get());
    }

    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> createCookingLookup(RecipeType<? extends AbstractCookingRecipe> recipeType) {
        return RecipeManager.createCheck((RecipeType<AbstractCookingRecipe>) recipeType);
    }

    public final ItemStackHandler athanorInv = new ItemStackHandler(OUTPUT_SLOT + NUM_OUTPUTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == TOOL_SLOT) return stack.is(NVTags.Items.ATHANOR_TOOL);
            if (slot >= INPUT_START && slot < INPUT_START + NUM_INPUTS) return true;
            if (slot == INPUT_BUCKET_SLOT || slot == OUTPUT_BUCKET_SLOT) return FluidUtil.getFluidHandler(stack).isPresent();
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == INPUT_BUCKET_SLOT || slot == OUTPUT_BUCKET_SLOT) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }
    };

    public int getProgressForGui() {
        return (int) (progress * 38);
    }

    public Map<SpiritusType, Double> getCurrentRecipeWillCost() {
        return currentRecipeWillCost;
    }

    public double getChunkWill(SpiritusType type) {
        return chunkWill[type.ordinal()];
    }

    public double getChunkWillMax(SpiritusType type) {
        return chunkWillMax[type.ordinal()];
    }

    public boolean isWillBlocked() {
        return willBlocked;
    }

    public static IItemHandler getItemHandler(AthanorBlockEntity tile, @Nullable Direction side) {
        if (side == null) {
            return tile.athanorInv;
        }
        return switch (side) {
            case UP -> new RangedWrapper(tile.athanorInv, TOOL_SLOT, TOOL_SLOT + 1);
            case DOWN -> new RangedWrapper(tile.athanorInv, OUTPUT_SLOT, OUTPUT_SLOT + NUM_OUTPUTS);
            default -> new RangedWrapper(tile.athanorInv, INPUT_START, OUTPUT_BUCKET_SLOT + 1);
        };
    }

    public final FluidTank inputTank = new FluidTank(20 * FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public final FluidTank outputTank = new FluidTank(20 * FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    @Override
    public Component getDisplayName() {
        return Component.literal("Athanor");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        CompoundTag inv = tag.getCompound("arcinv");
        athanorInv.deserializeNBT(registries, inv);
        if (athanorInv.getSlots() < OUTPUT_SLOT + NUM_OUTPUTS) {
            athanorInv.setSize(OUTPUT_SLOT + NUM_OUTPUTS);
        }
        inputTank.readFromNBT(registries, tag.getCompound("inputtank"));
        outputTank.readFromNBT(registries, tag.getCompound("outputtank"));
        progress = tag.getDouble("arcprogress");
        willBlocked = tag.getBoolean("willBlocked");
        if (tag.contains("chunkWill")) {
            CompoundTag willTag = tag.getCompound("chunkWill");
            for (SpiritusType type : SpiritusType.values()) {
                chunkWill[type.ordinal()] = willTag.getDouble(type.getSerializedName());
                chunkWillMax[type.ordinal()] = willTag.getDouble(type.getSerializedName() + "_max");
            }
        }
        if (tag.contains("recipeWillCost")) {
            CompoundTag costTag = tag.getCompound("recipeWillCost");
            EnumMap<SpiritusType, Double> costs = new EnumMap<>(SpiritusType.class);
            for (SpiritusType type : SpiritusType.values()) {
                if (costTag.contains(type.getSerializedName())) {
                    costs.put(type, costTag.getDouble(type.getSerializedName()));
                }
            }
            currentRecipeWillCost = Map.copyOf(costs);
        } else {
            currentRecipeWillCost = Map.of();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag inv = athanorInv.serializeNBT(registries);
        tag.put("arcinv", inv);
        CompoundTag input = new CompoundTag();
        CompoundTag output = new CompoundTag();
        inputTank.writeToNBT(registries, input);
        outputTank.writeToNBT(registries, output);
        tag.put("inputtank", input);
        tag.put("outputtank", output);
        tag.putDouble("arcprogress", progress);
        tag.putBoolean("willBlocked", willBlocked);
        CompoundTag willTag = new CompoundTag();
        for (SpiritusType type : SpiritusType.values()) {
            willTag.putDouble(type.getSerializedName(), chunkWill[type.ordinal()]);
            willTag.putDouble(type.getSerializedName() + "_max", chunkWillMax[type.ordinal()]);
        }
        tag.put("chunkWill", willTag);
        if (!currentRecipeWillCost.isEmpty()) {
            CompoundTag costTag = new CompoundTag();
            currentRecipeWillCost.forEach((type, amount) -> costTag.putDouble(type.getSerializedName(), amount));
            tag.put("recipeWillCost", costTag);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AthanorMenu(containerId, playerInventory, this);
    }

    public IFluidHandler getFluidHandler(Direction direction) {
        if (direction == Direction.DOWN) {
            return this.outputTank;
        }
        return this.inputTank;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AthanorBlockEntity athanorTile) {
        if (level.isClientSide()) {
            if (athanorTile.progress > 0) {
                com.breakinblocks.neovitae.client.sound.LoopSoundManager.tryStartLoop(
                        NVSounds.ATHANOR_BUBBLE.get(), 0.3f, level, blockPos,
                        be -> be instanceof AthanorBlockEntity athanor && athanor.progress > 0
                );
            }
            return;
        }

        ItemStack[] outputItems = {
                athanorTile.athanorInv.getStackInSlot(OUTPUT_SLOT),
                athanorTile.athanorInv.getStackInSlot(OUTPUT_SLOT + 1),
                athanorTile.athanorInv.getStackInSlot(OUTPUT_SLOT + 2),
                athanorTile.athanorInv.getStackInSlot(OUTPUT_SLOT + 3),
                athanorTile.athanorInv.getStackInSlot(OUTPUT_SLOT + 4)
        };
        AthanorOutputHandler itemOutputHandler = new AthanorOutputHandler(outputItems, 64);
        boolean outputChanged = athanorTile.handleSlots(itemOutputHandler);
        athanorTile.updateType();

        if (level.getGameTime() % 10 == 0) {
            athanorTile.snapshotChunkWill(level, blockPos);
        }
        ItemStack toolStack = athanorTile.athanorInv.getStackInSlot(TOOL_SLOT);
        ItemStack[] inputStacks = new ItemStack[NUM_INPUTS];
        for (int s = 0; s < NUM_INPUTS; s++) {
            inputStacks[s] = athanorTile.athanorInv.getStackInSlot(INPUT_START + s);
        }
        double rawWill = WorldSpiritusHandler.getCurrentWill(level, blockPos, SpiritusType.DEFAULT);
        double willSpeedMod = 0.5 + 1.5 * Math.min(1.0, rawWill / 100.0);
        boolean didProgress = false;
        if (toolStack.is(NVTags.Items.ATHANOR_TOOL)) {
            if (toolStack.is(NVTags.Items.ATHANOR_FURNACE)) {
                Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = Optional.empty();
                SingleRecipeInput input = new SingleRecipeInput(inputStacks[0]);
                if (toolStack.is(NVTags.Items.ARC_SMELTING)) {
                     recipe = athanorTile.quickSmelting.getRecipeFor(input, level);
                } else if (toolStack.is(NVTags.Items.ARC_BLASTING)) {
                    recipe = athanorTile.quickBlasting.getRecipeFor(input, level);
                } else if (toolStack.is(NVTags.Items.ARC_SMOKING)) {
                    recipe = athanorTile.quickSmoking.getRecipeFor(input, level);
                }
                if (athanorTile.canCraftFurnace(recipe, itemOutputHandler)) {
                    athanorTile.progress += DEFAULT_SPEED * ((double) recipe.get().value().getCookingTime() / 200D) * toolStack.getOrDefault(NVDataComponents.ARC_SPEED, 1D) * willSpeedMod;
                    didProgress = true;
                    if (athanorTile.progress >= 1) {
                        athanorTile.craftFurnace(recipe.get().value(), input, itemOutputHandler);
                        outputChanged = true;
                    }
                }
            } else {
                AthanorRecipeInput input = new AthanorRecipeInput(toolStack, inputStacks, athanorTile.inputTank.getFluidInTank(0));
                Optional<RecipeHolder<AthanorRecipe>> recipe = athanorTile.quickAthanor.getRecipeFor(input, level);
                if (athanorTile.canCraft(recipe, itemOutputHandler)) {
                    AthanorRecipe athanorRecipe = recipe.get().value();
                    athanorTile.currentRecipeWillCost = athanorRecipe.getSpiritusCosts();

                    if (athanorRecipe.hasSpiritusCosts()) {
                        if (!athanorTile.hasEnoughWill(level, blockPos, athanorRecipe)) {
                            athanorTile.willBlocked = true;
                        } else {
                            athanorTile.willBlocked = false;
                            athanorTile.progress += DEFAULT_SPEED * toolStack.getOrDefault(NVDataComponents.ARC_SPEED, 1D) * willSpeedMod;
                            didProgress = true;
                        }
                    } else {
                        athanorTile.willBlocked = false;
                        athanorTile.progress += DEFAULT_SPEED * toolStack.getOrDefault(NVDataComponents.ARC_SPEED, 1D) * willSpeedMod;
                        didProgress = true;
                    }

                    if (athanorTile.progress >= 1) {
                        if (athanorRecipe.hasSpiritusCosts()) {
                            athanorTile.drainWillCosts(level, blockPos, athanorRecipe);
                            athanorTile.snapshotChunkWill(level, blockPos);
                        }
                        athanorTile.craft(athanorRecipe, input, itemOutputHandler);
                        outputChanged = true;
                    }
                } else {
                    athanorTile.currentRecipeWillCost = Map.of();
                    athanorTile.willBlocked = false;
                }
            }
        } else {
            athanorTile.currentRecipeWillCost = Map.of();
            athanorTile.willBlocked = false;
        }

        if (didProgress && level.getGameTime() % 6 == 0) {
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x22AA22), blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5, 2, 0.1, 0.0, 0.1, 0.02);
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_BUBBLE.get(), 0x22AA22), blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5, 1, 0.15, 0.0, 0.15, 0);
        }

        if (didProgress && level.getGameTime() % 20 == 0 && level.random.nextFloat() < 0.05f) {
            WorldSpiritusHandler.drainWillFromChunk(level, blockPos, SpiritusType.DEFAULT, 1.0);
        }

        if (athanorTile.willBlocked && level.getGameTime() % 10 == 0) {
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x880022),
                    blockPos.getX() + 0.5, blockPos.getY() + 1.1, blockPos.getZ() + 0.5,
                    3, 0.15, 0.0, 0.15, 0.01);
        }

        athanorTile.setLit(didProgress);
        if (!didProgress && !athanorTile.willBlocked) {
            athanorTile.progress = 0;
        }

        if (outputChanged) {
            for (int i = 0; i < NUM_OUTPUTS; i++) {
                athanorTile.athanorInv.setStackInSlot(OUTPUT_SLOT + i, itemOutputHandler.getStackInSlot(i));
            }
        }
    }


    private boolean canCraftFurnace(Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe, AthanorOutputHandler outputHandler) {
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
        return outputHandler.canTransferAllItemsToSlots(List.of(result), true);
    }

    private void craftFurnace(AbstractCookingRecipe value, SingleRecipeInput input, AthanorOutputHandler outputHandler) {
        ItemStack output = value.assemble(input, level.registryAccess());
        handleInventory(List.of(output), outputHandler);
    }

    private boolean canCraft(Optional<RecipeHolder<AthanorRecipe>> recipe, AthanorOutputHandler outputHandler) {
        if (recipe.isEmpty()) {
            return false;
        }
        AthanorRecipe athanorRecipe = recipe.get().value();
        List<Pair<ItemStack, Double>> chanceOutputs = athanorRecipe.getAllListedOutputs();
        List<ItemStack> outputs = chanceOutputs.stream().map(Pair::getFirst).toList();
        if (!outputHandler.canTransferAllItemsToSlots(outputs, true)) {
            return false;
        }
        if (athanorRecipe.getOutputFluid().isPresent()) {
            int filled = outputTank.fill(athanorRecipe.getOutputFluid().get(), FluidAction.SIMULATE);
            if (!(filled == athanorRecipe.getOutputFluid().get().getAmount())) {
                return false;
            }
        }
        return true;
    }

    private void craft(AthanorRecipe value, AthanorRecipeInput input, AthanorOutputHandler outputHandler) {
        value.assemble(input, level.registryAccess());
        List<ItemStack> outputs = value.getActualOutputs();
        value.getInputFluid().ifPresent(required ->
                inputTank.drain(required.getAmount(), FluidAction.EXECUTE));
        int filled = outputTank.fill(value.getActualOutputFluid(), FluidAction.EXECUTE);
        handleInventory(outputs, outputHandler);
    }

    private void handleInventory(List<ItemStack> toOutput, AthanorOutputHandler outputHandler) {
        if (!outputHandler.canTransferAllItemsToSlots(toOutput, false)) {
            // Debug: NeoVitae.LOGGER.info("couldnt stash all {}", toOutput);
        }
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, NVSounds.ATHANOR_COMPLETE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x22AA22), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0);
        }
        for (int s = INPUT_START; s < INPUT_START + NUM_INPUTS; s++) {
            ItemStack inSlot = athanorInv.getStackInSlot(s);
            if (!inSlot.isEmpty()) {
                inSlot.shrink(1);
            }
        }
        progress = 0;

        ItemStack toolStack = athanorInv.getStackInSlot(TOOL_SLOT);
        if (!toolStack.has(DataComponents.UNBREAKABLE)) {
            if (toolStack.hasCraftingRemainingItem()) {
                athanorInv.setStackInSlot(TOOL_SLOT, toolStack.getCraftingRemainingItem());
            } else if (toolStack.has(DataComponents.MAX_DAMAGE)) {
                int lost = EnchantmentHelper.processDurabilityChange((ServerLevel) level, toolStack, 1); // this *should* apply enchantments like unbreaking
                int newDamage = toolStack.getOrDefault(DataComponents.DAMAGE, 0) + lost;
                if (newDamage >= toolStack.getMaxDamage()) {
                    // Tool is broken - clear the slot (handleSlots will move it to output if possible)
                    athanorInv.setStackInSlot(TOOL_SLOT, ItemStack.EMPTY);
                } else {
                    toolStack.set(DataComponents.DAMAGE, newDamage);
                }
            } else {
                toolStack.shrink(1);
            }
        }
    }

    public void setLit(boolean lit) {
        if (!getBlockState().getValue(AthanorBlock.LIT)) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState().setValue(AthanorBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    public void updateType() {
        SpiritusType type = athanorInv.getStackInSlot(TOOL_SLOT).getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT);
        if (getBlockState().getValue(AthanorBlock.TYPE) != type) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState().setValue(AthanorBlock.TYPE, type), Block.UPDATE_ALL);
        }
    }

    public boolean handleSlots(AthanorOutputHandler itemOutputHandler) {
        IFluidHandlerItem testInputHandler = FluidUtil.getFluidHandler(athanorInv.getStackInSlot(INPUT_BUCKET_SLOT).copy()).orElse(null);
        IFluidHandlerItem testOutputHandler = FluidUtil.getFluidHandler(athanorInv.getStackInSlot(OUTPUT_BUCKET_SLOT).copy()).orElse(null);

        boolean outputChanged = false;
        if (testInputHandler != null) {
            FluidStack transferredStack = FluidUtil.tryFluidTransfer(inputTank, testInputHandler, Integer.MAX_VALUE, false);
            if (!transferredStack.isEmpty()) {
                testInputHandler.drain(transferredStack, FluidAction.EXECUTE);
                tempBucketList.clear();
                tempBucketList.add(testInputHandler.getContainer());
                if (itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                    outputChanged = true;
                    inputTank.fill(transferredStack, FluidAction.EXECUTE);
                    itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
                    athanorInv.setStackInSlot(INPUT_BUCKET_SLOT, ItemStack.EMPTY);
                }
            } else {
                transferredStack = FluidUtil.tryFluidTransfer(testInputHandler, inputTank, inputTank.getFluidAmount(), false);
                if (!transferredStack.isEmpty()) {
                    testInputHandler.fill(transferredStack, FluidAction.EXECUTE);
                    tempBucketList.clear();
                    tempBucketList.add(testInputHandler.getContainer());
                    if (itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                        outputChanged = true;
                        inputTank.drain(transferredStack, FluidAction.EXECUTE);
                        itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
                        athanorInv.setStackInSlot(INPUT_BUCKET_SLOT, ItemStack.EMPTY);
                    }
                }
            }
        }

        if (testOutputHandler != null) {
            /* probably dont insert into output tank
            FluidStack transferredStack = FluidUtil.tryFluidTransfer(outputTank, testOutputHandler, outputTank.getCapacity() - outputTank.getFluidAmount(), false);
            if (!transferredStack.isEmpty()) {
                testOutputHandler.drain(transferredStack, FluidAction.EXECUTE);
                tempBucketList.clear();
                tempBucketList.add(testOutputHandler.getContainer());
                if (itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                    outputChanged = true;
                    outputTank.fill(transferredStack, FluidAction.EXECUTE);
                    itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
                    itemHandler.setStackInSlot(OUTPUT_BUCKET_SLOT, ItemStack.EMPTY);
                }
            } else {

             */
            FluidStack transferredStack = FluidUtil.tryFluidTransfer(testOutputHandler, outputTank, outputTank.getFluidAmount(), false);
            if (!transferredStack.isEmpty()) {
                testOutputHandler.fill(transferredStack, FluidAction.EXECUTE);
                tempBucketList.clear();
                tempBucketList.add(testOutputHandler.getContainer());
                if (itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                    outputChanged = true;
                    outputTank.drain(transferredStack, FluidAction.EXECUTE);
                    itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
                    athanorInv.setStackInSlot(OUTPUT_BUCKET_SLOT, ItemStack.EMPTY);
                }
            }
            //}
        }

        ItemStack toolStack = athanorInv.getStackInSlot(TOOL_SLOT).copy();
        if (toolStack.getDamageValue() >= toolStack.getMaxDamage()) {
            tempBucketList.clear();
            toolStack.setDamageValue(toolStack.getMaxDamage());
            tempBucketList.add(toolStack);
            if (itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                outputChanged = true;
                itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
                athanorInv.setStackInSlot(TOOL_SLOT, ItemStack.EMPTY);
                updateType();
            }
        }

        return outputChanged;
    }

    private void snapshotChunkWill(Level level, BlockPos pos) {
        for (SpiritusType type : SpiritusType.values()) {
            chunkWill[type.ordinal()] = WorldSpiritusHandler.getCurrentWill(level, pos, type);
            chunkWillMax[type.ordinal()] = WorldSpiritusHandler.getMaxWill(level, pos, type);
        }
        setChanged();
    }

    private boolean hasEnoughWill(Level level, BlockPos pos, AthanorRecipe recipe) {
        for (Map.Entry<SpiritusType, Double> entry : recipe.getSpiritusCosts().entrySet()) {
            if (WorldSpiritusHandler.getCurrentWill(level, pos, entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void drainWillCosts(Level level, BlockPos pos, AthanorRecipe recipe) {
        for (Map.Entry<SpiritusType, Double> entry : recipe.getSpiritusCosts().entrySet()) {
            WorldSpiritusHandler.drainWillFromChunk(level, pos, entry.getKey(), entry.getValue());
        }
    }
}
