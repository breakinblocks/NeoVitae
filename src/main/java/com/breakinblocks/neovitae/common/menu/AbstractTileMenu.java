package com.breakinblocks.neovitae.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Base class for Blood Magic menus that interact with block entities.
 * Provides common quickMoveStack (shift-click) handling with a template method pattern.
 *
 * @param <T> The block entity type this menu interacts with
 */
public abstract class AbstractTileMenu<T extends BlockEntity> extends AbstractContainerMenu {

    public final T tile;
    protected final int playerSlotsStart;

    /**
     * @param type The menu type
     * @param containerId The container ID
     * @param tile The block entity this menu is for
     * @param playerSlotsStart The index where player inventory slots begin (after all tile slots)
     */
    protected AbstractTileMenu(MenuType<?> type, int containerId, T tile, int playerSlotsStart) {
        super(type, containerId);
        this.tile = tile;
        this.playerSlotsStart = playerSlotsStart;
    }

    public T getTile() {
        return tile;
    }

    /**
     * Standard quickMoveStack implementation using template method pattern.
     * Subclasses override {@link #handleQuickMoveStack} to provide specific move logic.
     */
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

    /**
     * Handle the specific move logic for shift-click transfer.
     * Called by {@link #quickMoveStack} after initial setup.
     *
     * @param index The slot index being shift-clicked
     * @param slotStack The stack in the slot (will be modified by moveItemStackTo)
     * @param originalCopy A copy of the original stack (for onQuickCraft calls)
     * @param slot The slot being shift-clicked
     * @return true if the move was handled successfully, false to return ItemStack.EMPTY
     */
    protected abstract boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot);

    /**
     * Helper to move items from tile slots to player inventory.
     * @param stack The stack to move
     * @param reverseDirection If true, tries hotbar first then main inventory
     * @return true if any items were moved
     */
    protected boolean moveToPlayer(ItemStack stack, boolean reverseDirection) {
        return this.moveItemStackTo(stack, playerSlotsStart, playerSlotsStart + 36, reverseDirection);
    }

    /**
     * Helper to move items from player inventory to tile slots.
     * @param stack The stack to move
     * @param startSlot First tile slot index (inclusive)
     * @param endSlot Last tile slot index (exclusive)
     * @return true if any items were moved
     */
    protected boolean moveToTileSlots(ItemStack stack, int startSlot, int endSlot) {
        return this.moveItemStackTo(stack, startSlot, endSlot, false);
    }

    /**
     * Check if the given index is in the player inventory range.
     */
    protected boolean isPlayerSlot(int index) {
        return index >= playerSlotsStart;
    }

    /**
     * Check if the given index is in the tile's slot range.
     */
    protected boolean isTileSlot(int index) {
        return index < playerSlotsStart;
    }
}
