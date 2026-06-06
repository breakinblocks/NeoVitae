// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that push items to connected inventories.
 */
public interface IOutputItemRoutingNode extends IItemRoutingNode {

    /**
     * Checks if this node acts as an output on the given side.
     */
    boolean isOutput(Direction side);

    /**
     * Gets the output filter for the given side.
     */
    IItemFilter getOutputFilterForSide(Direction side);
}
