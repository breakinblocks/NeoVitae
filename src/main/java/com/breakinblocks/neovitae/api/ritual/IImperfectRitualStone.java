// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Interface for imperfect ritual stone block entities.
 * Provides access to the world and position for ritual implementations.
 */
public interface IImperfectRitualStone {

    /**
     * Gets the world the ritual stone is in.
     *
     * @return The level
     */
    Level getRitualWorld();

    /**
     * Gets the position of the ritual stone.
     *
     * @return The block position
     */
    BlockPos getRitualPos();
}
