package com.breakinblocks.neovitae.common.routing;

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
}
