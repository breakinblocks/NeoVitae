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
import com.breakinblocks.neovitae.will.ISpiritus;
import com.breakinblocks.neovitae.will.ISpiritusGem;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

/**
 * Vas Maleficum - manages spiritus between items and chunk aura.
 *
 * Without redstone signal (default):
 * - Spiritus Gems: drains will gradually into chunk aura (caps at configured max)
 * - Monster Souls: consumed immediately, will released to aura
 * - Demon Crystals: consumed when chunk will drops below threshold
 *
 * With redstone signal:
 * - Spiritus Gems: absorbs will from chunk aura into the gem
 * - Monster Souls/Crystals: not affected (output only)
 */
public class VasMaleficumBlockEntity extends BaseBlockEntity {
    public static final double GEM_DRAIN_RATE = 10.0; // Will drained from gems per tick
    public static final double CRYSTAL_CONSUME_THRESHOLD = 50.0;
    public static final double WILL_PER_CRYSTAL = 50.0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof ISpiritusGem ||
                   stack.getItem() instanceof ISpiritus ||
                   stack.getItem() instanceof SpiritusCrystalItem;
        }
    };

    private int internalCounter = 0;

    public VasMaleficumBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.VAS_MALEFICUM_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VasMaleficumBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.internalCounter++;
        tile.onUpdate();
    }

    private void onUpdate() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }

        boolean isPowered = level.hasNeighborSignal(worldPosition);

        if (isPowered) {
            if (stack.getItem() instanceof ISpiritusGem gem) {
                handleGemFill(gem, stack);
            }
        } else {
            if (stack.getItem() instanceof ISpiritusGem gem) {
                handleGemDrain(gem, stack);
            } else if (stack.getItem() instanceof ISpiritus will) {
                handleWillItem(will, stack);
            } else if (stack.getItem() instanceof SpiritusCrystalItem crystal) {
                handleCrystal(crystal, stack);
            }
        }
    }

    private void handleGemDrain(ISpiritusGem gem, ItemStack stack) {
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
            double maxWillInChunk = WorldSpiritusHandler.getMaxWill(level, worldPosition, type);

            if (currentChunkWill >= maxWillInChunk) {
                continue;
            }

            double spaceInChunk = maxWillInChunk - currentChunkWill;
            double drainAmount = Math.min(GEM_DRAIN_RATE, spaceInChunk);

            double canDrain = gem.drainWill(type, stack, drainAmount, false);
            if (canDrain > 0) {
                double drained = gem.drainWill(type, stack, canDrain, true);
                if (drained > 0) {
                    WorldSpiritusHandler.addWillToChunk(level, worldPosition, type, drained);
                    setChanged();
                }
            }
        }
    }

    private void handleGemFill(ISpiritusGem gem, ItemStack stack) {
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
            if (currentChunkWill <= 0) {
                continue;
            }

            double fillAmount = Math.min(GEM_DRAIN_RATE, currentChunkWill);

            double canFill = gem.fillWill(type, stack, fillAmount, false);
            if (canFill > 0) {
                double drained = WorldSpiritusHandler.drainWillFromChunk(level, worldPosition, type, canFill);
                if (drained > 0) {
                    gem.fillWill(type, stack, drained, true);
                    setChanged();
                }
            }
        }
    }

    private void handleWillItem(ISpiritus will, ItemStack stack) {
        SpiritusType type = will.getType(stack);
        double currentChunkWill = WorldSpiritusHandler.getCurrentWill(level, worldPosition, type);
        double maxWillInChunk = WorldSpiritusHandler.getMaxWill(level, worldPosition, type);

        if (currentChunkWill >= maxWillInChunk) {
            return;
        }

        double willAmount = will.getWill(type, stack);
        double spaceInChunk = maxWillInChunk - currentChunkWill;

        if (spaceInChunk > 0 && willAmount > 0) {
            double toAdd = Math.min(willAmount, spaceInChunk);
            double drained = will.drainWill(type, stack, toAdd);
            if (drained > 0) {
                WorldSpiritusHandler.addWillToChunk(level, worldPosition, type, drained);
                if (stack.isEmpty() || stack.getCount() <= 0) {
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
            if (!currentItem.isEmpty()) {
                return true;
            }
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
