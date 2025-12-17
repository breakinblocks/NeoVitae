package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.DemonCrystalItem;
import com.breakinblocks.neovitae.will.IDemonWill;
import com.breakinblocks.neovitae.will.IDemonWillGem;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

/**
 * Demon Crucible - manages demon will between items and chunk aura.
 *
 * Without redstone signal (default):
 * - Tartaric Gems: drains will gradually into chunk aura (caps at configured max)
 * - Monster Souls: consumed immediately, will released to aura
 * - Demon Crystals: consumed when chunk will drops below threshold
 *
 * With redstone signal:
 * - Tartaric Gems: absorbs will from chunk aura into the gem
 * - Monster Souls/Crystals: not affected (output only)
 */
public class DemonCrucibleTile extends BaseTile {
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
            return stack.getItem() instanceof IDemonWillGem ||
                   stack.getItem() instanceof IDemonWill ||
                   stack.getItem() instanceof DemonCrystalItem;
        }
    };

    private int internalCounter = 0;

    public DemonCrucibleTile(BlockPos pos, BlockState state) {
        super(BMTiles.DEMON_CRUCIBLE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DemonCrucibleTile tile) {
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

        // Check for redstone signal
        boolean isPowered = level.hasNeighborSignal(worldPosition);

        if (isPowered) {
            // With redstone: fill gem from internal buffer (not implemented yet - gems fill from aura)
            if (stack.getItem() instanceof IDemonWillGem gem) {
                handleGemFill(gem, stack);
            }
        } else {
            // Without redstone: drain to aura
            if (stack.getItem() instanceof IDemonWillGem gem) {
                handleGemDrain(gem, stack);
            } else if (stack.getItem() instanceof IDemonWill will) {
                handleWillItem(will, stack);
            } else if (stack.getItem() instanceof DemonCrystalItem crystal) {
                handleCrystal(crystal, stack);
            }
        }
    }

    /**
     * Handles tartaric gems - drains will from gem to aura (no redstone).
     * Loops through all will types and drains each one.
     * Respects the configurable max will per chunk.
     */
    private void handleGemDrain(IDemonWillGem gem, ItemStack stack) {
        // Loop through all will types like 1.20.1 does
        for (EnumWillType type : EnumWillType.values()) {
            double currentChunkWill = WorldDemonWillHandler.getCurrentWill(level, worldPosition, type);
            double maxWillInChunk = WorldDemonWillHandler.getMaxWill(level, worldPosition, type);

            // Only drain if chunk isn't full
            if (currentChunkWill >= maxWillInChunk) {
                continue;
            }

            double spaceInChunk = maxWillInChunk - currentChunkWill;
            double drainAmount = Math.min(GEM_DRAIN_RATE, spaceInChunk);

            // First check how much we can actually drain from the gem (simulate)
            double canDrain = gem.drainWill(type, stack, drainAmount, false);
            if (canDrain > 0) {
                // Actually drain from gem
                double drained = gem.drainWill(type, stack, canDrain, true);
                if (drained > 0) {
                    // Add to chunk aura
                    WorldDemonWillHandler.addWillToChunk(level, worldPosition, type, drained);
                    setChanged();
                }
            }
        }
    }

    /**
     * Handles tartaric gems - absorbs will from aura into gem (with redstone).
     */
    private void handleGemFill(IDemonWillGem gem, ItemStack stack) {
        // Loop through all will types
        for (EnumWillType type : EnumWillType.values()) {
            double currentChunkWill = WorldDemonWillHandler.getCurrentWill(level, worldPosition, type);
            if (currentChunkWill <= 0) {
                continue;
            }

            double fillAmount = Math.min(GEM_DRAIN_RATE, currentChunkWill);

            // Check how much we can fill into the gem (simulate)
            double canFill = gem.fillWill(type, stack, fillAmount, false);
            if (canFill > 0) {
                // Drain from aura
                double drained = WorldDemonWillHandler.drainWillFromChunk(level, worldPosition, type, canFill);
                if (drained > 0) {
                    // Fill into gem
                    gem.fillWill(type, stack, drained, true);
                    setChanged();
                }
            }
        }
    }

    /**
     * Handles monster souls and raw will - consumed when there's room in the aura.
     */
    private void handleWillItem(IDemonWill will, ItemStack stack) {
        EnumWillType type = will.getType(stack);
        double currentChunkWill = WorldDemonWillHandler.getCurrentWill(level, worldPosition, type);
        double maxWillInChunk = WorldDemonWillHandler.getMaxWill(level, worldPosition, type);

        // Only consume if chunk isn't full
        if (currentChunkWill >= maxWillInChunk) {
            return;
        }

        double willAmount = will.getWill(type, stack);
        double spaceInChunk = maxWillInChunk - currentChunkWill;

        // Only consume if we can fit the will amount
        if (spaceInChunk > 0 && willAmount > 0) {
            double toAdd = Math.min(willAmount, spaceInChunk);
            double drained = will.drainWill(type, stack, toAdd);
            if (drained > 0) {
                WorldDemonWillHandler.addWillToChunk(level, worldPosition, type, drained);
                if (stack.isEmpty() || stack.getCount() <= 0) {
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                }
                setChanged();
            }
        }
    }

    /**
     * Handles demon crystals - consumed when chunk will drops below threshold.
     */
    private void handleCrystal(DemonCrystalItem crystal, ItemStack stack) {
        EnumWillType type = crystal.getWillType();
        double currentChunkWill = WorldDemonWillHandler.getCurrentWill(level, worldPosition, type);

        // Only consume crystals when will drops below threshold
        if (currentChunkWill < CRYSTAL_CONSUME_THRESHOLD) {
            double added = WorldDemonWillHandler.addWillToChunk(level, worldPosition, type, WILL_PER_CRYSTAL);
            if (added > 0) {
                stack.shrink(1);
                setChanged();
            }
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    /**
     * Called when a player interacts with the crucible.
     * @return true if interaction was handled
     */
    public boolean handleInteraction(ItemStack heldItem) {
        ItemStack currentItem = inventory.getStackInSlot(0);

        if (heldItem.isEmpty()) {
            // Extract item
            if (!currentItem.isEmpty()) {
                // Drop or give to player handled by block
                return true;
            }
        } else {
            // Insert item
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
