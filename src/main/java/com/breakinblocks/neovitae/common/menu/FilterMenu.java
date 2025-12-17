package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.common.item.inventory.InventoryFilter;
import com.breakinblocks.neovitae.common.item.routing.IRoutingFilterProvider;
import com.breakinblocks.neovitae.common.item.routing.ItemRouterFilter;
import com.breakinblocks.neovitae.common.item.routing.ItemTagFilter;
import com.breakinblocks.neovitae.util.GhostItemHelper;

/**
 * Menu for configuring item routing filters.
 * Contains ghost item slots for setting up filter patterns.
 */
public class FilterMenu extends AbstractContainerMenu {
    public final InventoryFilter filterInventory;
    public final Player player;
    public final ItemStack filterStack;
    public final boolean isTag;
    private final ContainerData data;
    private final int currentSlotHeldIn;

    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;

    public FilterMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.getMainHandItem(), buf.readBoolean());
    }

    public FilterMenu(int containerId, Inventory playerInventory, ItemStack filterStack, boolean isTag) {
        super(BMMenus.FILTER.get(), containerId);
        this.player = playerInventory.player;
        this.filterStack = filterStack;
        this.isTag = isTag;
        this.currentSlotHeldIn = player.getInventory().selected;

        // Create filter inventory from stack data
        this.filterInventory = new InventoryFilter(ItemRouterFilter.INVENTORY_SIZE) {
            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                super.setStackInSlot(slot, stack);
                saveToFilterStack();
            }
        };

        // Load existing inventory from filter stack
        loadFromFilterStack();

        // Create data container for syncing button states
        this.data = new SimpleContainerData(ItemRouterFilter.DATA_COUNT) {
            @Override
            public int get(int index) {
                if (index == ItemRouterFilter.DATA_SLOT) {
                    return -1; // Selected slot (client tracking)
                } else if (index == ItemRouterFilter.DATA_BWLIST) {
                    return ItemRouterFilter.getBlacklistState(filterStack);
                } else if (index >= ItemRouterFilter.DATA_TAG && index < ItemRouterFilter.DATA_COUNT) {
                    int slot = index - ItemRouterFilter.DATA_TAG;
                    return ItemTagFilter.getItemTagIndex(filterStack, slot);
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == ItemRouterFilter.DATA_BWLIST) {
                    ItemRouterFilter.setBlacklistState(filterStack, value);
                } else if (index >= ItemRouterFilter.DATA_TAG && index < ItemRouterFilter.DATA_COUNT) {
                    int slot = index - ItemRouterFilter.DATA_TAG;
                    ItemTagFilter.setItemTagIndex(filterStack, slot, value);
                }
            }

            @Override
            public int getCount() {
                return ItemRouterFilter.DATA_COUNT;
            }
        };
        this.addDataSlots(data);

        setupSlots(playerInventory);
    }

    private void loadFromFilterStack() {
        FilterInventory inv = ItemRouterFilter.getFilterInventory(filterStack);
        for (int i = 0; i < ItemRouterFilter.INVENTORY_SIZE; i++) {
            filterInventory.setStackInSlot(i, inv.getItem(i));
        }
    }

    private void saveToFilterStack() {
        FilterInventory currentInv = ItemRouterFilter.getFilterInventory(filterStack);
        java.util.List<ItemStack> items = new java.util.ArrayList<>(ItemRouterFilter.INVENTORY_SIZE);
        for (int i = 0; i < ItemRouterFilter.INVENTORY_SIZE; i++) {
            items.add(filterInventory.getStackInSlot(i));
        }
        FilterInventory newInv = new FilterInventory(items, currentInv.tagIndices());
        ItemRouterFilter.setFilterInventory(filterStack, newInv);
    }

    private void setupSlots(Inventory playerInv) {
        // Ghost item slots (3x3 grid)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotGhostItem(filterInventory, col + row * 3, 110 + col * 21, 15 + row * 21));
            }
        }

        // Player inventory (3 rows)
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 105 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < PLAYER_INVENTORY_COLUMNS; col++) {
            if (col == currentSlotHeldIn) {
                this.addSlot(new SlotDisabled(playerInv, col, 8 + col * 18, 163));
            } else {
                this.addSlot(new Slot(playerInv, col, 8 + col * 18, 163));
            }
        }
    }

    public int getData(int index) {
        return data.get(index);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == ItemRouterFilter.BUTTON_BWLIST) {
            int state = data.get(ItemRouterFilter.DATA_BWLIST);
            setData(ItemRouterFilter.DATA_BWLIST, state == 0 ? 1 : 0);
            return true;
        } else if (buttonId == ItemRouterFilter.BUTTON_TAG && isTag) {
            // Cycle tag selection - need to find selected slot first
            // This is handled via network packet from screen
            return true;
        }
        return false;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < ItemRouterFilter.INVENTORY_SIZE) {
            Slot slot = this.slots.get(slotId);

            if (slot instanceof SlotGhostItem) {
                if (dragType == 0 || dragType == 1) {
                    ItemStack slotStack = slot.getItem();
                    ItemStack heldStack = this.getCarried();

                    if (dragType == 0) { // Left click
                        if (heldStack.isEmpty() && !slotStack.isEmpty()) {
                            // Select slot (handled in screen)
                            return;
                        } else if (!heldStack.isEmpty() && slotStack.isEmpty()) {
                            // Place ghost item
                            ItemStack copyStack = heldStack.copy();
                            GhostItemHelper.setItemGhostAmount(copyStack, 0);
                            copyStack.setCount(1);
                            slot.set(copyStack);
                        }
                    } else { // Right click - clear slot
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void setData(int id, int value) {
        super.setData(id, value);
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        // No shift-clicking for ghost items
        return ItemStack.EMPTY;
    }

    /**
     * Ghost slot that doesn't actually store items - just references.
     */
    public class SlotGhostItem extends SlotItemHandler {
        public SlotGhostItem(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }

    /**
     * Disabled slot to prevent picking up the filter item itself.
     */
    private class SlotDisabled extends Slot {
        public SlotDisabled(Container inventory, int slotIndex, int x, int y) {
            super(inventory, slotIndex, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
