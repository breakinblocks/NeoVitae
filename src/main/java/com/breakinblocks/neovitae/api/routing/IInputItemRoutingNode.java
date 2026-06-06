// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that pull items from connected inventories.
 */
public interface IInputItemRoutingNode extends IItemRoutingNode {

    /**
     * Checks if this node acts as an input on the given side.
     */
    boolean isInput(Direction side);

    /**
     * Gets the input filter for the given side.
     */
    IItemFilter getInputFilterForSide(Direction side);
}
