package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import com.breakinblocks.neovitae.common.routing.IItemFilter;

import java.util.List;

/**
 * Interface for items that provide item filters for the routing system.
 */
public interface IItemFilterProvider extends IRoutingFilterProvider {

    /**
     * Gets an input filter from this provider.
     */
    IItemFilter getInputItemFilter(ItemStack stack, BlockEntity tile, IItemHandler handler);

    /**
     * Gets an output filter from this provider.
     */
    IItemFilter getOutputItemFilter(ItemStack stack, BlockEntity tile, IItemHandler handler);

    /**
     * Gets an uninitialized filter (for filters that only check stacks without inventory context).
     */
    IItemFilter getUninitializedItemFilter(ItemStack stack);

    /**
     * Sets the amount for a ghost item in the filter.
     */
    void setGhostItemAmount(ItemStack filterStack, int ghostItemSlot, int amount);

    /**
     * Gets tooltip text for a ghost item slot.
     */
    List<Component> getTextForHoverItem(ItemStack filterStack, String buttonKey, int ghostItemSlot);

    /**
     * Gets the current button state. Returns -1 for invalid input.
     */
    int getCurrentButtonState(ItemStack filterStack, String buttonKey, int ghostItemSlot);

    /**
     * Gets the texture position for the current button state.
     * @return Pair of (u, v) texture coordinates
     */
    Pair<Integer, Integer> getTexturePositionForState(ItemStack filterStack, String buttonKey, int currentButtonState);

    /**
     * Handles a button press. Returns the new state, or -1 for invalid input.
     */
    int receiveButtonPress(ItemStack filterStack, String buttonKey, int ghostItemSlot, int currentButtonState);

    /**
     * Checks if the button applies globally (not per-slot).
     */
    boolean isButtonGlobal(ItemStack filterStack, String buttonKey);

    /**
     * Gets a filter key for the given ghost slot configuration.
     */
    IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount);
}
