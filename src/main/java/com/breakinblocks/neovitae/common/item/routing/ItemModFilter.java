package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.util.GhostItemHelper;

import java.util.List;

/**
 * Filter that matches items by their mod namespace.
 * Place any item from a mod in the filter to match all items from that mod.
 */
public class ItemModFilter extends ItemRouterFilter implements INestableItemFilterProvider {

    public ItemModFilter() {
        super();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.neovitae.filter.mod");
    }

    @Override
    public void appendHoverText(ItemStack filterStack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.modfilter.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

        boolean sneaking = Screen.hasShiftDown();
        if (!sneaking) {
            tooltip.add(Component.translatable("tooltip.neovitae.extraInfo").withStyle(ChatFormatting.BLUE));
        } else {
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

                String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
                Component modText = Component.translatable("tooltip.neovitae.filter.from_mod", namespace);

                if (isWhitelist) {
                    int amount = GhostItemHelper.getItemGhostAmount(stack);
                    if (amount > 0) {
                        tooltip.add(Component.translatable("tooltip.neovitae.filter.count", amount, modText));
                    } else {
                        tooltip.add(Component.translatable("tooltip.neovitae.filter.all", modText));
                    }
                } else {
                    tooltip.add(modText);
                }
            }
        }
    }

    @Override
    public IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount) {
        String namespace = BuiltInRegistries.ITEM.getKey(ghostStack.getItem()).getNamespace();
        return new ModFilterKey(namespace, amount);
    }
}
