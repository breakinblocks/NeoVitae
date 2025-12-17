package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.common.menu.FilterMenu;
import com.breakinblocks.neovitae.common.routing.BasicItemFilter;
import com.breakinblocks.neovitae.common.routing.BlacklistItemFilter;
import com.breakinblocks.neovitae.common.routing.IItemFilter;
import com.breakinblocks.neovitae.util.GhostItemHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for item routing filters.
 * Allows setting up ghost items to filter what items pass through routing nodes.
 */
public class ItemRouterFilter extends Item implements MenuProvider, IItemFilterProvider {
    public static final int INVENTORY_SIZE = 9;

    // Data slot indices for the menu ContainerData
    public static final int DATA_SLOT = 0;
    public static final int DATA_BWLIST = DATA_SLOT + 1;
    public static final int DATA_TAG = DATA_BWLIST + 1; // + slot (0-8)
    public static final int DATA_COUNT = DATA_TAG + 9;

    // Button IDs for clickMenuButton
    public static final int BUTTON_BWLIST = 0;
    public static final int BUTTON_TAG = 1;

    public ItemRouterFilter() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(this, buf -> {
                buf.writeBoolean(hasTagButton());
            });
        }

        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide), stack);
    }

    public boolean hasTagButton() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        ItemStack stack = player.getMainHandItem();
        return new FilterMenu(containerId, playerInv, stack, hasTagButton());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.neovitae.filter.exact");
    }

    protected IItemFilter getFilterTypeFromConfig(ItemStack filterStack) {
        int state = getBlacklistState(filterStack);
        if (state == 1) {
            return new BlacklistItemFilter();
        }
        return new BasicItemFilter();
    }

    public static int getBlacklistState(ItemStack filterStack) {
        return filterStack.getOrDefault(BMDataComponents.FILTER_BLACKLIST, 0);
    }

    public static void setBlacklistState(ItemStack filterStack, int state) {
        filterStack.set(BMDataComponents.FILTER_BLACKLIST, state);
    }

    public static FilterInventory getFilterInventory(ItemStack filterStack) {
        return filterStack.getOrDefault(BMDataComponents.FILTER_INVENTORY, FilterInventory.empty());
    }

    public static void setFilterInventory(ItemStack filterStack, FilterInventory inventory) {
        filterStack.set(BMDataComponents.FILTER_INVENTORY, inventory);
    }

    @Override
    public IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount) {
        return new BasicFilterKey(ghostStack, amount);
    }

    @Override
    public IItemFilter getInputItemFilter(ItemStack filterStack, BlockEntity tile, IItemHandler handler) {
        IItemFilter testFilter = getFilterTypeFromConfig(filterStack);
        List<IFilterKey> filteredList = buildFilterList(filterStack);
        testFilter.initializeFilter(filteredList, tile, handler, false);
        return testFilter;
    }

    @Override
    public IItemFilter getOutputItemFilter(ItemStack filterStack, BlockEntity tile, IItemHandler handler) {
        IItemFilter testFilter = getFilterTypeFromConfig(filterStack);
        List<IFilterKey> filteredList = buildFilterListForOutput(filterStack);
        testFilter.initializeFilter(filteredList, tile, handler, true);
        return testFilter;
    }

    protected List<IFilterKey> buildFilterList(ItemStack filterStack) {
        List<IFilterKey> filteredList = new ArrayList<>();
        FilterInventory inv = getFilterInventory(filterStack);

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            int amount = GhostItemHelper.getItemGhostAmount(stack);
            ItemStack ghostStack = GhostItemHelper.getSingleStackFromGhost(stack);

            IFilterKey key = getFilterKey(filterStack, i, ghostStack, amount);
            filteredList.add(key);
        }

        return filteredList;
    }

    protected List<IFilterKey> buildFilterListForOutput(ItemStack filterStack) {
        List<IFilterKey> filteredList = new ArrayList<>();
        FilterInventory inv = getFilterInventory(filterStack);

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

            IFilterKey key = getFilterKey(filterStack, i, ghostStack, amount);
            filteredList.add(key);
        }

        return filteredList;
    }

    @Override
    public void setGhostItemAmount(ItemStack filterStack, int ghostItemSlot, int amount) {
        FilterInventory inv = getFilterInventory(filterStack);
        ItemStack stack = inv.getItem(ghostItemSlot);
        if (!stack.isEmpty()) {
            GhostItemHelper.setItemGhostAmount(stack, amount);
            inv = inv.setItem(ghostItemSlot, stack);
            setFilterInventory(filterStack, inv);
        }
    }

    @Override
    public IItemFilter getUninitializedItemFilter(ItemStack filterStack) {
        IItemFilter testFilter = getFilterTypeFromConfig(filterStack);
        List<IFilterKey> filteredList = buildFilterList(filterStack);
        testFilter.initializeFilter(filteredList);
        return testFilter;
    }

    @Override
    public List<Component> getTextForHoverItem(ItemStack filterStack, String buttonKey, int ghostItemSlot) {
        List<Component> textList = new ArrayList<>();
        switch (buttonKey) {
            case "bwlist" -> {
                int state = getBlacklistState(filterStack);
                textList.add(Component.translatable(state == 0 ? "filter.neovitae.whitelist" : "filter.neovitae.blacklist"));
            }
        }
        return textList;
    }

    @Override
    public int getCurrentButtonState(ItemStack filterStack, String buttonKey, int ghostItemSlot) {
        return switch (buttonKey) {
            case "bwlist" -> getBlacklistState(filterStack);
            default -> -1;
        };
    }

    @Override
    public Pair<Integer, Integer> getTexturePositionForState(ItemStack filterStack, String buttonKey, int currentButtonState) {
        return switch (buttonKey) {
            case "bwlist" -> currentButtonState == 0 ? Pair.of(176, 0) : Pair.of(176, 20);
            default -> Pair.of(0, 0);
        };
    }

    @Override
    public int receiveButtonPress(ItemStack filterStack, String buttonKey, int ghostItemSlot, int currentButtonState) {
        return switch (buttonKey) {
            case "bwlist" -> {
                int newState = currentButtonState == 0 ? 1 : 0;
                setBlacklistState(filterStack, newState);
                yield newState;
            }
            default -> -1;
        };
    }

    @Override
    public boolean isButtonGlobal(ItemStack filterStack, String buttonKey) {
        return switch (buttonKey) {
            case "bwlist" -> true;
            default -> false;
        };
    }
}
