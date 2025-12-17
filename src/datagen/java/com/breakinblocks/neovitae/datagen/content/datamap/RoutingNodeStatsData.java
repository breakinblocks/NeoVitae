package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;

import java.util.Optional;
import java.util.function.Function;

/**
 * Generates default routing node stats for Blood Magic's built-in routing nodes.
 *
 * <p>These values can be overridden via datapacks at:
 * {@code data/<namespace>/data_maps/block/routing_node_stats.json}</p>
 *
 * <p>Addon mods can define their own custom routing nodes with different stats,
 * enabling faster transfer rates, larger capacities, or other modifications.</p>
 *
 * <h2>Example Custom Master Node</h2>
 * <pre>{@code
 * // In data/mymod/data_maps/block/routing_node_stats.json
 * {
 *   "values": {
 *     "mymod:advanced_master_node": {
 *       "base_tick_rate": 5,        // 4x faster than default
 *       "base_item_transfer": 64,   // 4x more items
 *       "base_fluid_transfer": 4000 // 4x more fluid
 *     }
 *   }
 * }
 * }</pre>
 */
public class RoutingNodeStatsData {

    // Default values for master routing node
    private static final int BASE_TICK_RATE = 20;
    private static final int BASE_ITEM_TRANSFER = 16;
    private static final int BASE_FLUID_TRANSFER = 1000;
    private static final int ITEM_PER_UPGRADE = 16;
    private static final int FLUID_PER_UPGRADE = 1000;
    private static final int MAX_SPEED_UPGRADES = 19;
    private static final int MAX_STACK_UPGRADES = 64;

    public static void bootstrap(Function<DataMapType<Block, RoutingNodeStats>, DataMapProvider.Builder<RoutingNodeStats, Block>> setup) {
        var builder = setup.apply(BMDataMaps.ROUTING_NODE_STATS);

        // Master routing node - the network controller
        builder.add(
                BMBlocks.MASTER_ROUTING_NODE.block(),
                new RoutingNodeStats(
                        Optional.empty(),                    // unlimited connections
                        Optional.empty(),                    // unlimited range
                        Optional.of(0),                      // no priority bonus
                        Optional.of(BASE_TICK_RATE),         // 20 ticks base
                        Optional.of(BASE_ITEM_TRANSFER),     // 16 items base
                        Optional.of(BASE_FLUID_TRANSFER),    // 1000 mB base
                        Optional.of(ITEM_PER_UPGRADE),       // +16 per upgrade
                        Optional.of(FLUID_PER_UPGRADE),      // +1000 per upgrade
                        Optional.of(MAX_SPEED_UPGRADES),     // max 19 speed upgrades
                        Optional.of(MAX_STACK_UPGRADES)      // max 64 stack upgrades
                ),
                false
        );

        // Basic routing node - relay node (no special stats needed, uses defaults)
        // We still register it so it shows in the datamap for reference
        builder.add(
                BMBlocks.ROUTING_NODE.block(),
                RoutingNodeStats.DEFAULT_NODE,
                false
        );

        // Input routing node - pulls items from inventories
        builder.add(
                BMBlocks.INPUT_ROUTING_NODE.block(),
                RoutingNodeStats.DEFAULT_NODE,
                false
        );

        // Output routing node - pushes items to inventories
        builder.add(
                BMBlocks.OUTPUT_ROUTING_NODE.block(),
                RoutingNodeStats.DEFAULT_NODE,
                false
        );
    }
}
