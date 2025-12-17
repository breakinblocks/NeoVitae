package com.breakinblocks.neovitae.common.routing;

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
