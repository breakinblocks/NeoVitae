package com.breakinblocks.neovitae.util;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

/**
 * General utility methods for Blood Magic.
 */
public class Utils {

    /**
     * Inserts an item stack into a tile entity's inventory.
     */
    public static ItemStack insertStackIntoTile(ItemStack stack, BlockEntity tile, Direction dir) {
        IItemHandler handler = tile.getLevel().getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                tile.getBlockPos(), dir);

        if (handler != null) {
            return insertStackIntoTile(stack, handler);
        } else if (tile instanceof Container container) {
            return insertStackIntoInventory(stack, container, dir);
        }

        return stack;
    }

    /**
     * Inserts a stack into an item handler.
     */
    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler) {
        return insertStackIntoTile(stack, handler, false);
    }

    /**
     * Inserts a stack into an item handler.
     * @param doCleanly If true, tries to stack with existing items first
     */
    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler, boolean doCleanly) {
        int numberOfSlots = handler.getSlots();
        ItemStack copyStack = stack.copy();

        if (doCleanly) {
            // First pass: try to stack with existing items
            for (int slot = 0; slot < numberOfSlots; slot++) {
                ItemStack containedStack = handler.getStackInSlot(slot);
                if (ItemStack.isSameItemSameComponents(stack, containedStack)) {
                    copyStack = handler.insertItem(slot, copyStack, false);
                    if (copyStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        // Second pass: insert into any available slot
        for (int slot = 0; slot < numberOfSlots; slot++) {
            copyStack = handler.insertItem(slot, copyStack, false);
            if (copyStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return copyStack;
    }

    /**
     * Inserts a stack into a container inventory.
     */
    public static ItemStack insertStackIntoInventory(ItemStack stack, Container inventory, Direction dir) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack copyStack = stack.copy();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack slotStack = inventory.getItem(slot);

            if (slotStack.isEmpty()) {
                inventory.setItem(slot, copyStack);
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, copyStack)) {
                int maxSize = Math.min(slotStack.getMaxStackSize(), inventory.getMaxStackSize());
                int space = maxSize - slotStack.getCount();
                if (space > 0) {
                    int toTransfer = Math.min(space, copyStack.getCount());
                    slotStack.grow(toTransfer);
                    copyStack.shrink(toTransfer);
                    if (copyStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        return copyStack;
    }

    /**
     * Counts the number of free slots in a tile's inventory.
     */
    public static int getNumberOfFreeSlots(BlockEntity tile, Direction dir) {
        int slots = 0;

        IItemHandler handler = tile.getLevel().getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                tile.getBlockPos(), dir);

        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).isEmpty()) {
                    slots++;
                }
            }
        } else if (tile instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                if (container.getItem(i).isEmpty()) {
                    slots++;
                }
            }
        }

        return slots;
    }

    /**
     * Gets an item handler for a block entity.
     */
    @Nullable
    public static IItemHandler getInventory(BlockEntity tile, @Nullable Direction facing) {
        if (tile == null || tile.getLevel() == null) return null;
        if (facing == null) facing = Direction.DOWN;

        IItemHandler handler = tile.getLevel().getCapability(
                Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), facing);

        if (handler != null) {
            return handler;
        } else if (tile instanceof WorldlyContainer worldly) {
            int[] slots = worldly.getSlotsForFace(facing);
            return slots.length != 0 ? new SidedInvWrapper(worldly, facing) : null;
        } else if (tile instanceof Container container) {
            return new InvWrapper(container);
        }

        return null;
    }
}
