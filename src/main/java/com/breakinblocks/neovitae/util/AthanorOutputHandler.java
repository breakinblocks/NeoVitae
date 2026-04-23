package com.breakinblocks.neovitae.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AthanorOutputHandler {
    private ItemStack[] items;
    private final int stackLimit;

    public AthanorOutputHandler(int size, int limit) {
        this.stackLimit = limit;
        this.items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            this.items[i] = ItemStack.EMPTY;
        }
    }

    public AthanorOutputHandler(ItemStack[] items, int limit) {
        this.stackLimit = limit;
        this.items = items;
    }

    public int getSlots() {
        return items.length;
    }

    public ItemStack getStackInSlot(int slot) {
        return items[slot];
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.items[slot] = stack;
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = getStackInSlot(slot);
        int maxStackSize;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) {
                return stack;
            }
            if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                return stack;
            }
            if (!isItemValid(slot, stack)) {
                return stack;
            }

            maxStackSize = Math.min(stack.getMaxStackSize(), getSlotLimit(slot) - stackInSlot.getCount());
            if (stack.getCount() <= maxStackSize) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot, copy);
                }

                return ItemStack.EMPTY;
            } else {
                ItemStack tmpStack = stack.copy();
                if (!simulate) {
                    ItemStack copy = tmpStack.split(maxStackSize);
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(slot, copy);
                    return tmpStack;
                } else {
                    tmpStack.shrink(maxStackSize);
                    return tmpStack;
                }
            }
        } else {
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            maxStackSize = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (maxStackSize < stack.getCount()) {
                ItemStack tmpStack = stack.copy();
                if (!simulate) {
                    setInventorySlotContents(slot, tmpStack.split(maxStackSize));
                    return tmpStack;
                } else {
                    tmpStack.shrink(maxStackSize);
                    return tmpStack;
                }
            } else {
                if (!simulate) {
                    setInventorySlotContents(slot, stack);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = getStackInSlot(slot);
        if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int maxTransfer = Math.min(stackInSlot.getCount(), amount);
            ItemStack decrStackSize = decrStackSize(slot, maxTransfer);
            return decrStackSize;
        }
    }

    public boolean canTransferAllItemsToSlots(List<ItemStack> stackList, boolean simulate) {
        ItemStack[] copyList = new ItemStack[items.length];
        for (int i = 0; i < copyList.length; i++) {
            copyList[i] = items[i].copy();
        }

        boolean hasStashedAll = true;

        for (ItemStack stack : stackList) {
            if (stack.isEmpty()) {
                continue;
            }

            slots: for (int slot = 0; slot < copyList.length; slot++) {
                ItemStack stackInSlot = copyList[slot];

                int m;
                if (!stackInSlot.isEmpty()) {
                    if(stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) {
                        continue;
                    }
                    if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                        continue;
                    }
                    if (!isItemValid(slot, stack)) {
                        continue;
                    }

                    m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot) - stackInSlot.getCount());
                    if (stack.getCount() <= m) {
                        ItemStack copy = stack.copy();
                        if (!simulate) {
                            copy.grow(stackInSlot.getCount());
                            copyList[slot] = copy;
                        }
                        stack = ItemStack.EMPTY;
                        break slots;
                    } else {
                        ItemStack tmpStack = stack.copy();
                        if (!simulate) {
                            ItemStack copy = tmpStack.split(m);
                            copy.grow(stackInSlot.getCount());
                            copyList[slot] = copy;
                        } else {
                            tmpStack.shrink(m);
                        }
                    }
                } else {
                    if (!isItemValid(slot, stack)) {
                        continue;
                    }
                    m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
                    if (m < stack.getCount()) {
                        ItemStack tmpStack = stack.copy();
                        if (!simulate) {
                            copyList[slot] = tmpStack.split(m);
                        } else {
                            tmpStack.shrink(m);
                        }
                    } else {
                        if (!simulate) {
                            copyList[slot] = stack;
                        }
                        stack = ItemStack.EMPTY;
                    }
                }
            }
            if (!stack.isEmpty()) {
                hasStashedAll = false;
            }
        }
        if (!simulate) {
            items = copyList;
        }
        return hasStashedAll;
    }

    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemStack = getStackInSlot(slot);
        if (!itemStack.isEmpty()) {
            if (itemStack.getCount() <= amount) {
                setInventorySlotContents(slot, ItemStack.EMPTY);
                return itemStack;
            }
            return itemStack.split(amount);
        }

        return ItemStack.EMPTY;
    }

    public int getSlotLimit(int slot) {
        return this.stackLimit;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
