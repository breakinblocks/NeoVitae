package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Filter key that matches items by their item type.
 */
public class BasicFilterKey implements IFilterKey {

    private final ItemStack keyStack;
    private int count;

    public BasicFilterKey(ItemStack keyStack, int count) {
        this.keyStack = keyStack;
        this.count = count;
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        return !keyStack.isEmpty() && !testStack.isEmpty() && keyStack.getItem() == testStack.getItem();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void shrink(int changeAmount) {
        this.count -= changeAmount;
    }

    @Override
    public void grow(int changeAmount) {
        this.count += changeAmount;
    }

    @Override
    public boolean isEmpty() {
        return count == 0 || keyStack.isEmpty();
    }

    public ItemStack getKeyStack() {
        return keyStack;
    }
}
