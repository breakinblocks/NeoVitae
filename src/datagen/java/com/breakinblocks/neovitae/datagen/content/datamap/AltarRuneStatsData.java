package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.datamap.AltarRuneStats;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;

import java.util.function.Function;

/**
 * Generates default altar rune stats for Blood Magic's built-in runes.
 *
 * <p>These values can be overridden via datapacks at:
 * {@code data/<namespace>/data_maps/block/altar_rune_stats.json}</p>
 *
 * <p>For dynamic runes whose bonuses change at runtime, addon mods should
 * listen to {@link com.breakinblocks.neovitae.api.event.AltarRuneEvent.CalculateStats}
 * and modify the stats based on their block entity state.</p>
 */
public class AltarRuneStatsData {

    // Values from AltarConstants - kept in sync
    private static final double CAPACITY_PER_RUNE = 0.2;
    private static final double AUGMENTED_CAPACITY_POWER = 1.075;
    private static final double CONSUMPTION_PER_RUNE = 0.2;
    private static final double SACRIFICE_PER_RUNE = 0.1;
    private static final double SELF_SACRIFICE_PER_RUNE = 0.1;
    private static final double DISLOCATION_POWER = 1.2;
    private static final double ORB_CAPACITY_PER_RUNE = 0.2;
    private static final int ACCELERATION_PER_RUNE = 1;
    private static final int CHARGE_AMOUNT_PER_RUNE = 10;
    private static final double EFFICIENCY_POWER = 0.85;

    public static void bootstrap(Function<DataMapType<Block, AltarRuneStats>, DataMapProvider.Builder<AltarRuneStats, Block>> setup) {
        var builder = setup.apply(BMDataMaps.ALTAR_RUNE_STATS);

        // Tier 1 Runes (base stats)
        builder
            // Speed runes - increase crafting speed
            .add(BMBlocks.RUNE_SPEED.block(), AltarRuneStats.speed(CONSUMPTION_PER_RUNE), false)

            // Sacrifice runes - increase LP from mob sacrifice
            .add(BMBlocks.RUNE_SACRIFICE.block(), AltarRuneStats.sacrifice(SACRIFICE_PER_RUNE), false)

            // Self-sacrifice runes - increase LP from player self-sacrifice
            .add(BMBlocks.RUNE_SELF_SACRIFICE.block(), AltarRuneStats.selfSacrifice(SELF_SACRIFICE_PER_RUNE), false)

            // Capacity runes - additive capacity increase
            .add(BMBlocks.RUNE_CAPACITY.block(), AltarRuneStats.capacity(CAPACITY_PER_RUNE), false)

            // Augmented capacity runes - multiplicative capacity increase
            .add(BMBlocks.RUNE_CAPACITY_AUGMENTED.block(), AltarRuneStats.augmentedCapacity(AUGMENTED_CAPACITY_POWER), false)

            // Displacement runes - increase fluid I/O rate (multiplicative)
            .add(BMBlocks.RUNE_DISLOCATION.block(), AltarRuneStats.displacement(DISLOCATION_POWER), false)

            // Orb runes - increase soul network capacity bonus
            .add(BMBlocks.RUNE_ORB.block(), AltarRuneStats.orb(ORB_CAPACITY_PER_RUNE), false)

            // Acceleration runes - reduce tick rate
            .add(BMBlocks.RUNE_ACCELERATION.block(), AltarRuneStats.acceleration(ACCELERATION_PER_RUNE), false)

            // Charging runes - enable pre-charging LP
            .add(BMBlocks.RUNE_CHARGING.block(), AltarRuneStats.charging(CHARGE_AMOUNT_PER_RUNE), false)

            // Efficiency runes - reduce LP loss on failed crafts (multiplicative)
            .add(BMBlocks.RUNE_EFFICIENCY.block(), AltarRuneStats.efficiency(EFFICIENCY_POWER), false);

        // Tier 2 Runes (double stats)
        builder
            .add(BMBlocks.RUNE_2_SPEED.block(), AltarRuneStats.speed(CONSUMPTION_PER_RUNE * 2), false)
            .add(BMBlocks.RUNE_2_SACRIFICE.block(), AltarRuneStats.sacrifice(SACRIFICE_PER_RUNE * 2), false)
            .add(BMBlocks.RUNE_2_SELF_SACRIFICE.block(), AltarRuneStats.selfSacrifice(SELF_SACRIFICE_PER_RUNE * 2), false)
            .add(BMBlocks.RUNE_2_CAPACITY.block(), AltarRuneStats.capacity(CAPACITY_PER_RUNE * 2), false)
            // Augmented capacity tier 2: compounds twice (power^2 effect per block)
            .add(BMBlocks.RUNE_2_CAPACITY_AUGMENTED.block(), AltarRuneStats.augmentedCapacity(AUGMENTED_CAPACITY_POWER * AUGMENTED_CAPACITY_POWER), false)
            // Displacement tier 2: compounds twice (power^2 effect per block)
            .add(BMBlocks.RUNE_2_DISLOCATION.block(), AltarRuneStats.displacement(DISLOCATION_POWER * DISLOCATION_POWER), false)
            .add(BMBlocks.RUNE_2_ORB.block(), AltarRuneStats.orb(ORB_CAPACITY_PER_RUNE * 2), false)
            .add(BMBlocks.RUNE_2_ACCELERATION.block(), AltarRuneStats.acceleration(ACCELERATION_PER_RUNE * 2), false)
            .add(BMBlocks.RUNE_2_CHARGING.block(), AltarRuneStats.charging(CHARGE_AMOUNT_PER_RUNE * 2), false)
            // Efficiency tier 2: compounds twice (power^2 effect per block)
            .add(BMBlocks.RUNE_2_EFFICIENCY.block(), AltarRuneStats.efficiency(EFFICIENCY_POWER * EFFICIENCY_POWER), false);
    }
}
