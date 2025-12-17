package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for filter keys that define matching criteria for items.
 */
public interface IFilterKey {

    /**
     * Checks if the test stack matches this filter key.
     */
    boolean doesStackMatch(ItemStack testStack);

    /**
     * Gets the count/amount for this filter key.
     */
    int getCount();

    /**
     * Sets the count/amount for this filter key.
     */
    void setCount(int count);

    /**
     * Increases the count by the given amount.
     */
    void grow(int changeAmount);

    /**
     * Checks if this filter key is empty (count <= 0).
     */
    boolean isEmpty();

    /**
     * Decreases the count by the given amount.
     */
    void shrink(int changeAmount);
}
