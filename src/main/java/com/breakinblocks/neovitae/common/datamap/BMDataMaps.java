package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

public class BMDataMaps {
    public static final DataMapType<Item, Double> TARTARIC_GEM_MAX_AMOUNTS = DataMapType.builder(
            NeoVitae.rl("tartaric_gem_max"),
            Registries.ITEM,
            Codec.DOUBLE
    ).synced(Codec.DOUBLE, true).build();

    public static final DataMapType<Item, BloodOrb> BLOOD_ORB_STATS = DataMapType.builder(
            NeoVitae.rl("blood_orb_stats"),
            Registries.ITEM,
            BloodOrb.CODEC
    ).synced(BloodOrb.CODEC, true).build();

    /**
     * Altar rune statistics - allows customization of rune block bonuses via datapacks.
     * Maps Block -> AltarRuneStats.
     *
     * <p>Each rune block can have any combination of bonuses (capacity, sacrifice, speed, etc.).
     * This allows modpack developers to tweak balance and addon mods to add custom runes
     * without code changes.</p>
     */
    public static final DataMapType<Block, AltarRuneStats> ALTAR_RUNE_STATS = DataMapType.builder(
            NeoVitae.rl("altar_rune_stats"),
            Registries.BLOCK,
            AltarRuneStats.CODEC
    ).synced(AltarRuneStats.CODEC, true).build();

    /**
     * Sigil statistics - allows customization of LP costs, ranges, and effect parameters.
     * Maps Item -> SigilStats.
     */
    public static final DataMapType<Item, SigilStats> SIGIL_STATS = DataMapType.builder(
            NeoVitae.rl("sigil_stats"),
            Registries.ITEM,
            SigilStats.CODEC
    ).synced(SigilStats.CODEC, true).build();

    /**
     * Ritual statistics - allows customization of activation/refresh costs, times, and ranges.
     * Maps Ritual -> RitualStats using the custom ritual registry.
     */
    public static final DataMapType<Ritual, RitualStats> RITUAL_STATS = DataMapType.builder(
            NeoVitae.rl("ritual_stats"),
            RitualRegistry.RITUAL_REGISTRY_KEY,
            RitualStats.CODEC
    ).synced(RitualStats.CODEC, true).build();

    /**
     * Imperfect ritual statistics - allows customization of activation cost, required block, and consumption.
     * Maps ImperfectRitual -> ImperfectRitualStats using the custom imperfect ritual registry.
     */
    public static final DataMapType<ImperfectRitual, ImperfectRitualStats> IMPERFECT_RITUAL_STATS = DataMapType.builder(
            NeoVitae.rl("imperfect_ritual_stats"),
            RitualRegistry.IMPERFECT_RITUAL_REGISTRY_KEY,
            ImperfectRitualStats.CODEC
    ).synced(ImperfectRitualStats.CODEC, true).build();

    /**
     * Entity sacrifice LP values - allows customization of LP generated when entities
     * are damaged by sacrifice rituals (e.g., Well of Suffering).
     * Maps EntityType -> EntitySacrificeValue.
     *
     * <p>Supports entity tags via the standard {@code #tag_name} syntax in datapacks.
     * Priority: specific entity > tag > default (25 LP per damage).</p>
     *
     * <h2>Example</h2>
     * <pre>{@code
     * {
     *   "values": {
     *     "minecraft:zombie": { "lp_per_damage": 30 },
     *     "#minecraft:undead": { "lp_per_damage": 25 },
     *     "#c:bosses": { "lp_per_damage": 1000, "max_lp_per_hit": 5000 }
     *   }
     * }
     * }</pre>
     */
    public static final DataMapType<EntityType<?>, EntitySacrificeValue> ENTITY_SACRIFICE_VALUE = DataMapType.builder(
            NeoVitae.rl("entity_sacrifice_value"),
            Registries.ENTITY_TYPE,
            EntitySacrificeValue.CODEC
    ).synced(EntitySacrificeValue.CODEC, true).build();

    /**
     * Block tranquility values - allows customization of tranquility contributions
     * for the Incense Altar system. Maps Block -> TranquilityValue.
     *
     * <p>Supports block tags via the standard {@code #tag_name} syntax in datapacks.
     * When a block matches multiple tags, specific block entries take priority over tags.</p>
     *
     * <h2>Tranquility Types</h2>
     * <ul>
     *   <li>{@code plant} - Flowers, grass, ferns</li>
     *   <li>{@code crop} - Wheat, carrots, potatoes</li>
     *   <li>{@code tree} - Logs and leaves</li>
     *   <li>{@code earthen} - Dirt, sand, gravel, clay</li>
     *   <li>{@code water} - Water source and flowing</li>
     *   <li>{@code fire} - Fire and soul fire</li>
     *   <li>{@code lava} - Lava source and flowing</li>
     * </ul>
     *
     * <h2>Example</h2>
     * <pre>{@code
     * {
     *   "values": {
     *     "minecraft:oak_log": { "type": "tree", "value": 1.5 },
     *     "#minecraft:logs": { "type": "tree", "value": 1.0 },
     *     "#minecraft:dirt": { "type": "earthen", "value": 0.5 }
     *   }
     * }
     * }</pre>
     */
    public static final DataMapType<Block, TranquilityValue> TRANQUILITY = DataMapType.builder(
            NeoVitae.rl("tranquility"),
            Registries.BLOCK,
            TranquilityValue.CODEC
    ).synced(TranquilityValue.CODEC, true).build();

    /**
     * Routing node statistics - allows customization of routing node performance via datapacks.
     * Maps Block -> RoutingNodeStats.
     *
     * <p>This enables addon mods to create custom routing node blocks with different
     * transfer speeds, capacities, and connection limits. Modpack developers can also
     * tweak the balance of existing nodes.</p>
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
     *   <li>{@code base_tick_rate} - Ticks between operations (lower = faster)</li>
     *   <li>{@code base_item_transfer} - Items transferred per operation</li>
     *   <li>{@code base_fluid_transfer} - Fluid mB transferred per operation</li>
     *   <li>{@code item_transfer_per_upgrade} - Additional items per stack upgrade</li>
     *   <li>{@code fluid_transfer_per_upgrade} - Additional mB per stack upgrade</li>
     * </ul>
     *
     * <h2>Example</h2>
     * <pre>{@code
     * {
     *   "values": {
     *     "neovitae:master_routing_node": {
     *       "base_tick_rate": 20,
     *       "base_item_transfer": 16
     *     },
     *     "mymod:fast_master_node": {
     *       "base_tick_rate": 5,
     *       "base_item_transfer": 64
     *     }
     *   }
     * }
     * }</pre>
     */
    public static final DataMapType<Block, RoutingNodeStats> ROUTING_NODE_STATS = DataMapType.builder(
            NeoVitae.rl("routing_node_stats"),
            Registries.BLOCK,
            RoutingNodeStats.CODEC
    ).synced(RoutingNodeStats.CODEC, true).build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(TARTARIC_GEM_MAX_AMOUNTS);
        event.register(BLOOD_ORB_STATS);
        event.register(ALTAR_RUNE_STATS);
        event.register(SIGIL_STATS);
        event.register(RITUAL_STATS);
        event.register(IMPERFECT_RITUAL_STATS);
        event.register(ENTITY_SACRIFICE_VALUE);
        event.register(TRANQUILITY);
        event.register(ROUTING_NODE_STATS);
    }
}
