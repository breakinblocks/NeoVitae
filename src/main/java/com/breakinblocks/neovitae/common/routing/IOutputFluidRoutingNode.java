package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that push fluids to connected tanks.
 */
public interface IOutputFluidRoutingNode extends IFluidRoutingNode {

    /**
     * Checks if this node acts as a fluid output on the given side.
     */
    boolean isFluidOutput(Direction side);

    /**
     * Gets the output fluid filter for the given side.
     */
    IFluidFilter getOutputFluidFilterForSide(Direction side);
}
