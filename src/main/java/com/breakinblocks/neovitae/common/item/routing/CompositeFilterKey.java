// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter key that combines multiple filter keys - an item must match ALL contained keys.
 */
public class CompositeFilterKey implements IFilterKey {
    private final List<IFilterKey> keyList = new ArrayList<>();
    private int count;

    public CompositeFilterKey(int count) {
        this.count = count;
    }

    public void addFilterKey(IFilterKey key) {
        if (!(key instanceof CompositeFilterKey)) {
            keyList.add(key);
        }
    }

    @Override
    public boolean doesStackMatch(ItemStack testStack) {
        if (testStack.isEmpty()) {
            return false;
        }

        for (IFilterKey key : keyList) {
            if (!key.doesStackMatch(testStack)) {
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
        return count == 0;
    }

    public List<IFilterKey> getKeyList() {
        return keyList;
    }
}
