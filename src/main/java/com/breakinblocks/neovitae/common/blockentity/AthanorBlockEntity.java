package com.breakinblocks.neovitae.common.blockentity;


import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.block.AthanorBlock;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipeInput;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.util.AthanorOutputHandler;
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
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + NUM_OUTPUTS;
    public static final int TANK_CAPACITY = 20 * FluidType.BUCKET_VOLUME;

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

    @SuppressWarnings("unchecked")
    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> createCookingLookup(RecipeType<? extends AbstractCookingRecipe> recipeType) {
        return RecipeManager.createCheck((RecipeType<AbstractCookingRecipe>) recipeType);
    }

    public final Inv athanorInv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(INVENTORY_SIZE); }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index == TOOL_SLOT) return resource.is(NVTags.Items.ATHANOR_TOOL);
            if (index >= INPUT_START && index < INPUT_START + NUM_INPUTS) return true;
            if (index == INPUT_BUCKET_SLOT || index == OUTPUT_BUCKET_SLOT) {
                return ItemAccess.forStack(resource.toStack(1)).oneByOne().getCapability(Capabilities.Fluid.ITEM) != null;
            }
            return false;
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            if (index == INPUT_BUCKET_SLOT || index == OUTPUT_BUCKET_SLOT) return 1;
            return super.getCapacity(index, resource);
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        public ItemStack getStackInSlot(int slot) {
            ItemResource r = getResource(slot);
            return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    public final Tank inputTank = new Tank();
    public final Tank outputTank = new Tank();

    public class Tank extends FluidStacksResourceHandler {
        Tank() { super(1, TANK_CAPACITY); }

        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            setChanged();
        }

        public FluidStack getFluid() {
            FluidResource r = getResource(0);
            return r.isEmpty() ? FluidStack.EMPTY : r.toStack(getAmountAsInt(0));
        }

        public void setFluid(FluidStack stack) {
            set(0, FluidResource.of(stack), stack.getAmount());
        }

        public int getFluidAmount() {
            return getAmountAsInt(0);
        }

        public int getCapacity() {
            return capacity;
        }

        public boolean isEmpty() {
            return getResource(0).isEmpty();
        }
    }

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

    public static ResourceHandler<ItemResource> getItemHandler(AthanorBlockEntity tile, @Nullable Direction side) {
        if (side == null) {
            return tile.athanorInv;
        }
        return switch (side) {
            case UP -> RangedResourceHandler.of(tile.athanorInv, TOOL_SLOT, TOOL_SLOT + 1);
            case DOWN -> RangedResourceHandler.of(tile.athanorInv, OUTPUT_SLOT, OUTPUT_SLOT + NUM_OUTPUTS);
            default -> RangedResourceHandler.of(tile.athanorInv, INPUT_START, OUTPUT_BUCKET_SLOT + 1);
        };
    }

    public static ResourceHandler<FluidResource> getFluidHandler(AthanorBlockEntity tile, @Nullable Direction side) {
        return side == Direction.DOWN ? tile.outputTank : tile.inputTank;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.neovitae.athanor");
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.child("arcinv").ifPresent(athanorInv::deserialize);
        tag.child("inputTank").ifPresent(inputTank::deserialize);
        tag.child("outputTank").ifPresent(outputTank::deserialize);
        progress = tag.getDoubleOr("arcprogress", 0d);
        willBlocked = tag.getBooleanOr("willBlocked", false);
        tag.child("chunkWill").ifPresent(willTag -> {
            for (SpiritusType type : SpiritusType.values()) {
                chunkWill[type.ordinal()] = willTag.getDoubleOr(type.getSerializedName(), 0d);
                chunkWillMax[type.ordinal()] = willTag.getDoubleOr(type.getSerializedName() + "_max", 0d);
            }
        });
        Optional<ValueInput> costTagOpt = tag.child("recipeWillCost");
        if (costTagOpt.isPresent()) {
            ValueInput costTag = costTagOpt.get();
            EnumMap<SpiritusType, Double> costs = new EnumMap<>(SpiritusType.class);
            for (SpiritusType type : SpiritusType.values()) {
                costTag.read(type.getSerializedName(), com.mojang.serialization.Codec.DOUBLE).ifPresent(v -> costs.put(type, v));
            }
            currentRecipeWillCost = Map.copyOf(costs);
        } else {
            currentRecipeWillCost = Map.of();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        athanorInv.serialize(tag.child("arcinv"));
        inputTank.serialize(tag.child("inputTank"));
        outputTank.serialize(tag.child("outputTank"));
        tag.putDouble("arcprogress", progress);
        tag.putBoolean("willBlocked", willBlocked);
        ValueOutput willTag = tag.child("chunkWill");
        for (SpiritusType type : SpiritusType.values()) {
            willTag.putDouble(type.getSerializedName(), chunkWill[type.ordinal()]);
            willTag.putDouble(type.getSerializedName() + "_max", chunkWillMax[type.ordinal()]);
        }
        if (!currentRecipeWillCost.isEmpty()) {
            ValueOutput costTag = tag.child("recipeWillCost");
            currentRecipeWillCost.forEach((type, amount) -> costTag.putDouble(type.getSerializedName(), amount));
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AthanorMenu(containerId, playerInventory, this);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AthanorBlockEntity athanorTile) {
        if (level.isClientSide()) {
            if (athanorTile.progress > 0) {
                LoopSoundManager.tryStartLoop(
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
        double rawWill = WorldSpiritusHandler.getCurrentWill(level, blockPos, SpiritusType.RAW);
        double willSpeedMod = 0.5 + 1.5 * Math.min(1.0, rawWill / 100.0);
        boolean didProgress = false;
        ServerLevel serverLevel = level instanceof ServerLevel sl ? sl : null;
        if (toolStack.is(NVTags.Items.ATHANOR_TOOL)) {
            if (toolStack.is(NVTags.Items.ATHANOR_FURNACE) && serverLevel != null) {
                Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = Optional.empty();
                SingleRecipeInput input = new SingleRecipeInput(inputStacks[0]);
                if (toolStack.is(NVTags.Items.ARC_SMELTING)) {
                     recipe = athanorTile.quickSmelting.getRecipeFor(input, serverLevel);
                } else if (toolStack.is(NVTags.Items.ARC_BLASTING)) {
                    recipe = athanorTile.quickBlasting.getRecipeFor(input, serverLevel);
                } else if (toolStack.is(NVTags.Items.ARC_SMOKING)) {
                    recipe = athanorTile.quickSmoking.getRecipeFor(input, serverLevel);
                }
                if (athanorTile.canCraftFurnace(recipe, itemOutputHandler)) {
                    athanorTile.progress += DEFAULT_SPEED * ((double) recipe.get().value().cookingTime() / 200D) * toolStack.getOrDefault(NVDataComponents.ARC_SPEED, 1D) * willSpeedMod;
                    didProgress = true;
                    if (athanorTile.progress >= 1) {
                        athanorTile.craftFurnace(recipe.get().value(), input, itemOutputHandler);
                        outputChanged = true;
                    }
                }
            } else if (serverLevel != null) {
                AthanorRecipeInput input = new AthanorRecipeInput(toolStack, inputStacks, athanorTile.inputTank.getFluid());
                Optional<RecipeHolder<AthanorRecipe>> recipe = athanorTile.quickAthanor.getRecipeFor(input, serverLevel);
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

        if (didProgress && level.getGameTime() % 20 == 0 && level.getRandom().nextFloat() < 0.05f) {
            WorldSpiritusHandler.drainWillFromChunk(level, blockPos, SpiritusType.RAW, 1.0);
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
        if (recipe.isEmpty() || !(level instanceof ServerLevel sl)) {
            return false;
        }
        ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(ItemStack.EMPTY));
        return outputHandler.canTransferAllItemsToSlots(List.of(result), true);
    }

    private void craftFurnace(AbstractCookingRecipe value, SingleRecipeInput input, AthanorOutputHandler outputHandler) {
        if (!(level instanceof ServerLevel sl)) return;
        ItemStack output = value.assemble(input);
        handleInventory(List.of(output), outputHandler);
    }

    private boolean canCraft(Optional<RecipeHolder<AthanorRecipe>> recipe, AthanorOutputHandler outputHandler) {
        if (recipe.isEmpty()) {
            return false;
        }
        AthanorRecipe athanorRecipe = recipe.get().value();
        List<ItemStack> outputs = athanorRecipe.getAllListedOutputs().stream()
                .map(Pair::getFirst)
                .map(ItemStackTemplate::create)
                .toList();
        if (!outputHandler.canTransferAllItemsToSlots(outputs, true)) {
            return false;
        }
        if (athanorRecipe.getOutputFluid().isPresent()) {
            FluidStack fluid = athanorRecipe.getOutputFluid().get();
            int filled;
            try (Transaction tx = Transaction.openRoot()) {
                filled = outputTank.insert(FluidResource.of(fluid), fluid.getAmount(), tx);
            }
            if (filled != fluid.getAmount()) {
                return false;
            }
        }
        return true;
    }

    private void craft(AthanorRecipe value, AthanorRecipeInput input, AthanorOutputHandler outputHandler) {
        AthanorRecipe.AthanorResult result = value.assembleOutputs(input, level, worldPosition);
        value.getInputFluid().ifPresent(required -> {
            try (Transaction tx = Transaction.openRoot()) {
                FluidResource res = FluidResource.of(inputTank.getFluid());
                if (!res.isEmpty()) {
                    inputTank.extract(res, required.amount(), tx);
                }
                tx.commit();
            }
        });
        FluidStack outFluid = result.fluid();
        if (!outFluid.isEmpty()) {
            try (Transaction tx = Transaction.openRoot()) {
                outputTank.insert(FluidResource.of(outFluid), outFluid.getAmount(), tx);
                tx.commit();
            }
        }
        handleInventory(result.items(), outputHandler);
    }

    private void handleInventory(List<ItemStack> toOutput, AthanorOutputHandler outputHandler) {
        outputHandler.canTransferAllItemsToSlots(toOutput, false);
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, NVSounds.ATHANOR_COMPLETE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x22AA22), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0);
        }
        for (int s = INPUT_START; s < INPUT_START + NUM_INPUTS; s++) {
            ItemStack inSlot = athanorInv.getStackInSlot(s);
            if (!inSlot.isEmpty()) {
                inSlot.shrink(1);
                athanorInv.setStackInSlot(s, inSlot);
            }
        }
        progress = 0;

        ItemStack toolStack = athanorInv.getStackInSlot(TOOL_SLOT);
        if (!toolStack.has(DataComponents.UNBREAKABLE)) {
            var template = toolStack.getCraftingRemainder();
            ItemStack remainder = template != null ? template.create() : ItemStack.EMPTY;
            if (!remainder.isEmpty()) {
                athanorInv.setStackInSlot(TOOL_SLOT, remainder);
            } else if (toolStack.has(DataComponents.MAX_DAMAGE)) {
                int lost = EnchantmentHelper.processDurabilityChange((ServerLevel) level, toolStack, 1);
                int newDamage = toolStack.getOrDefault(DataComponents.DAMAGE, 0) + lost;
                if (newDamage >= toolStack.getMaxDamage()) {
                    athanorInv.setStackInSlot(TOOL_SLOT, ItemStack.EMPTY);
                } else {
                    toolStack.set(DataComponents.DAMAGE, newDamage);
                    athanorInv.setStackInSlot(TOOL_SLOT, toolStack);
                }
            } else {
                toolStack.shrink(1);
                athanorInv.setStackInSlot(TOOL_SLOT, toolStack);
            }
        }
    }

    public void setLit(boolean lit) {
        BlockState state = getBlockState();
        if (state.getValue(AthanorBlock.LIT) != lit) {
            level.setBlock(getBlockPos(), state.setValue(AthanorBlock.LIT, lit), Block.UPDATE_ALL);
        }
    }

    public void updateType() {
        SpiritusType type = athanorInv.getStackInSlot(TOOL_SLOT).getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        BlockState state = getBlockState();
        if (state.getValue(AthanorBlock.TYPE) != type) {
            level.setBlock(getBlockPos(), state.setValue(AthanorBlock.TYPE, type), Block.UPDATE_ALL);
        }
    }

    public boolean handleSlots(AthanorOutputHandler itemOutputHandler) {
        boolean outputChanged = false;

        outputChanged |= moveBucket(INPUT_BUCKET_SLOT, inputTank, itemOutputHandler);
        outputChanged |= moveBucket(OUTPUT_BUCKET_SLOT, outputTank, itemOutputHandler);

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

    private boolean moveBucket(int bucketSlot, Tank tank, AthanorOutputHandler itemOutputHandler) {
        ItemStack bucket = athanorInv.getStackInSlot(bucketSlot);
        if (bucket.isEmpty()) return false;

        ItemAccess access = ItemAccess.forStack(bucket.copy()).oneByOne();
        ResourceHandler<FluidResource> bucketHandler = access.getCapability(Capabilities.Fluid.ITEM);
        if (bucketHandler == null) return false;

        try (Transaction tx = Transaction.openRoot()) {
            ResourceStack<FluidResource> moved = ResourceHandlerUtil.moveFirst(tank, bucketHandler, r -> true, Integer.MAX_VALUE, tx);
            if (moved == null) {
                moved = ResourceHandlerUtil.moveFirst(bucketHandler, tank, r -> true, Integer.MAX_VALUE, tx);
            }
            if (moved == null) return false;

            ItemStack resultBucket = access.getResource().toStack(access.getAmount());
            tempBucketList.clear();
            tempBucketList.add(resultBucket);
            if (!itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, true)) {
                return false;
            }
            tx.commit();
            itemOutputHandler.canTransferAllItemsToSlots(tempBucketList, false);
            athanorInv.setStackInSlot(bucketSlot, ItemStack.EMPTY);
            return true;
        }
    }

    private void snapshotChunkWill(Level level, BlockPos pos) {
        for (SpiritusType type : SpiritusType.values()) {
            chunkWill[type.ordinal()] = WorldSpiritusHandler.getCurrentWill(level, pos, type);
            chunkWillMax[type.ordinal()] = WorldSpiritusHandler.getMaxSpiritus(level, pos, type);
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
