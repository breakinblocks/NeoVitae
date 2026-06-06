// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Filter key that matches items against any tag in a collection.
 * Used when "any tag" mode is selected for tag filtering.
 */
public class CollectionTagFilterKey implements IFilterKey {
    private List<TagKey<Item>> itemTags;
    private int count;

    public CollectionTagFilterKey(List<TagKey<Item>> tagList, int count) {
        this.itemTags = tagList;
        this.count = count;
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        for (TagKey<Item> tag : itemTags) {
            if (testStack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void shrink(int changeAmount) {
        this.count -= changeAmount;
    }

    @Override
    public void grow(int changeAmount) {
        this.count += changeAmount;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }
}
