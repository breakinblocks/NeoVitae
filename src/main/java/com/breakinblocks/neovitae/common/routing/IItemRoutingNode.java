package com.breakinblocks.neovitae.common.routing;

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
