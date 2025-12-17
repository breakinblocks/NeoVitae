package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Filter key that matches items by their mod namespace.
 */
public class ModFilterKey implements IFilterKey {

    private final String namespace;
    private int count;

    public ModFilterKey(String namespace, int count) {
        this.namespace = namespace;
        this.count = count;
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        if (testStack.isEmpty()) return false;
        var key = BuiltInRegistries.ITEM.getKey(testStack.getItem());
        return key.getNamespace().equals(namespace);
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

    public String getNamespace() {
        return namespace;
    }
}
