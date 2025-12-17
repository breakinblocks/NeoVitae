package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for filters that can contain other nested filters.
 */
public interface ICompositeItemFilterProvider extends IItemFilterProvider {

    /**
     * Nests another filter inside this composite filter.
     * @param mainStack The composite filter stack
     * @param nestedStack The filter to nest
     * @return True if nesting was successful
     */
    boolean nestFilter(ItemStack mainStack, ItemStack nestedStack);

    /**
     * Checks if this filter can receive the given nested filter.
     */
    boolean canReceiveNestedFilter(ItemStack mainStack, ItemStack nestedStack);
}
