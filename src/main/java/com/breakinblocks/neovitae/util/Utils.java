package com.breakinblocks.neovitae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;

public class Utils {

    public static ItemStack insertStackIntoTile(ItemStack stack, BlockEntity tile, Direction dir) {
        ResourceHandler<ItemResource> handler = lookupItemHandler(tile, dir);
        if (handler != null) {
            return insertStackIntoTile(stack, handler);
        } else if (tile instanceof Container container) {
            return insertStackIntoInventory(stack, container, dir);
        }
        return stack;
    }

    @Nullable
    private static ResourceHandler<ItemResource> lookupItemHandler(@Nullable BlockEntity tile, @Nullable Direction dir) {
        if (tile == null || tile.getLevel() == null) return null;
        return tile.getLevel().getCapability(Capabilities.Item.BLOCK, tile.getBlockPos(), dir);
    }

    public static ItemStack insertStackIntoTile(ItemStack stack, ResourceHandler<ItemResource> handler) {
        return insertStackIntoTile(stack, handler, false);
    }

    public static ItemStack insertStackIntoTile(ItemStack stack, ResourceHandler<ItemResource> handler, boolean doCleanly) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemResource resource = ItemResource.of(stack);
        int remaining = stack.getCount();
        int numberOfSlots = handler.size();

        try (Transaction tx = Transaction.openRoot()) {
            if (doCleanly) {
                for (int slot = 0; slot < numberOfSlots && remaining > 0; slot++) {
                    if (!resource.matches(handler.getResource(slot).toStack(1))) continue;
                    remaining -= handler.insert(slot, resource, remaining, tx);
                }
            }
            for (int slot = 0; slot < numberOfSlots && remaining > 0; slot++) {
                remaining -= handler.insert(slot, resource, remaining, tx);
            }
            tx.commit();
        }

        if (remaining == stack.getCount()) return stack;
        if (remaining == 0) return ItemStack.EMPTY;
        ItemStack leftover = stack.copy();
        leftover.setCount(remaining);
        return leftover;
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

        ResourceHandler<ItemResource> handler = lookupItemHandler(tile, dir);

        if (handler != null) {
            for (int i = 0; i < handler.size(); i++) {
                if (handler.getResource(i).isEmpty()) {
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
    public static ResourceHandler<ItemResource> getInventory(BlockEntity tile, @Nullable Direction facing) {
        if (tile == null || tile.getLevel() == null) return null;
        if (facing == null) facing = Direction.DOWN;

        ResourceHandler<ItemResource> handler = lookupItemHandler(tile, facing);
        if (handler != null) return handler;

        if (tile instanceof Container container) {
            return VanillaContainerWrapper.of(container);
        }
        return null;
    }

    public static ItemStack stackAt(ResourceHandler<ItemResource> handler, int slot) {
        ItemResource r = handler.getResource(slot);
        return r.isEmpty() ? ItemStack.EMPTY : r.toStack(handler.getAmountAsInt(slot));
    }

    public static ItemStack extractItem(ResourceHandler<ItemResource> handler, int slot, int amount, boolean simulate) {
        ItemResource r = handler.getResource(slot);
        if (r.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = handler.extract(slot, r, amount, tx);
            if (extracted <= 0) return ItemStack.EMPTY;
            if (!simulate) tx.commit();
            return r.toStack(extracted);
        }
    }

    public static ItemStack insertItemStacked(ResourceHandler<ItemResource> handler, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemResource resource = ItemResource.of(stack);
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = handler.insert(resource, stack.getCount(), tx);
            if (!simulate) tx.commit();
            if (inserted >= stack.getCount()) return ItemStack.EMPTY;
            ItemStack remainder = stack.copy();
            remainder.shrink(inserted);
            return remainder;
        }
    }
}
