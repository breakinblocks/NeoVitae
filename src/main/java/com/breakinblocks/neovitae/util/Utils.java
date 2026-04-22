package com.breakinblocks.neovitae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

public class Utils {

    public static ItemStack insertStackIntoTile(ItemStack stack, BlockEntity tile, Direction dir) {
        IItemHandler handler = lookupItemHandler(tile, dir);

        if (handler != null) {
            return insertStackIntoTile(stack, handler);
        } else if (tile instanceof Container container) {
            return insertStackIntoInventory(stack, container, dir);
        }

        return stack;
    }

    @Nullable
    private static IItemHandler lookupItemHandler(@Nullable BlockEntity tile, @Nullable Direction dir) {
        if (tile == null || tile.getLevel() == null) return null;
        var rh = tile.getLevel().getCapability(Capabilities.Item.BLOCK, tile.getBlockPos(), dir);
        return rh != null ? IItemHandler.of(rh) : null;
    }

    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler) {
        return insertStackIntoTile(stack, handler, false);
    }

    /**
     * @param doCleanly If true, tries to stack with existing items first
     */
    public static ItemStack insertStackIntoTile(ItemStack stack, IItemHandler handler, boolean doCleanly) {
        int numberOfSlots = handler.getSlots();
        ItemStack copyStack = stack.copy();

        if (doCleanly) {
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

        for (int slot = 0; slot < numberOfSlots; slot++) {
            copyStack = handler.insertItem(slot, copyStack, false);
            if (copyStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return copyStack;
    }

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

    public static int getNumberOfFreeSlots(BlockEntity tile, Direction dir) {
        int slots = 0;

        IItemHandler handler = lookupItemHandler(tile, dir);

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

    public static void spawnStackAtBlock(Level level, BlockPos pos, Direction dir, ItemStack stack) {
        if (stack.isEmpty() || level.isClientSide()) return;

        double x = pos.getX() + 0.5 + dir.getStepX() * 0.6;
        double y = pos.getY() + 0.5 + dir.getStepY() * 0.6;
        double z = pos.getZ() + 0.5 + dir.getStepZ() * 0.6;

        ItemEntity entity = new ItemEntity(level, x, y, z, stack.copy());
        entity.setDeltaMovement(dir.getStepX() * 0.05, dir.getStepY() * 0.05 + 0.1, dir.getStepZ() * 0.05);
        level.addFreshEntity(entity);
    }

    @Nullable
    public static IItemHandler getInventory(BlockEntity tile, @Nullable Direction facing) {
        if (tile == null || tile.getLevel() == null) return null;
        if (facing == null) facing = Direction.DOWN;

        IItemHandler handler = lookupItemHandler(tile, facing);

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
