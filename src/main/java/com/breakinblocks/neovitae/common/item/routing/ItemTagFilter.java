package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.FilterInventory;
import com.breakinblocks.neovitae.util.GhostItemHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter that matches items by their tags.
 * Supports "any tag" mode (matches any tag on the item) or specific tag selection.
 */
public class ItemTagFilter extends ItemRouterFilter implements INestableItemFilterProvider {

    @Override
    public boolean hasTagButton() {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.neovitae.filter.tag");
    }

    @Override
    public void appendHoverText(ItemStack filterStack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.tagfilter.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

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

            ResourceLocation tag = getItemTagResource(filterStack, i);
            Component display;
            if (tag != null) {
                display = Component.literal(tag.toString());
            } else {
                display = Component.translatable("tooltip.neovitae.filter.anytag", stack.getHoverName());
            }

            if (isWhitelist) {
                int amount = GhostItemHelper.getItemGhostAmount(stack);
                if (amount > 0) {
                    tooltip.add(Component.translatable("tooltip.neovitae.filter.count", amount, display));
                } else {
                    tooltip.add(Component.translatable("tooltip.neovitae.filter.all", display));
                }
            } else {
                tooltip.add(display);
            }
        }
    }

    @Override
    public IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount) {
        int index = getItemTagIndex(filterStack, slot);
        if (index == 0) {
            List<TagKey<Item>> tagList = getAllItemTags(ghostStack);
            if (tagList != null && !tagList.isEmpty()) {
                return new CollectionTagFilterKey(tagList, amount);
            }
        } else {
            TagKey<Item> tag = getItemTag(filterStack, slot);
            if (tag != null) {
                return new TagFilterKey(tag, amount);
            }
        }
        return new BasicFilterKey(ghostStack, amount);
    }

    public static int getItemTagIndex(ItemStack filterStack, int slot) {
        FilterInventory inv = getFilterInventory(filterStack);
        return inv.getTagIndex(slot);
    }

    public static void setItemTagIndex(ItemStack filterStack, int slot, int index) {
        FilterInventory inv = getFilterInventory(filterStack);
        inv = inv.setTagIndex(slot, index);
        setFilterInventory(filterStack, inv);
    }

    public void cycleToNextTag(ItemStack filterStack, int slot) {
        FilterInventory inv = getFilterInventory(filterStack);
        ItemStack ghostStack = inv.getItem(slot);
        if (ghostStack.isEmpty()) {
            return;
        }

        int index = getItemTagIndex(filterStack, slot);
        List<TagKey<Item>> tags = getAllItemTags(ghostStack);
        index++;

        if (index > tags.size()) {
            index = 0;
        }

        setItemTagIndex(filterStack, slot, index);

        // Store the selected tag name in the ghost stack
        if (index > 0 && index <= tags.size()) {
            TagKey<Item> selectedTag = tags.get(index - 1);
            ghostStack.set(BMDataComponents.FILTER_TAG, selectedTag.location().toString());
        } else {
            ghostStack.remove(BMDataComponents.FILTER_TAG);
        }
        inv = inv.setItem(slot, ghostStack);
        setFilterInventory(filterStack, inv);
    }

    public TagKey<Item> getItemTag(ItemStack filterStack, int slot) {
        int index = getItemTagIndex(filterStack, slot);
        if (index <= 0) {
            return null;
        }

        FilterInventory inv = getFilterInventory(filterStack);
        ItemStack ghostStack = inv.getItem(slot);
        if (ghostStack.isEmpty()) {
            return null;
        }

        String tagName = ghostStack.getOrDefault(BMDataComponents.FILTER_TAG, "");
        if (!tagName.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(tagName);
            if (rl != null) {
                return TagKey.create(Registries.ITEM, rl);
            }
        }

        // Fallback to index-based lookup
        List<TagKey<Item>> tags = getAllItemTags(ghostStack);
        if (tags.size() >= index) {
            return tags.get(index - 1);
        }

        return null;
    }

    public ResourceLocation getItemTagResource(ItemStack filterStack, int slot) {
        TagKey<Item> tag = getItemTag(filterStack, slot);
        if (tag == null) {
            return null;
        }
        return tag.location();
    }

    public static List<TagKey<Item>> getAllItemTags(ItemStack ghostStack) {
        if (ghostStack.isEmpty()) {
            return new ArrayList<>();
        }

        List<TagKey<Item>> tagList = new ArrayList<>();
        ghostStack.getTags().forEach(tagList::add);
        return tagList;
    }
}
