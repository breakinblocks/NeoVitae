package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Datamap record for routing node block statistics.
 *
 * <p>This allows customization of routing node performance via datapacks,
 * enabling modpack developers to balance node performance and addon mods
 * to create custom node types with different capabilities.</p>
 *
 * <h2>Properties for All Nodes</h2>
 * <ul>
 *   <li>{@code max_connections} - Maximum nodes this can connect to (-1 = unlimited)</li>
 *   <li>{@code connection_range} - Maximum connection distance in blocks (-1 = unlimited)</li>
 *   <li>{@code priority_bonus} - Bonus added to all priorities on this node</li>
 * </ul>
 *
 * <h2>Properties for Master Nodes</h2>
 * <ul>
 *   <li>{@code base_tick_rate} - Ticks between operations (lower = faster, min 1)</li>
 *   <li>{@code base_item_transfer} - Items transferred per operation</li>
 *   <li>{@code base_fluid_transfer} - Fluid mB transferred per operation</li>
 *   <li>{@code item_transfer_per_upgrade} - Additional items per stack upgrade</li>
 *   <li>{@code fluid_transfer_per_upgrade} - Additional mB per stack upgrade</li>
 *   <li>{@code max_speed_upgrades} - Maximum speed upgrades allowed</li>
 *   <li>{@code max_stack_upgrades} - Maximum stack upgrades allowed</li>
 * </ul>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // In data/<namespace>/data_maps/block/routing_node_stats.json
 * {
 *   "values": {
 *     "neovitae:master_routing_node": {
 *       "base_tick_rate": 20,
 *       "base_item_transfer": 16,
 *       "base_fluid_transfer": 1000,
 *       "item_transfer_per_upgrade": 16,
 *       "fluid_transfer_per_upgrade": 1000,
 *       "max_speed_upgrades": 19,
 *       "max_stack_upgrades": 64
 *     },
 *     "mymod:fast_master_node": {
 *       "base_tick_rate": 5,
 *       "base_item_transfer": 64,
 *       "base_fluid_transfer": 4000
 *     },
 *     "mymod:extended_range_node": {
 *       "connection_range": 32,
 *       "max_connections": 16
 *     }
 *   }
 * }
 * }</pre>
 *
 * @param maxConnections        Maximum number of connections (-1 = unlimited)
 * @param connectionRange       Maximum connection distance in blocks (-1 = unlimited)
 * @param priorityBonus         Bonus to all priorities on this node
 * @param baseTickRate          Ticks between operations (master nodes only)
 * @param baseItemTransfer      Items per operation (master nodes only)
 * @param baseFluidTransfer     Fluid mB per operation (master nodes only)
 * @param itemTransferPerUpgrade Additional items per stack upgrade (master nodes only)
 * @param fluidTransferPerUpgrade Additional mB per stack upgrade (master nodes only)
 * @param maxSpeedUpgrades      Maximum speed upgrades allowed (master nodes only)
 * @param maxStackUpgrades      Maximum stack upgrades allowed (master nodes only)
 */
public record RoutingNodeStats(
        Optional<Integer> maxConnections,
        Optional<Integer> connectionRange,
        Optional<Integer> priorityBonus,
        Optional<Integer> baseTickRate,
        Optional<Integer> baseItemTransfer,
        Optional<Integer> baseFluidTransfer,
        Optional<Integer> baseEnergyTransfer,
        Optional<Integer> itemTransferPerUpgrade,
        Optional<Integer> fluidTransferPerUpgrade,
        Optional<Integer> energyTransferPerUpgrade,
        Optional<Integer> maxSpeedUpgrades,
        Optional<Integer> maxStackUpgrades
) {
    public static final RoutingNodeStats DEFAULT_NODE = new RoutingNodeStats(
            Optional.empty(),
            Optional.empty(),
            Optional.of(0),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final RoutingNodeStats DEFAULT_MASTER = new RoutingNodeStats(
            Optional.empty(),
            Optional.empty(),
            Optional.of(0),
            Optional.of(20),
            Optional.of(16),
            Optional.of(1000),
            Optional.of(10000),
            Optional.of(16),
            Optional.of(1000),
            Optional.of(10000),
            Optional.of(19),
            Optional.of(64)
    );

    public static final Codec<RoutingNodeStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("max_connections").forGetter(RoutingNodeStats::maxConnections),
            Codec.INT.optionalFieldOf("connection_range").forGetter(RoutingNodeStats::connectionRange),
            Codec.INT.optionalFieldOf("priority_bonus").forGetter(RoutingNodeStats::priorityBonus),
            Codec.INT.optionalFieldOf("base_tick_rate").forGetter(RoutingNodeStats::baseTickRate),
            Codec.INT.optionalFieldOf("base_item_transfer").forGetter(RoutingNodeStats::baseItemTransfer),
            Codec.INT.optionalFieldOf("base_fluid_transfer").forGetter(RoutingNodeStats::baseFluidTransfer),
            Codec.INT.optionalFieldOf("base_energy_transfer").forGetter(RoutingNodeStats::baseEnergyTransfer),
            Codec.INT.optionalFieldOf("item_transfer_per_upgrade").forGetter(RoutingNodeStats::itemTransferPerUpgrade),
            Codec.INT.optionalFieldOf("fluid_transfer_per_upgrade").forGetter(RoutingNodeStats::fluidTransferPerUpgrade),
            Codec.INT.optionalFieldOf("energy_transfer_per_upgrade").forGetter(RoutingNodeStats::energyTransferPerUpgrade),
            Codec.INT.optionalFieldOf("max_speed_upgrades").forGetter(RoutingNodeStats::maxSpeedUpgrades),
            Codec.INT.optionalFieldOf("max_stack_upgrades").forGetter(RoutingNodeStats::maxStackUpgrades)
    ).apply(instance, RoutingNodeStats::new));

    public int getMaxConnections() {
        return maxConnections.orElse(-1);
    }

    /**
     * Gets the maximum connection range in blocks.
     * @return max range, or -1 for unlimited
     */
    public int getConnectionRange() {
        return connectionRange.orElse(-1);
    }

    /**
     * Gets the priority bonus for this node.
     * @return priority bonus (default 0)
     */
    public int getPriorityBonus() {
        return priorityBonus.orElse(0);
    }

    public int getBaseTickRate() {
        return baseTickRate.orElse(20);
    }

    /**
     * Gets the base item transfer amount for master nodes.
     * @return items per operation (default 16)
     */
    public int getBaseItemTransfer() {
        return baseItemTransfer.orElse(16);
    }

    /**
     * Gets the base fluid transfer amount for master nodes.
     * @return mB per operation (default 1000)
     */
    public int getBaseFluidTransfer() {
        return baseFluidTransfer.orElse(1000);
    }

    public int getBaseEnergyTransfer() {
        return baseEnergyTransfer.orElse(10000);
    }

    /**
     * Gets the additional items per stack upgrade for master nodes.
     * @return items per upgrade (default 16)
     */
    public int getItemTransferPerUpgrade() {
        return itemTransferPerUpgrade.orElse(16);
    }

    /**
     * Gets the additional fluid per stack upgrade for master nodes.
     * @return mB per upgrade (default 1000)
     */
    public int getFluidTransferPerUpgrade() {
        return fluidTransferPerUpgrade.orElse(1000);
    }

    public int getEnergyTransferPerUpgrade() {
        return energyTransferPerUpgrade.orElse(10000);
    }

    /**
     * Gets the maximum number of speed upgrades allowed for master nodes.
     * @return max speed upgrades (default 19)
     */
    public int getMaxSpeedUpgrades() {
        return maxSpeedUpgrades.orElse(19);
    }

    /**
     * Gets the maximum number of stack upgrades allowed for master nodes.
     * @return max stack upgrades (default 64)
     */
    public int getMaxStackUpgrades() {
        return maxStackUpgrades.orElse(64);
    }

    /**
     * Checks if this node has master node properties defined.
     */
    public boolean hasMasterProperties() {
        return baseTickRate.isPresent() || baseItemTransfer.isPresent() || baseFluidTransfer.isPresent();
    }
}
