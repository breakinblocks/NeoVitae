package com.breakinblocks.neovitae.api.routing;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Interface for items that provide configurable item filters for the routing system.
 * <p>
 * Extend this to create custom filter items that can be placed in routing node
 * filter slots. The filter item controls which items pass through and in what quantities.
 */
public interface IItemFilterProvider extends IRoutingFilterProvider {

    IItemFilter getInputItemFilter(ItemStack stack, BlockEntity tile, IItemHandler handler);

    IItemFilter getOutputItemFilter(ItemStack stack, BlockEntity tile, IItemHandler handler);

    IItemFilter getUninitializedItemFilter(ItemStack stack);

    void setGhostItemAmount(ItemStack filterStack, int ghostItemSlot, int amount);

    List<Component> getTextForHoverItem(ItemStack filterStack, String buttonKey, int ghostItemSlot);

    int getCurrentButtonState(ItemStack filterStack, String buttonKey, int ghostItemSlot);

    Pair<Integer, Integer> getTexturePositionForState(ItemStack filterStack, String buttonKey, int currentButtonState);

    int receiveButtonPress(ItemStack filterStack, String buttonKey, int ghostItemSlot, int currentButtonState);

    boolean isButtonGlobal(ItemStack filterStack, String buttonKey);

    IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount);
}
