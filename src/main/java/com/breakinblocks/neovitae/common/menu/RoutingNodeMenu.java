package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;

public class RoutingNodeMenu extends AbstractContainerMenu {
    public final FilteredRoutingNodeBlockEntity tile;
    private final ContainerData data;
    private final SimpleContainer ghostContainer;

    public static final int GHOST_SLOT_COUNT = SideFilterConfig.PAGE_SIZE;

    public static final int DATA_CURRENT_SLOT = 0;
    public static final int DATA_PRIORITY_DOWN = 1;
    public static final int DATA_PRIORITY_UP = 2;
    public static final int DATA_PRIORITY_NORTH = 3;
    public static final int DATA_PRIORITY_SOUTH = 4;
    public static final int DATA_PRIORITY_WEST = 5;
    public static final int DATA_PRIORITY_EAST = 6;
    public static final int DATA_SIDE_ENABLED_START = 7;       // +6 for each direction
    public static final int DATA_SIDE_ITEM_MODE_START = 13;    // +6 for each direction
    public static final int DATA_SIDE_FLUID_MODE_START = 19;   // +6 for each direction
    public static final int DATA_SIZE = 25;

    private static final int GHOST_COLS = SideFilterConfig.PAGE_COLUMNS;
    private static final int GHOST_ROWS = SideFilterConfig.PAGE_ROWS;
    public static final int GHOST_ORIGIN_X = 8;
    public static final int GHOST_ORIGIN_Y = 76;
    public static final int INVENTORY_Y = 142;
    public static final int HOTBAR_Y = 200;
    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 224;

    private int currentPage = 0;
    private boolean showItemGhosts = true;

    public RoutingNodeMenu(int containerId, Inventory playerInventory, FilteredRoutingNodeBlockEntity tile) {
        super(NVMenus.ROUTING_NODE.get(), containerId);
        this.tile = tile;

        this.ghostContainer = new SimpleContainer(GHOST_SLOT_COUNT);
        refreshGhostContainer();

        if (tile != null) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    if (index == DATA_CURRENT_SLOT) {
                        return tile.getCurrentActiveSlot();
                    } else if (index >= DATA_PRIORITY_DOWN && index <= DATA_PRIORITY_EAST) {
                        return tile.priorities[index - DATA_PRIORITY_DOWN];
                    } else if (index >= DATA_SIDE_ENABLED_START && index < DATA_SIDE_ENABLED_START + 6) {
                        return tile.getSideFilter(index - DATA_SIDE_ENABLED_START).isEnabled() ? 1 : 0;
                    } else if (index >= DATA_SIDE_ITEM_MODE_START && index < DATA_SIDE_ITEM_MODE_START + 6) {
                        return tile.getSideFilter(index - DATA_SIDE_ITEM_MODE_START).getItemMode().ordinal();
                    } else if (index >= DATA_SIDE_FLUID_MODE_START && index < DATA_SIDE_FLUID_MODE_START + 6) {
                        return tile.getSideFilter(index - DATA_SIDE_FLUID_MODE_START).getFluidMode().ordinal();
                    }
                    return 0;
                }

                @Override
                public void set(int index, int value) {
                    if (index == DATA_CURRENT_SLOT) {
                        tile.setCurrentActiveSlot(value);
                    } else if (index >= DATA_PRIORITY_DOWN && index <= DATA_PRIORITY_EAST) {
                        tile.priorities[index - DATA_PRIORITY_DOWN] = value;
                    } else if (index >= DATA_SIDE_ENABLED_START && index < DATA_SIDE_ENABLED_START + 6) {
                        tile.getSideFilter(index - DATA_SIDE_ENABLED_START).setEnabled(value != 0);
                    } else if (index >= DATA_SIDE_ITEM_MODE_START && index < DATA_SIDE_ITEM_MODE_START + 6) {
                        FilterMode[] values = FilterMode.values();
                        int idx = Math.max(0, Math.min(values.length - 1, value));
                        tile.getSideFilter(index - DATA_SIDE_ITEM_MODE_START).setItemMode(values[idx]);
                    } else if (index >= DATA_SIDE_FLUID_MODE_START && index < DATA_SIDE_FLUID_MODE_START + 6) {
                        FilterMode[] values = FilterMode.values();
                        int idx = Math.max(0, Math.min(values.length - 1, value));
                        tile.getSideFilter(index - DATA_SIDE_FLUID_MODE_START).setFluidMode(values[idx]);
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

        for (int row = 0; row < GHOST_ROWS; row++) {
            for (int col = 0; col < GHOST_COLS; col++) {
                int index = col + row * GHOST_COLS;
                int x = GHOST_ORIGIN_X + col * 18;
                int y = GHOST_ORIGIN_Y + row * 18;
                this.addSlot(new GhostSlot(ghostContainer, index, x, y));
            }
        }

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, INVENTORY_Y, HOTBAR_Y);
    }

    public RoutingNodeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntitySafe(playerInventory, buf.readBlockPos()));
    }

    private static FilteredRoutingNodeBlockEntity getBlockEntitySafe(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level() == null) return null;
        if (playerInventory.player.level().getBlockEntity(pos) instanceof FilteredRoutingNodeBlockEntity tile) {
            return tile;
        }
        return null;
    }

    public int absoluteSlot(int ghostSlot) {
        return currentPage * SideFilterConfig.PAGE_SIZE + ghostSlot;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageCount() {
        if (tile == null) return 1;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return 1;
        return tile.getSideFilter(sideIdx).getPageCount();
    }

    public void setPage(int page) {
        int pages = getPageCount();
        this.currentPage = Math.max(0, Math.min(pages - 1, page));
        refreshGhostContainer();
    }

    /** Toggle whether the item-ghost slots render; the Fluids tab hides them. */
    public void setShowItemGhosts(boolean show) {
        this.showItemGhosts = show;
        refreshGhostContainer();
    }

    /** Mirror item ghosts for the selected side and page into the client container. */
    public void refreshGhostContainer() {
        if (tile == null || !showItemGhosts) {
            for (int i = 0; i < GHOST_SLOT_COUNT; i++) {
                ghostContainer.setItem(i, ItemStack.EMPTY);
            }
            return;
        }
        SideFilterConfig cfg = tile.getSideFilter(Direction.from3DDataValue(tile.getCurrentActiveSlot()));
        for (int i = 0; i < GHOST_SLOT_COUNT; i++) {
            ghostContainer.setItem(i, cfg.getItemGhost(absoluteSlot(i)).copy());
        }
    }

    public FluidStack getCurrentFluidGhost(int ghostSlot) {
        if (tile == null) return FluidStack.EMPTY;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return FluidStack.EMPTY;
        return tile.getSideFilter(sideIdx).getFluidGhost(absoluteSlot(ghostSlot));
    }

    public int getCurrentItemAmount(int ghostSlot) {
        if (tile == null) return 0;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return 0;
        return tile.getSideFilter(sideIdx).getItemAmount(absoluteSlot(ghostSlot));
    }

    public int getCurrentFluidAmount(int ghostSlot) {
        if (tile == null) return 0;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return 0;
        return tile.getSideFilter(sideIdx).getFluidAmount(absoluteSlot(ghostSlot));
    }

    public void setCurrentItemAmountLocal(int ghostSlot, int amount) {
        if (tile == null) return;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return;
        tile.getSideFilter(sideIdx).setItemAmount(absoluteSlot(ghostSlot), amount);
    }

    public void setCurrentFluidAmountLocal(int ghostSlot, int amount) {
        if (tile == null) return;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return;
        tile.getSideFilter(sideIdx).setFluidAmount(absoluteSlot(ghostSlot), amount);
    }

    public void setVisibleItemGhostLocal(int ghostSlot, ItemStack stack) {
        ItemStack ghost = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        ghostContainer.setItem(ghostSlot, ghost);
        if (tile != null) {
            int sideIdx = tile.getCurrentActiveSlot();
            if (sideIdx >= 0 && sideIdx < 6) {
                tile.getSideFilter(sideIdx).setItemGhost(absoluteSlot(ghostSlot), ghost);
            }
        }
    }

    public void setVisibleFluidGhostLocal(int ghostSlot, FluidStack stack) {
        if (tile == null) return;
        int sideIdx = tile.getCurrentActiveSlot();
        if (sideIdx < 0 || sideIdx >= 6) return;
        FluidStack ghost = stack.isEmpty() ? FluidStack.EMPTY : stack.copy();
        if (!ghost.isEmpty()) ghost.setAmount(1);
        tile.getSideFilter(sideIdx).setFluidGhost(absoluteSlot(ghostSlot), ghost);
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

    public boolean isSideEnabled(int sideIndex) {
        if (sideIndex < 0 || sideIndex >= 6) return false;
        return data.get(DATA_SIDE_ENABLED_START + sideIndex) != 0;
    }

    public FilterMode getSideItemMode(int sideIndex) {
        if (sideIndex < 0 || sideIndex >= 6) return FilterMode.WHITELIST;
        int ord = data.get(DATA_SIDE_ITEM_MODE_START + sideIndex);
        FilterMode[] values = FilterMode.values();
        return values[Math.max(0, Math.min(values.length - 1, ord))];
    }

    public FilterMode getSideFluidMode(int sideIndex) {
        if (sideIndex < 0 || sideIndex >= 6) return FilterMode.AUTO_MATCH;
        int ord = data.get(DATA_SIDE_FLUID_MODE_START + sideIndex);
        FilterMode[] values = FilterMode.values();
        return values[Math.max(0, Math.min(values.length - 1, ord))];
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < 6) {
            data.set(DATA_CURRENT_SLOT, slot);
            if (tile != null) {
                tile.swapFilters(slot);
            }
            currentPage = 0;
            refreshGhostContainer();
            broadcastChanges();
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
    public void clicked(int slotId, int dragType, ContainerInput clickType, Player player) {
        if (slotId >= 0 && slotId < GHOST_SLOT_COUNT) {
            Slot slot = this.slots.get(slotId);
            if (slot instanceof GhostSlot) {
                if (clickType == ContainerInput.PICKUP) {
                    if (dragType == 0) {
                        ItemStack carried = this.getCarried();
                        if (!carried.isEmpty()) {
                            ItemStack copy = carried.copy();
                            copy.setCount(1);
                            ghostContainer.setItem(slotId, copy);
                        }
                    } else if (dragType == 1) {
                        ghostContainer.setItem(slotId, ItemStack.EMPTY);
                    }
                }
                return;
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Ghost slots are not real storage; shift-clicks on them are no-ops.
        if (index < GHOST_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            ItemStack copy = slotStack.copy();
            int invStart = GHOST_SLOT_COUNT;
            int invEnd = invStart + 27;
            int hotbarEnd = invEnd + 9;
            if (index >= invStart && index < invEnd) {
                if (!this.moveItemStackTo(slotStack, invEnd, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= invEnd && index < hotbarEnd) {
                if (!this.moveItemStackTo(slotStack, invStart, invEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            return copy;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile != null && Container.stillValidBlockEntity(tile, player);
    }

    private static class GhostSlot extends Slot {
        public GhostSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
