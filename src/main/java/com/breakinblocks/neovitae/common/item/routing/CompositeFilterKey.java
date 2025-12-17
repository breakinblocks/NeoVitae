package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter key that combines multiple filter keys - an item must match ALL contained keys.
 */
public class CompositeFilterKey implements IFilterKey {
    private final List<IFilterKey> keyList = new ArrayList<>();
    private int count;

    public CompositeFilterKey(int count) {
        this.count = count;
    }

    public void addFilterKey(IFilterKey key) {
        // Don't allow nested composite keys
        if (!(key instanceof CompositeFilterKey)) {
            keyList.add(key);
        }
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        if (testStack.isEmpty()) {
            return false;
        }

        // All keys must match
        for (IFilterKey key : keyList) {
            if (!key.doesStackMatch(testStack)) {
                return false;
            }
        }

        return true;
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
        return count == 0;
    }

    public List<IFilterKey> getKeyList() {
        return keyList;
    }
}
