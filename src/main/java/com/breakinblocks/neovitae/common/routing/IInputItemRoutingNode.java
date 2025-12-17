package com.breakinblocks.neovitae.common.routing;

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
