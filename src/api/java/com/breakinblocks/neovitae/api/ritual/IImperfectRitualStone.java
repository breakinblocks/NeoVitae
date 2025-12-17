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
