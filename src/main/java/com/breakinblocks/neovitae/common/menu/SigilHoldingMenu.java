package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.item.sigil.ISigil;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;

/**
 * Menu for the Sigil of Holding - allows inserting/removing sigils and selecting which to use.
 */
public class SigilHoldingMenu extends AbstractContainerMenu {

    private final ItemStack holdingStack;
    private final int holdingSlot;
    private final ItemStackHandler sigilInventory;
    private int selectedSlot;

    public SigilHoldingMenu(int containerId, Inventory playerInventory, ItemStack holdingStack, int holdingSlot) {
        super(BMMenus.SIGIL_HOLDING.get(), containerId);
        this.holdingStack = holdingStack;
        this.holdingSlot = holdingSlot;
        this.selectedSlot = ItemSigilHolding.getCurrentItemOrdinal(holdingStack);

        // Create inventory from the holding sigil's contents
        this.sigilInventory = new ItemStackHandler(ItemSigilHolding.INVENTORY_SIZE) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                // Only allow sigils (but not other holding sigils)
                return stack.getItem() instanceof ISigil && !(stack.getItem() instanceof ItemSigilHolding);
            }

            @Override
            protected void onContentsChanged(int slot) {
                saveToHoldingStack();
            }
        };

        // Load existing contents
        loadFromHoldingStack();

        // Add the 5 sigil slots in a row (36 pixels apart, matching 1.20.1 texture)
        for (int i = 0; i < ItemSigilHolding.INVENTORY_SIZE; i++) {
            final int slotIndex = i;
            this.addSlot(new SlotItemHandler(sigilInventory, i, 8 + i * 36, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return sigilInventory.isItemValid(slotIndex, stack);
                }
            });
        }

        // Player inventory (y = 41)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 41 + row * 18));
            }
        }

        // Player hotbar (y = 99) with special handling for holding sigil slot
        for (int i = 0; i < 9; i++) {
            final int hotbarSlot = i;
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 99) {
                @Override
                public boolean mayPickup(Player player) {
                    // Prevent picking up the holding sigil itself
                    return hotbarSlot != holdingSlot;
                }
            });
        }
    }

    public SigilHoldingMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getHoldingStack(playerInventory.player, buf.readInt()), buf.readInt());
    }

    private static ItemStack getHoldingStack(Player player, int slot) {
        return player.getInventory().getItem(slot);
    }

    private void loadFromHoldingStack() {
        NonNullList<ItemStack> inv = ItemSigilHolding.getInternalInventory(holdingStack);
        for (int i = 0; i < ItemSigilHolding.INVENTORY_SIZE; i++) {
            sigilInventory.setStackInSlot(i, inv.get(i).copy());
        }
    }

    private void saveToHoldingStack() {
        NonNullList<ItemStack> inv = NonNullList.withSize(ItemSigilHolding.INVENTORY_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < ItemSigilHolding.INVENTORY_SIZE; i++) {
            inv.set(i, sigilInventory.getStackInSlot(i).copy());
        }
        holdingStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inv));
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < ItemSigilHolding.INVENTORY_SIZE) {
            this.selectedSlot = slot;
            holdingStack.set(BMDataComponents.READER_STATE.get(), slot);
        }
    }

    public ItemStackHandler getSigilInventory() {
        return sigilInventory;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // From sigil slots to player inventory
            if (index < ItemSigilHolding.INVENTORY_SIZE) {
                if (!this.moveItemStackTo(slotStack, ItemSigilHolding.INVENTORY_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From player inventory to sigil slots
            else {
                if (slotStack.getItem() instanceof ISigil && !(slotStack.getItem() instanceof ItemSigilHolding)) {
                    if (!this.moveItemStackTo(slotStack, 0, ItemSigilHolding.INVENTORY_SIZE, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
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
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // Prevent moving the holding sigil
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.getItem().getItem() instanceof ItemSigilHolding) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        // Valid as long as the player still has the holding sigil
        ItemStack currentStack = player.getInventory().getItem(holdingSlot);
        return currentStack.getItem() instanceof ItemSigilHolding;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveToHoldingStack();
    }
}
