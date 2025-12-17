package com.breakinblocks.neovitae.common.routing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.item.routing.IFilterKey;

import java.util.List;

/**
 * Interface for item filters used in the routing system.
 */
public interface IItemFilter extends IRoutingFilter {

    /**
     * Initializes the filter with the given filter list and inventory context.
     * @param filteredList The list of filter keys
     * @param tile The block entity this filter is attached to
     * @param itemHandler The item handler to filter
     * @param isFilterOutput True if this is an output filter
     */
    void initializeFilter(List<IFilterKey> filteredList, BlockEntity tile, IItemHandler itemHandler, boolean isFilterOutput);

    /**
     * Initializes the filter with just the filter list (no inventory context).
     */
    void initializeFilter(List<IFilterKey> filteredList);

    /**
     * Transfers an item stack through the output filter.
     * Called when the output inventory receives items.
     * @param inputStack The stack to filter
     * @return The remainder after absorption into the inventory
     */
    ItemStack transferStackThroughOutputFilter(ItemStack inputStack);

    /**
     * Transfers items from the input inventory to the output filter.
     * @param outputFilter The output filter to transfer to
     * @param maxTransfer Maximum number of items to transfer
     * @return The number of items actually transferred
     */
    int transferThroughInputFilter(IItemFilter outputFilter, int maxTransfer);

    /**
     * Checks if the given stack passes through this filter.
     */
    boolean doesStackPassFilter(ItemStack testStack);

    /**
     * Checks if the filter key matches the test stack.
     */
    boolean doStacksMatch(IFilterKey filterStack, ItemStack testStack);

    /**
     * Gets the filter list. The returned list should not be modified.
     */
    List<IFilterKey> getFilterList();
}
