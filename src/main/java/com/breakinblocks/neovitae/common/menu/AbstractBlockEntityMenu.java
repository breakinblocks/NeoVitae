package com.breakinblocks.neovitae.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractBlockEntityMenu<T extends BlockEntity> extends AbstractContainerMenu {

    public final T tile;
    protected final int playerSlotsStart;

    protected AbstractBlockEntityMenu(MenuType<?> type, int containerId, T tile, int playerSlotsStart) {
        super(type, containerId);
        this.tile = tile;
        this.playerSlotsStart = playerSlotsStart;
    }

    public T getTile() {
        return tile;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (!handleQuickMoveStack(index, slotStack, result, slot)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return result;
    }

    protected abstract boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot);

    protected boolean moveToPlayer(ItemStack stack, boolean reverseDirection) {
        return this.moveItemStackTo(stack, playerSlotsStart, playerSlotsStart + 36, reverseDirection);
    }

    protected boolean moveToTileSlots(ItemStack stack, int startSlot, int endSlot) {
        return this.moveItemStackTo(stack, startSlot, endSlot, false);
    }

    protected boolean isPlayerSlot(int index) {
        return index >= playerSlotsStart;
    }

    protected boolean isTileSlot(int index) {
        return index < playerSlotsStart;
    }
}
