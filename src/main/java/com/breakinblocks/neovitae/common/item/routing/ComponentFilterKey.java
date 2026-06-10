// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.routing;

import com.breakinblocks.neovitae.api.routing.IFilterKey;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Set;

/**
 * Filter key that matches by item type plus a selected set of data components.
 * Only the chosen component types must match; any other components on the test
 * stack are ignored, so a single multi-variant item (e.g. a conduit whose type
 * is stored in a component) can be filtered down to one variant.
 */
public class ComponentFilterKey implements IFilterKey {

    private final ItemStack keyStack;
    private final Set<DataComponentType<?>> matchedComponents;
    private int count;

    public ComponentFilterKey(ItemStack keyStack, Set<DataComponentType<?>> matchedComponents, int count) {
        this.keyStack = keyStack;
        this.matchedComponents = matchedComponents;
        this.count = count;
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        if (keyStack.isEmpty() || testStack.isEmpty()) return false;
        if (keyStack.getItem() != testStack.getItem()) return false;
        for (DataComponentType<?> type : matchedComponents) {
            if (!Objects.equals(keyStack.get(type), testStack.get(type))) {
                return false;
            }
        }
        return true;
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
        return count == 0 || keyStack.isEmpty();
    }

    public ItemStack getKeyStack() {
        return keyStack;
    }

    public Set<DataComponentType<?>> getMatchedComponents() {
        return matchedComponents;
    }
}
