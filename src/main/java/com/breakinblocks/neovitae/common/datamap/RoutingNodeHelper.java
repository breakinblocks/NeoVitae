package com.breakinblocks.neovitae.common.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class for looking up routing node statistics from the datamap.
 *
 * <p>This provides a centralized way to query routing node stats,
 * with fallback to default values if not defined in the datamap.</p>
 */
public class RoutingNodeHelper {

    /**
     * Gets the routing node stats for a block, or null if not defined.
     *
     * @param block The block to look up
     * @return The stats or null if not in the datamap
     */
    @Nullable
    public static RoutingNodeStats getStats(Block block) {
        return BuiltInRegistries.BLOCK.wrapAsHolder(block).getData(BMDataMaps.ROUTING_NODE_STATS);
    }

    /**
     * Gets the routing node stats for a block state, or null if not defined.
     *
     * @param state The block state to look up
     * @return The stats or null if not in the datamap
     */
    @Nullable
    public static RoutingNodeStats getStats(BlockState state) {
        return getStats(state.getBlock());
    }

    /**
     * Gets the routing node stats for a block, with a default fallback.
     *
     * @param block        The block to look up
     * @param defaultStats The default stats to use if not defined
     * @return The stats from the datamap or the default
     */
    public static RoutingNodeStats getStatsOrDefault(Block block, RoutingNodeStats defaultStats) {
        RoutingNodeStats stats = getStats(block);
        return stats != null ? stats : defaultStats;
    }

    /**
     * Gets the routing node stats for a block, using DEFAULT_NODE as fallback.
     *
     * @param block The block to look up
     * @return The stats from the datamap or DEFAULT_NODE
     */
    public static RoutingNodeStats getNodeStats(Block block) {
        return getStatsOrDefault(block, RoutingNodeStats.DEFAULT_NODE);
    }

    /**
     * Gets the routing node stats for a block, using DEFAULT_MASTER as fallback.
     *
     * @param block The block to look up
     * @return The stats from the datamap or DEFAULT_MASTER
     */
    public static RoutingNodeStats getMasterStats(Block block) {
        return getStatsOrDefault(block, RoutingNodeStats.DEFAULT_MASTER);
    }

    // === Convenience methods for common lookups ===

    /**
     * Gets the maximum number of connections for a node block.
     * @return max connections, or -1 for unlimited
     */
    public static int getMaxConnections(Block block) {
        return getNodeStats(block).getMaxConnections();
    }

    /**
     * Gets the connection range for a node block.
     * @return max range in blocks, or -1 for unlimited
     */
    public static int getConnectionRange(Block block) {
        return getNodeStats(block).getConnectionRange();
    }

    /**
     * Gets the priority bonus for a node block.
     * @return priority bonus (default 0)
     */
    public static int getPriorityBonus(Block block) {
        return getNodeStats(block).getPriorityBonus();
    }

    /**
     * Gets the base tick rate for a master node block.
     * @return tick rate (default 20)
     */
    public static int getBaseTickRate(Block block) {
        return getMasterStats(block).getBaseTickRate();
    }

    /**
     * Gets the base item transfer for a master node block.
     * @return items per operation (default 16)
     */
    public static int getBaseItemTransfer(Block block) {
        return getMasterStats(block).getBaseItemTransfer();
    }

    /**
     * Gets the base fluid transfer for a master node block.
     * @return mB per operation (default 1000)
     */
    public static int getBaseFluidTransfer(Block block) {
        return getMasterStats(block).getBaseFluidTransfer();
    }

    /**
     * Gets the item transfer per upgrade for a master node block.
     * @return items per upgrade (default 16)
     */
    public static int getItemTransferPerUpgrade(Block block) {
        return getMasterStats(block).getItemTransferPerUpgrade();
    }

    /**
     * Gets the fluid transfer per upgrade for a master node block.
     * @return mB per upgrade (default 1000)
     */
    public static int getFluidTransferPerUpgrade(Block block) {
        return getMasterStats(block).getFluidTransferPerUpgrade();
    }

    /**
     * Gets the max speed upgrades for a master node block.
     * @return max speed upgrades (default 19)
     */
    public static int getMaxSpeedUpgrades(Block block) {
        return getMasterStats(block).getMaxSpeedUpgrades();
    }

    /**
     * Gets the max stack upgrades for a master node block.
     * @return max stack upgrades (default 64)
     */
    public static int getMaxStackUpgrades(Block block) {
        return getMasterStats(block).getMaxStackUpgrades();
    }

    /**
     * Calculates the effective tick rate for a master node with speed upgrades.
     *
     * @param block        The master node block
     * @param speedUpgrades Number of speed upgrades installed
     * @return The effective tick rate (minimum 1)
     */
    public static int getEffectiveTickRate(Block block, int speedUpgrades) {
        RoutingNodeStats stats = getMasterStats(block);
        int baseRate = stats.getBaseTickRate();
        int maxUpgrades = stats.getMaxSpeedUpgrades();
        int effectiveUpgrades = Math.min(speedUpgrades, maxUpgrades);
        return Math.max(1, baseRate - effectiveUpgrades);
    }

    /**
     * Calculates the effective item transfer for a master node with stack upgrades.
     *
     * @param block         The master node block
     * @param stackUpgrades Number of stack upgrades installed
     * @return The effective item transfer amount
     */
    public static int getEffectiveItemTransfer(Block block, int stackUpgrades) {
        RoutingNodeStats stats = getMasterStats(block);
        int baseTransfer = stats.getBaseItemTransfer();
        int perUpgrade = stats.getItemTransferPerUpgrade();
        int maxUpgrades = stats.getMaxStackUpgrades();
        int effectiveUpgrades = Math.min(stackUpgrades, maxUpgrades);
        return baseTransfer + (effectiveUpgrades * perUpgrade);
    }

    /**
     * Calculates the effective fluid transfer for a master node with stack upgrades.
     *
     * @param block         The master node block
     * @param stackUpgrades Number of stack upgrades installed
     * @return The effective fluid transfer amount in mB
     */
    public static int getEffectiveFluidTransfer(Block block, int stackUpgrades) {
        RoutingNodeStats stats = getMasterStats(block);
        int baseTransfer = stats.getBaseFluidTransfer();
        int perUpgrade = stats.getFluidTransferPerUpgrade();
        int maxUpgrades = stats.getMaxStackUpgrades();
        int effectiveUpgrades = Math.min(stackUpgrades, maxUpgrades);
        return baseTransfer + (effectiveUpgrades * perUpgrade);
    }
}
