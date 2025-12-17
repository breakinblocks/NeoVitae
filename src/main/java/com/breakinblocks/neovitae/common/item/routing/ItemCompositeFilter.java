package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.common.datacomponent.NestedFilterInventory;
import com.breakinblocks.neovitae.common.routing.BasicItemFilter;
import com.breakinblocks.neovitae.common.routing.BlacklistItemFilter;
import com.breakinblocks.neovitae.common.routing.IItemFilter;
import com.breakinblocks.neovitae.util.GhostItemHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite filter that combines multiple nested filters.
 * An item must match ALL nested filters to pass through.
 * Can also have its own ghost items that must match.
 */
public class ItemCompositeFilter extends ItemRouterFilter implements ICompositeItemFilterProvider {

    public ItemCompositeFilter() {
        super();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Composite filter doesn't have its own GUI - it uses nesting from routing nodes
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.neovitae.filter.composite");
    }

    @Override
    public void appendHoverText(ItemStack filterStack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.compositefilter.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

        List<ItemStack> nestedFilters = getNestedFilters(filterStack);
        if (!nestedFilters.isEmpty()) {
            boolean sneaking = Screen.hasShiftDown();
            if (!sneaking) {
                tooltip.add(Component.translatable("tooltip.neovitae.extraInfo").withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("tooltip.neovitae.contained_filters").withStyle(ChatFormatting.BLUE));
                for (ItemStack nestedStack : nestedFilters) {
                    tooltip.add(nestedStack.getHoverName());
                }
            }
        }

        int whitelistState = getBlacklistState(filterStack);
        boolean isWhitelist = whitelistState == 0;

        if (isWhitelist) {
            tooltip.add(Component.translatable("tooltip.neovitae.filter.whitelist").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.neovitae.filter.blacklist").withStyle(ChatFormatting.GRAY));
        }

        FilterInventory inv = getFilterInventory(filterStack);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (isWhitelist) {
                int amount = GhostItemHelper.getItemGhostAmount(stack);
                if (amount > 0) {
                    tooltip.add(Component.translatable("tooltip.neovitae.filter.count", amount, stack.getHoverName()));
                } else {
                    tooltip.add(Component.translatable("tooltip.neovitae.filter.all", stack.getHoverName()));
                }
            } else {
                tooltip.add(stack.getHoverName());
            }
        }
    }

    @Override
    public IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount) {
        // Composite filter returns null for individual filter keys - it builds composite keys instead
        return null;
    }

    @Override
    public IItemFilter getInputItemFilter(ItemStack filterStack, BlockEntity tile, IItemHandler handler) {
        IItemFilter testFilter = getFilterTypeFromConfig(filterStack);

        List<IFilterKey> filteredList = new ArrayList<>();
        FilterInventory inv = getFilterInventory(filterStack);

        List<ItemStack> nestedList = getNestedFilters(filterStack);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            int amount = GhostItemHelper.getItemGhostAmount(stack);
            ItemStack ghostStack = GhostItemHelper.getSingleStackFromGhost(stack);

            // Create a composite key that combines all nested filter keys
            CompositeFilterKey compositeKey = new CompositeFilterKey(amount);

            // Add the basic filter key for this ghost item
            compositeKey.addFilterKey(new BasicFilterKey(ghostStack, amount));

            // Add filter keys from all nested filters
            for (ItemStack nestedStack : nestedList) {
                if (nestedStack.getItem() instanceof INestableItemFilterProvider nestedFilter) {
                    IFilterKey nestedKey = nestedFilter.getFilterKey(nestedStack, i, ghostStack, amount);
                    if (nestedKey != null) {
                        compositeKey.addFilterKey(nestedKey);
                    }
                }
            }

            filteredList.add(compositeKey);
        }

        testFilter.initializeFilter(filteredList, tile, handler, false);
        return testFilter;
    }

    @Override
    public IItemFilter getOutputItemFilter(ItemStack filterStack, BlockEntity tile, IItemHandler handler) {
        IItemFilter testFilter = getFilterTypeFromConfig(filterStack);

        List<IFilterKey> filteredList = new ArrayList<>();
        FilterInventory inv = getFilterInventory(filterStack);

        List<ItemStack> nestedList = getNestedFilters(filterStack);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            int amount = GhostItemHelper.getItemGhostAmount(stack);
            ItemStack ghostStack = GhostItemHelper.getSingleStackFromGhost(stack);
            if (amount == 0) {
                amount = Integer.MAX_VALUE;
            }

            // Create a composite key that combines all nested filter keys
            CompositeFilterKey compositeKey = new CompositeFilterKey(amount);

            // Add the basic filter key for this ghost item
            compositeKey.addFilterKey(new BasicFilterKey(ghostStack, amount));

            // Add filter keys from all nested filters
            for (ItemStack nestedStack : nestedList) {
                if (nestedStack.getItem() instanceof INestableItemFilterProvider nestedFilter) {
                    IFilterKey nestedKey = nestedFilter.getFilterKey(nestedStack, i, ghostStack, amount);
                    if (nestedKey != null) {
                        compositeKey.addFilterKey(nestedKey);
                    }
                }
            }

            filteredList.add(compositeKey);
        }

        testFilter.initializeFilter(filteredList, tile, handler, true);
        return testFilter;
    }

    protected IItemFilter getFilterTypeFromConfig(ItemStack filterStack) {
        int state = getBlacklistState(filterStack);
        if (state == 1) {
            return new BlacklistItemFilter();
        }
        return new BasicItemFilter();
    }

    public static NestedFilterInventory getNestedFilterInventory(ItemStack filterStack) {
        return filterStack.getOrDefault(BMDataComponents.NESTED_FILTERS, NestedFilterInventory.empty());
    }

    public static void setNestedFilterInventory(ItemStack filterStack, NestedFilterInventory inventory) {
        filterStack.set(BMDataComponents.NESTED_FILTERS, inventory);
    }

    public List<ItemStack> getNestedFilters(ItemStack mainFilterStack) {
        return getNestedFilterInventory(mainFilterStack).getNonEmptyFilters();
    }

    @Override
    public boolean canReceiveNestedFilter(ItemStack mainStack, ItemStack nestedStack) {
        if (nestedStack.isEmpty()) {
            return false;
        }
        if (!(nestedStack.getItem() instanceof INestableItemFilterProvider)) {
            return false;
        }

        NestedFilterInventory inv = getNestedFilterInventory(mainStack);

        // Check if there's an empty slot
        if (inv.getFirstEmptySlot() == -1) {
            return false;
        }

        // Don't allow duplicate filter types
        if (inv.containsFilterType(nestedStack)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean nestFilter(ItemStack mainStack, ItemStack nestedStack) {
        if (!canReceiveNestedFilter(mainStack, nestedStack)) {
            return false;
        }

        NestedFilterInventory inv = getNestedFilterInventory(mainStack);
        int emptySlot = inv.getFirstEmptySlot();
        if (emptySlot >= 0) {
            inv = inv.setFilter(emptySlot, nestedStack.copy());
            setNestedFilterInventory(mainStack, inv);
            return true;
        }

        return false;
    }
}
