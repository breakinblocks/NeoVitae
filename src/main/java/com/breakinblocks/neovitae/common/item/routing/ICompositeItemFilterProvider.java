package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for filters that can contain other nested filters.
 */
public interface ICompositeItemFilterProvider extends IItemFilterProvider {

    boolean nestFilter(ItemStack mainStack, ItemStack nestedStack);

    boolean canReceiveNestedFilter(ItemStack mainStack, ItemStack nestedStack);
}
