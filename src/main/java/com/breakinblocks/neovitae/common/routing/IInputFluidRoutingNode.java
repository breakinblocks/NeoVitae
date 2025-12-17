package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.Direction;

/**
 * Interface for routing nodes that pull fluids from connected tanks.
 */
public interface IInputFluidRoutingNode extends IFluidRoutingNode {

    /**
     * Checks if this node acts as a fluid input on the given side.
     */
    boolean isFluidInput(Direction side);

    /**
     * Gets the input fluid filter for the given side.
     */
    IFluidFilter getInputFluidFilterForSide(Direction side);
}
