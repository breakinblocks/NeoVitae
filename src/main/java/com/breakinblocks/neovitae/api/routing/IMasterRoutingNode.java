package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Interface for the master routing node that coordinates the entire network.
 */
public interface IMasterRoutingNode extends IRoutingNode {

    /**
     * Checks if a path exists to the given node position.
     */
    boolean isConnected(List<BlockPos> path, BlockPos nodePos);

    /**
     * Adds a routing node to the master's node list.
     */
    void addNodeToList(IRoutingNode node);

    /**
     * Adds multiple connections for a node.
     */
    void addConnections(BlockPos pos, List<BlockPos> connectionList);

    /**
     * Adds a bidirectional connection between two positions.
     */
    void addConnection(BlockPos pos1, BlockPos pos2);

    /**
     * Removes a connection between two positions.
     */
    void removeConnection(BlockPos pos1, BlockPos pos2);

    /**
     * Scrubs a position from the network graph entirely: removes all edges
     * involving it and deletes its entry from the master's connection map.
     * Used when a conduit or node is broken so the graph doesn't leak stale
     * references. Call this from the node's {@code removeAllConnections}
     * instead of trying to remove a self-edge.
     */
    void removeNodeFromGraph(BlockPos pos);
}
