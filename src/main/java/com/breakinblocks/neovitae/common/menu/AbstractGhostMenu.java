package com.breakinblocks.neovitae.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import com.breakinblocks.neovitae.NeoVitae;

public abstract class AbstractGhostMenu<T extends AbstractContainerMenu> extends AbstractContainerMenu {

    public AbstractGhostMenu(MenuType<T> type, int containerId, Inventory playerInv, int dataSize, int rows, int columns, int xOff, int yOff, int invOff, int heldSlot) {
        this(type, containerId, playerInv, new SimpleContainerData(dataSize), new GhostItemHandler(rows * columns), rows, columns, xOff, yOff, invOff, heldSlot);
    }

    public AbstractGhostMenu(MenuType<T> type, int containerId, Inventory playerInventory, ContainerData tracker, GhostItemHandler handler, int rows, int columns, int xOff, int yOff, int invOff, int heldSlot) {
        super(type, containerId);
        this.tracker = tracker;
        this.handler = handler;
        this.addDataSlots(tracker);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.addSlot(new GhostSlot(handler, j + i * rows, xOff + j * 21, yOff + i * 21));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, invOff + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            if (i == heldSlot) {
                this.addSlot(new DisabledSlot(playerInventory, i, 8 + i * 18, invOff + 58));
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, invOff + 58));
            }
        }
    }

    public final GhostItemHandler handler;
    public final ContainerData tracker;
    public int getLastGhostSlotClicked() {
        return tracker.get(0);
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        if (slotId < slots.size() && slotId >= 0) {
            Slot slot = slots.get(slotId);
            if (slot instanceof GhostSlot ghostSlot) {
                if ((button == 0 || button == 1)) {
                    ItemStack slotStack = slot.getItem();
                    ItemStack heldStack = this.getCarried();

                    if (button == 0) { // Left mouse click-eth
                        if (heldStack.isEmpty() && !slotStack.isEmpty()) {
                            updateGhostSelection(tracker.get(0), slotId);
                            // Debug: NeoVitae.LOGGER.info("selected ghost slot {} index {}", slotId, slot.getSlotIndex());
                            tracker.set(0, slot.getSlotIndex());
                            return;
                        } else if (!heldStack.isEmpty() && slotStack.isEmpty() && ghostSlot.isValid(heldStack)) {
                            ItemStack copyStack = heldStack.copy();
                            copyStack.setCount(1);
                            slot.set(copyStack);
                        }
                    } else { // Right mouse click-eth away
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    public void updateGhostSelection(int previousSlot, int currentSlot) {}

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot movedSlot = this.slots.get(index);
        if (movedSlot instanceof GhostSlot || movedSlot instanceof DisabledSlot || !movedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = movedSlot.getItem();
        for (Slot target : this.slots) {
            if (target instanceof GhostSlot ghost && !ghost.hasItem() && ghost.isValid(stack)) {
                ItemStack copyStack = stack.copy();
                copyStack.setCount(1);
                ghost.set(copyStack);
                break;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setData(int id, int data) {
        super.setData(id, data);
        this.broadcastChanges();
    }

    public int getData(int id) {
        return this.tracker.get(id);
    }

    public static class GhostSlot extends ResourceHandlerSlot {

        private final GhostItemHandler handler;
        public GhostSlot(GhostItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, itemHandler::set, index, xPosition, yPosition);
            this.handler = itemHandler;
        }

        public boolean isValid(ItemStack stack) {
            return handler.isValid(this.getSlotIndex(), ItemResource.of(stack));
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    public static class DisabledSlot extends Slot {

        public DisabledSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return false;
        }
    }
}
