// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that handle item transfers.
 */
public interface IItemRoutingNode extends IRoutingNode {

    /**
     * Checks if an inventory is connected on the given side.
     */
    boolean isInventoryConnectedToSide(Direction side);

    /**
     * Gets the priority for the given side (0-9, higher = processed first).
     */
    int getPriority(Direction side);
}
