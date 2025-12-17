package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that handle fluid transfers.
 */
public interface IFluidRoutingNode extends IRoutingNode {

    /**
     * Checks if a fluid tank is connected on the given side.
     */
    boolean isTankConnectedToSide(Direction side);

    /**
     * Gets the priority for the given side (0-9, higher = processed first).
     */
    int getFluidPriority(Direction side);
}
