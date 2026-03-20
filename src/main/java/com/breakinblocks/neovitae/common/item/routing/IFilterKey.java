package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for filter keys that define matching criteria for items.
 */
public interface IFilterKey {

    boolean doesStackMatch(ItemStack testStack);

    int getCount();

    void setCount(int count);

    void grow(int changeAmount);

    boolean isEmpty();

    void shrink(int changeAmount);
}
