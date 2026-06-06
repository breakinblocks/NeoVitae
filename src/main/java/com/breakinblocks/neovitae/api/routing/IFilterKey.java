// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.routing;

import net.minecraft.world.item.ItemStack;

/**
 * Defines matching criteria for a single entry in an item routing filter.
 * <p>
 * Each filter key represents one "slot" in a filter configuration, specifying
 * which items match and how many should be transferred.
 * <p>
 * Built-in implementations include:
 * <ul>
 *   <li>Basic matching by exact item type</li>
 *   <li>Tag-based matching (any item with a specific tag)</li>
 *   <li>Mod-based matching (any item from a specific mod)</li>
 *   <li>Composite matching (item must match ALL contained keys)</li>
 * </ul>
 */
public interface IFilterKey {

    boolean doesStackMatch(ItemStack testStack);

    int getCount();

    void setCount(int count);

    void grow(int changeAmount);

    boolean isEmpty();

    void shrink(int changeAmount);
}
