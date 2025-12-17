package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Data-driven altar rune statistics that can be customized via datapacks.
 *
 * <p>Each rune block can have any combination of bonuses. All values are additive
 * except for power-based stats which use multiplicative scaling.</p>
 *
 * <h2>Static vs Dynamic Runes</h2>
 * <p>This datamap provides <b>static base values</b> for rune blocks. For dynamic runes
 * whose bonuses change at runtime (like Animus's dynamic runes), addon mods should:</p>
 * <ol>
 *   <li>Optionally register base values here</li>
 *   <li>Listen to {@link com.breakinblocks.neovitae.api.event.AltarRuneEvent.CalculateStats}</li>
 *   <li>Use {@code event.getRuneBlockEntities(MyRuneBlockEntity.class)} to find their runes</li>
 *   <li>Modify {@code event.getModifiers()} based on current rune state</li>
 * </ol>
 *
 * <h2>Modifier Types</h2>
 * <ul>
 *   <li><b>Additive</b>: Values sum up across all runes (capacity, sacrifice, selfSacrifice, orb, consumption, chargeAmount)</li>
 *   <li><b>Multiplicative</b>: Values multiply together (augmentedCapacityPower, dislocationPower, efficiencyPower)</li>
 *   <li><b>Subtractive</b>: Values subtract from base (acceleration - reduces tick rate)</li>
 * </ul>
 *
 * <h2>Example Datapack Entry</h2>
 * <pre>{@code
 * // data/neovitae/data_maps/block/altar_rune_stats.json
 * {
 *   "values": {
 *     "neovitae:sacrifice_rune": {
 *       "sacrifice_mod": 0.1
 *     },
 *     "animus:greater_sacrifice_rune": {
 *       "sacrifice_mod": 0.25,
 *       "capacity_mod": 0.1
 *     }
 *   }
 * }
 * }</pre>
 *
 * <h2>Dynamic Runes Example (Addon Code)</h2>
 * <pre>{@code
 * @SubscribeEvent
 * public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
 *     for (MyDynamicRuneBlockEntity rune : event.getRuneBlockEntities(MyDynamicRuneBlockEntity.class)) {
 *         // Modify based on current rune state
 *         double bonus = rune.getCurrentBonus(); // Changes every few seconds
 *         event.getModifiers().addSacrificeMod((float) bonus);
 *     }
 * }
 * }</pre>
 *
 * @param capacityMod Additive bonus to altar capacity (default 0.2 for capacity rune)
 * @param augmentedCapacityPower Multiplier for capacity (compounds with other aug capacity runes, default 1.075)
 * @param consumptionMod Additive bonus to crafting speed (default 0.2 for speed rune)
 * @param sacrificeMod Additive bonus to mob sacrifice LP gain (default 0.1 for sacrifice rune)
 * @param selfSacrificeMod Additive bonus to player self-sacrifice LP gain (default 0.1)
 * @param dislocationPower Multiplier for fluid I/O rate (compounds, default 1.2)
 * @param orbCapacityMod Additive bonus to soul network capacity when filling orbs (default 0.2)
 * @param accelerationMod Tick rate reduction (default 1 per rune, lower tick rate = faster)
 * @param chargeAmountMod Additive bonus to charge amount per tick (default 10)
 * @param efficiencyPower Multiplier for efficiency (compounds, default 0.85 - lower = better)
 */
public record AltarRuneStats(
        Optional<Double> capacityMod,
        Optional<Double> augmentedCapacityPower,
        Optional<Double> consumptionMod,
        Optional<Double> sacrificeMod,
        Optional<Double> selfSacrificeMod,
        Optional<Double> dislocationPower,
        Optional<Double> orbCapacityMod,
        Optional<Integer> accelerationMod,
        Optional<Integer> chargeAmountMod,
        Optional<Double> efficiencyPower
) {
    public static final Codec<AltarRuneStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("capacity_mod").forGetter(AltarRuneStats::capacityMod),
            Codec.DOUBLE.optionalFieldOf("augmented_capacity_power").forGetter(AltarRuneStats::augmentedCapacityPower),
            Codec.DOUBLE.optionalFieldOf("consumption_mod").forGetter(AltarRuneStats::consumptionMod),
            Codec.DOUBLE.optionalFieldOf("sacrifice_mod").forGetter(AltarRuneStats::sacrificeMod),
            Codec.DOUBLE.optionalFieldOf("self_sacrifice_mod").forGetter(AltarRuneStats::selfSacrificeMod),
            Codec.DOUBLE.optionalFieldOf("dislocation_power").forGetter(AltarRuneStats::dislocationPower),
            Codec.DOUBLE.optionalFieldOf("orb_capacity_mod").forGetter(AltarRuneStats::orbCapacityMod),
            Codec.INT.optionalFieldOf("acceleration_mod").forGetter(AltarRuneStats::accelerationMod),
            Codec.INT.optionalFieldOf("charge_amount_mod").forGetter(AltarRuneStats::chargeAmountMod),
            Codec.DOUBLE.optionalFieldOf("efficiency_power").forGetter(AltarRuneStats::efficiencyPower)
    ).apply(instance, AltarRuneStats::new));

    /**
     * Empty stats with no modifiers.
     */
    public static final AltarRuneStats EMPTY = new AltarRuneStats(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
    );

    // Builder methods for common rune types

    /**
     * Creates stats for a capacity rune.
     */
    public static AltarRuneStats capacity(double capacityMod) {
        return new AltarRuneStats(
                Optional.of(capacityMod), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for an augmented capacity rune (power-based).
     */
    public static AltarRuneStats augmentedCapacity(double powerBase) {
        return new AltarRuneStats(
                Optional.empty(), Optional.of(powerBase), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for a speed rune.
     */
    public static AltarRuneStats speed(double consumptionMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.of(consumptionMod), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for a sacrifice rune.
     */
    public static AltarRuneStats sacrifice(double sacrificeMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(sacrificeMod), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for a self-sacrifice rune.
     */
    public static AltarRuneStats selfSacrifice(double selfSacrificeMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(selfSacrificeMod),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for a displacement/dislocation rune (power-based).
     */
    public static AltarRuneStats displacement(double powerBase) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(powerBase), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for an orb rune.
     */
    public static AltarRuneStats orb(double orbCapacityMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(orbCapacityMod), Optional.empty(), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for an acceleration rune.
     */
    public static AltarRuneStats acceleration(int accelerationMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(accelerationMod), Optional.empty(), Optional.empty()
        );
    }

    /**
     * Creates stats for a charging rune.
     */
    public static AltarRuneStats charging(int chargeAmountMod) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(chargeAmountMod), Optional.empty()
        );
    }

    /**
     * Creates stats for an efficiency rune (power-based).
     */
    public static AltarRuneStats efficiency(double powerBase) {
        return new AltarRuneStats(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(powerBase)
        );
    }

    // Convenience getters with defaults

    public double getCapacityMod(double defaultValue) {
        return capacityMod.orElse(defaultValue);
    }

    public double getAugmentedCapacityPower(double defaultValue) {
        return augmentedCapacityPower.orElse(defaultValue);
    }

    public double getConsumptionMod(double defaultValue) {
        return consumptionMod.orElse(defaultValue);
    }

    public double getSacrificeMod(double defaultValue) {
        return sacrificeMod.orElse(defaultValue);
    }

    public double getSelfSacrificeMod(double defaultValue) {
        return selfSacrificeMod.orElse(defaultValue);
    }

    public double getDislocationPower(double defaultValue) {
        return dislocationPower.orElse(defaultValue);
    }

    public double getOrbCapacityMod(double defaultValue) {
        return orbCapacityMod.orElse(defaultValue);
    }

    public int getAccelerationMod(int defaultValue) {
        return accelerationMod.orElse(defaultValue);
    }

    public int getChargeAmountMod(int defaultValue) {
        return chargeAmountMod.orElse(defaultValue);
    }

    public double getEfficiencyPower(double defaultValue) {
        return efficiencyPower.orElse(defaultValue);
    }
}
