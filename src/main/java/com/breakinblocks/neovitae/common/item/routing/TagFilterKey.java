// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Filter key that matches items by Minecraft tags.
 */
public class TagFilterKey implements IFilterKey {

    private final TagKey<Item> itemTag;
    private int count;

    public TagFilterKey(TagKey<Item> tag, int count) {
        this.itemTag = tag;
        this.count = count;
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        return !testStack.isEmpty() && testStack.is(itemTag);
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

    public TagKey<Item> getItemTag() {
        return itemTag;
    }
}
