package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeTile;
import com.breakinblocks.neovitae.common.item.routing.IItemFilterProvider;

/**
 * Menu for Input/Output Routing Nodes.
 * Provides 6 filter slots (one per direction) and priority controls.
 */
public class RoutingNodeMenu extends AbstractContainerMenu {
    public final FilteredRoutingNodeTile tile;
    private final ContainerData data;

    // Data indices
    public static final int DATA_CURRENT_SLOT = 0;
    public static final int DATA_PRIORITY_DOWN = 1;
    public static final int DATA_PRIORITY_UP = 2;
    public static final int DATA_PRIORITY_NORTH = 3;
    public static final int DATA_PRIORITY_SOUTH = 4;
    public static final int DATA_PRIORITY_WEST = 5;
    public static final int DATA_PRIORITY_EAST = 6;
    public static final int DATA_SIZE = 7;

    public RoutingNodeMenu(int containerId, Inventory playerInventory, FilteredRoutingNodeTile tile) {
        super(BMMenus.ROUTING_NODE.get(), containerId);
        this.tile = tile;

        // Create container data for syncing - handle null tile for client-side construction
        if (tile != null) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    if (index == DATA_CURRENT_SLOT) {
                        return tile.getCurrentActiveSlot();
                    } else if (index >= DATA_PRIORITY_DOWN && index <= DATA_PRIORITY_EAST) {
                        return tile.priorities[index - DATA_PRIORITY_DOWN];
                    }
                    return 0;
                }

                @Override
                public void set(int index, int value) {
                    if (index == DATA_CURRENT_SLOT) {
                        tile.setCurrentActiveSlot(value);
                    } else if (index >= DATA_PRIORITY_DOWN && index <= DATA_PRIORITY_EAST) {
                        tile.priorities[index - DATA_PRIORITY_DOWN] = value;
                    }
                }

                @Override
                public int getCount() {
                    return DATA_SIZE;
                }
            };
        } else {
            this.data = new SimpleContainerData(DATA_SIZE);
        }
        this.addDataSlots(data);

        // Add single filter slot that swaps based on direction - matches 1.20.1
        // Position (71, 33) from 1.20.1
        if (tile != null) {
            this.addSlot(new FilterSlot(tile, 0, 71, 33));
        }

        // Player inventory and hotbar - positions match 1.20.1
        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, 87, 145);
    }

    public RoutingNodeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntitySafe(playerInventory, buf.readBlockPos()));
    }

    private static FilteredRoutingNodeTile getBlockEntitySafe(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level() == null) return null;
        if (playerInventory.player.level().getBlockEntity(pos) instanceof FilteredRoutingNodeTile tile) {
            return tile;
        }
        return null;
    }

    public int getCurrentSlot() {
        return data.get(DATA_CURRENT_SLOT);
    }

    public int getPriority(Direction dir) {
        return data.get(DATA_PRIORITY_DOWN + dir.get3DDataValue());
    }

    public int getCurrentPriority() {
        int slot = getCurrentSlot();
        if (slot >= 0 && slot < 6) {
            return data.get(DATA_PRIORITY_DOWN + slot);
        }
        return 0;
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < 6) {
            data.set(DATA_CURRENT_SLOT, slot);
            if (tile != null) {
                tile.swapFilters(slot);
            }
        }
    }

    public void incrementPriority() {
        if (tile != null) {
            tile.incrementCurrentPriorityToMaximum(10);
        }
    }

    public void decrementPriority() {
        if (tile != null) {
            tile.decrementCurrentPriority();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // Filter slot is 0, player inventory is 1-36
            if (index == 0) {
                // Moving from filter slot to player inventory
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to filter slot
                if (slotStack.getItem() instanceof IItemFilterProvider) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    // From main inventory to hotbar
                    if (!this.moveItemStackTo(slotStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    // From hotbar to main inventory
                    if (!this.moveItemStackTo(slotStack, 1, 28, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile != null && tile.stillValid(player);
    }

    /**
     * Custom slot that only accepts filter items.
     * Dynamically maps to the current active direction slot in the tile.
     */
    private static class FilterSlot extends Slot {
        private final FilteredRoutingNodeTile routingTile;

        public FilterSlot(FilteredRoutingNodeTile tile, int index, int x, int y) {
            super(tile, index, x, y);
            this.routingTile = tile;
        }

        @Override
        public ItemStack getItem() {
            // Return the filter for the currently selected direction
            int activeSlot = routingTile.getCurrentActiveSlot();
            return routingTile.getItem(activeSlot);
        }

        @Override
        public void set(ItemStack stack) {
            // Set the filter for the currently selected direction
            int activeSlot = routingTile.getCurrentActiveSlot();
            routingTile.setItem(activeSlot, stack);
            this.setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            int activeSlot = routingTile.getCurrentActiveSlot();
            ItemStack stack = routingTile.getItem(activeSlot);
            if (!stack.isEmpty() && amount > 0) {
                ItemStack removed = stack.split(amount);
                if (stack.isEmpty()) {
                    routingTile.setItem(activeSlot, ItemStack.EMPTY);
                }
                this.setChanged();
                return removed;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof IItemFilterProvider;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
        }
    }
}
