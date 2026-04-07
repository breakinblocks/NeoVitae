package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.SpiritusCrystalItem;
import com.breakinblocks.neovitae.will.SpiritusHelper;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

public class VasMaleficumBlockEntity extends BaseBlockEntity {
    public static final double GEM_DRAIN_RATE = 10.0;
    public static final double CRYSTAL_CONSUME_THRESHOLD = 50.0;
    public static final double WILL_PER_CRYSTAL = 50.0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return SpiritusHelper.hasSpiritus(stack) || stack.getItem() instanceof SpiritusCrystalItem;
        }
    };

    private int internalCounter = 0;

    public VasMaleficumBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.VAS_MALEFICUM_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VasMaleficumBlockEntity tile) {
        if (level.isClientSide()) return;
        tile.internalCounter++;
        tile.onUpdate();
    }

    private void onUpdate() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()) return;

        boolean isPowered = level.hasNeighborSignal(worldPosition);

        if (stack.getItem() instanceof SpiritusCrystalItem crystal) {
            if (!isPowered) handleCrystal(crystal, stack);
        } else if (SpiritusHelper.hasSpiritus(stack)) {
            if (isPowered && SpiritusHelper.isRechargeable(stack)) {
                handleFill(stack);
            } else if (!isPowered) {
                if (SpiritusHelper.isRechargeable(stack)) {
                    handleDrain(stack);
                } else {
                    handleConsume(stack);
                }
            }
        }
    }

    private void handleDrain(ItemStack stack) {
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
            double maxWillInChunk = WorldSpiritusHandler.getMaxWill(level, worldPosition, type);
            if (currentChunkWill >= maxWillInChunk) continue;

            double spaceInChunk = maxWillInChunk - currentChunkWill;
            double drainAmount = Math.min(GEM_DRAIN_RATE, spaceInChunk);

            double canDrain = SpiritusHelper.drainWill(stack, type, drainAmount, false);
            if (canDrain > 0) {
                double drained = SpiritusHelper.drainWill(stack, type, canDrain, true);
                if (drained > 0) {
                    WorldSpiritusHandler.addWillToChunk(level, worldPosition, type, drained);
                    setChanged();
                }
            }
        }
    }

    private void handleFill(ItemStack stack) {
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
            if (currentChunkWill <= 0) continue;

            double fillAmount = Math.min(GEM_DRAIN_RATE, currentChunkWill);
            double canFill = SpiritusHelper.fillWill(stack, type, fillAmount, false);
            if (canFill > 0) {
                double drained = WorldSpiritusHandler.drainWillFromChunk(level, worldPosition, type, canFill);
                if (drained > 0) {
                    SpiritusHelper.fillWill(stack, type, drained, true);
                    setChanged();
                }
            }
        }
    }

    private void handleConsume(ItemStack stack) {
        SpiritusType type = SpiritusHelper.getCurrentType(stack);
        double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
        double maxWillInChunk = WorldSpiritusHandler.getMaxWill(level, worldPosition, type);
        if (currentChunkWill >= maxWillInChunk) return;

        double willAmount = SpiritusHelper.getWill(stack, type);
        double spaceInChunk = maxWillInChunk - currentChunkWill;

        if (spaceInChunk > 0 && willAmount > 0) {
            double toAdd = Math.min(willAmount, spaceInChunk);
            double drained = SpiritusHelper.drainWill(stack, type, toAdd, true);
            if (drained > 0) {
                WorldSpiritusHandler.addWillToChunk(level, worldPosition, type, drained);
                if (SpiritusHelper.getWill(stack, type) <= 0) {
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                }
                setChanged();
            }
        }
    }

    private void handleCrystal(SpiritusCrystalItem crystal, ItemStack stack) {
        SpiritusType type = crystal.getWillType();
        double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);

        if (currentChunkWill < CRYSTAL_CONSUME_THRESHOLD) {
            double added = WorldSpiritusHandler.addWillToChunk(level, worldPosition, type, WILL_PER_CRYSTAL);
            if (added > 0) {
                stack.shrink(1);
                setChanged();
            }
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean handleInteraction(ItemStack heldItem) {
        ItemStack currentItem = inventory.getStackInSlot(0);

        if (heldItem.isEmpty()) {
            if (!currentItem.isEmpty()) return true;
        } else {
            if (inventory.isItemValid(0, heldItem)) {
                if (currentItem.isEmpty()) {
                    inventory.setStackInSlot(0, heldItem.split(1));
                    return true;
                } else if (ItemStack.isSameItemSameComponents(currentItem, heldItem) && currentItem.getCount() < currentItem.getMaxStackSize()) {
                    currentItem.grow(1);
                    heldItem.shrink(1);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }
}
