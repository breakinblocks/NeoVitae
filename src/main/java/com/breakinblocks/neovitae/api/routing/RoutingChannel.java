package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Defines a capability-based routing channel for the routing node network.
 * <p>
 * Each channel handles one type of resource transfer (items, fluids, energy, etc.).
 * The master routing node iterates over all registered channels each tick and processes
 * transfers for each one independently.
 * <p>
 * To add a new resource type to the routing system:
 * <ol>
 *   <li>Create a filter interface extending {@link IRoutingFilter}</li>
 *   <li>Implement the filter with transfer logic</li>
 *   <li>Implement this interface to define node detection and filter creation</li>
 *   <li>Register via {@link RoutingChannelRegistry#register} during mod construction</li>
 * </ol>
 *
 * @param <F> The filter type used by this channel
 */
public interface RoutingChannel<F extends IRoutingFilter> {

    /**
     * Unique identifier for this channel, used as NBT key for node list persistence.
     */
    String id();

    /**
     * Returns true if the given block entity can act as an input node for this channel.
     */
    boolean isInputNode(BlockEntity be);

    /**
     * Returns true if the given block entity can act as an output node for this channel.
     */
    boolean isOutputNode(BlockEntity be);

    /**
     * Returns true if this channel's capability is available on the given side of the node.
     */
    boolean isConnectedOnSide(BlockEntity be, Direction side);

    boolean isInputSide(BlockEntity be, Direction side);

    boolean isOutputSide(BlockEntity be, Direction side);

    int getPriority(BlockEntity be, Direction side);

    @Nullable
    F getInputFilter(BlockEntity be, Direction side);

    @Nullable
    F getOutputFilter(BlockEntity be, Direction side);

    /**
     * Returns the maximum transfer amount per tick for this channel on the given master node.
     */
    int getMaxTransfer(BlockEntity masterNode);

    /**
     * Executes a transfer from input filter to output filter, up to maxTransfer.
     *
     * @return the amount actually transferred
     */
    int transfer(F inputFilter, F outputFilter, int maxTransfer);
}
