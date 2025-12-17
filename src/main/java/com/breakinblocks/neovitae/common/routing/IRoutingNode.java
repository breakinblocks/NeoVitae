package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Base interface for all routing nodes in the item routing network.
 */
public interface IRoutingNode {

    /**
     * Connects this node to the master routing node, propagating through the network.
     */
    void connectMasterToRemainingNode(Level level, List<BlockPos> alreadyChecked, IMasterRoutingNode master);

    /**
     * Gets the current block position of this node.
     */
    BlockPos getCurrentBlockPos();

    /**
     * Gets the list of connected node positions.
     */
    List<BlockPos> getConnected();

    /**
     * Gets the position of the master routing node this node is connected to.
     */
    BlockPos getMasterPos();

    /**
     * Checks if the connection to the given position is enabled.
     */
    boolean isConnectionEnabled(BlockPos testPos);

    /**
     * Checks if this node is connected to the given master.
     */
    boolean isMaster(IMasterRoutingNode master);

    /**
     * Adds a connection to another node.
     */
    void addConnection(BlockPos pos);

    /**
     * Removes a connection to another node.
     */
    void removeConnection(BlockPos pos);

    /**
     * Removes all connections from this node.
     */
    void removeAllConnections();

    /**
     * Checks and purges the connection to the master node.
     * @param ignorePos Position to ignore during the check
     * @return List of checked node locations
     */
    List<BlockPos> checkAndPurgeConnectionToMaster(BlockPos ignorePos);

    /**
     * Rechecks the connection to the master node.
     * @param alreadyChecked Positions already checked
     * @param nodeList List of routing nodes found
     * @return Triple of (found master, checked positions, found nodes)
     */
    Triple<Boolean, List<BlockPos>, List<IRoutingNode>> recheckConnectionToMaster(
            List<BlockPos> alreadyChecked, List<IRoutingNode> nodeList);
}
