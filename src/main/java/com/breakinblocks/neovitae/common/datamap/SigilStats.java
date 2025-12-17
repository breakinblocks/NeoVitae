package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Data-driven sigil statistics that can be customized via datapacks.
 *
 * @param lpCost LP cost per activation (for single-use sigils) or per drain tick (for toggleable sigils)
 * @param drainInterval For toggleable sigils, how often LP is drained (in ticks). Default is 100 (5 seconds).
 * @param range Optional range parameter for area-effect sigils (e.g., magnetism radius)
 * @param verticalRange Optional vertical range parameter
 * @param effectDuration Optional effect duration in ticks (e.g., for potion effects)
 * @param effectLevel Optional effect level/potency (e.g., for speed boost level)
 */
public record SigilStats(
        int lpCost,
        int drainInterval,
        Optional<Integer> range,
        Optional<Integer> verticalRange,
        Optional<Integer> effectDuration,
        Optional<Integer> effectLevel
) {
    // Default drain interval (5 seconds)
    public static final int DEFAULT_DRAIN_INTERVAL = 100;

    public static final Codec<SigilStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("lp_cost").forGetter(SigilStats::lpCost),
            Codec.INT.optionalFieldOf("drain_interval", DEFAULT_DRAIN_INTERVAL).forGetter(SigilStats::drainInterval),
            Codec.INT.optionalFieldOf("range").forGetter(SigilStats::range),
            Codec.INT.optionalFieldOf("vertical_range").forGetter(SigilStats::verticalRange),
            Codec.INT.optionalFieldOf("effect_duration").forGetter(SigilStats::effectDuration),
            Codec.INT.optionalFieldOf("effect_level").forGetter(SigilStats::effectLevel)
    ).apply(instance, SigilStats::new));

    /**
     * Creates a simple SigilStats with just LP cost.
     */
    public static SigilStats simple(int lpCost) {
        return new SigilStats(lpCost, DEFAULT_DRAIN_INTERVAL, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a toggleable SigilStats with LP cost and custom drain interval.
     */
    public static SigilStats toggleable(int lpCost, int drainInterval) {
        return new SigilStats(lpCost, drainInterval, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a SigilStats with range parameters for area-effect sigils.
     */
    public static SigilStats ranged(int lpCost, int range, int verticalRange) {
        return new SigilStats(lpCost, DEFAULT_DRAIN_INTERVAL, Optional.of(range), Optional.of(verticalRange), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a full SigilStats with all parameters.
     */
    public static SigilStats full(int lpCost, int drainInterval, int range, int verticalRange, int effectDuration, int effectLevel) {
        return new SigilStats(lpCost, drainInterval, Optional.of(range), Optional.of(verticalRange), Optional.of(effectDuration), Optional.of(effectLevel));
    }

    // Convenience getters with defaults
    public int getRange(int defaultValue) {
        return range.orElse(defaultValue);
    }

    public int getVerticalRange(int defaultValue) {
        return verticalRange.orElse(defaultValue);
    }

    public int getEffectDuration(int defaultValue) {
        return effectDuration.orElse(defaultValue);
    }

    public int getEffectLevel(int defaultValue) {
        return effectLevel.orElse(defaultValue);
    }
}
