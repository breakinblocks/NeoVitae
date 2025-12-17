package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * Data-driven ritual statistics that can be customized via datapacks.
 *
 * @param activationCost LP cost to activate the ritual
 * @param refreshCost LP cost per refresh tick while running
 * @param refreshTime Ticks between each refresh (lower = faster updates)
 * @param crystalLevel Required activation crystal tier (0 = weak, 1 = standard, 2 = awakened)
 * @param rangeLimits Map of range name to RangeLimit for area customization
 * @param enabled Whether the ritual is enabled (disabled rituals cannot be activated)
 */
public record RitualStats(
        int activationCost,
        int refreshCost,
        int refreshTime,
        int crystalLevel,
        Map<String, RangeLimit> rangeLimits,
        boolean enabled
) {
    public static final Codec<RitualStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("activation_cost").forGetter(RitualStats::activationCost),
            Codec.INT.fieldOf("refresh_cost").forGetter(RitualStats::refreshCost),
            Codec.INT.optionalFieldOf("refresh_time", 20).forGetter(RitualStats::refreshTime),
            Codec.INT.optionalFieldOf("crystal_level", 0).forGetter(RitualStats::crystalLevel),
            Codec.unboundedMap(Codec.STRING, RangeLimit.CODEC)
                    .optionalFieldOf("range_limits", Map.of())
                    .forGetter(RitualStats::rangeLimits),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(RitualStats::enabled)
    ).apply(instance, RitualStats::new));

    /**
     * Creates a simple RitualStats with basic parameters.
     */
    public static RitualStats simple(int activationCost, int refreshCost) {
        return new RitualStats(activationCost, refreshCost, 20, 0, Map.of(), true);
    }

    /**
     * Creates a RitualStats with all timing parameters.
     */
    public static RitualStats timed(int activationCost, int refreshCost, int refreshTime, int crystalLevel) {
        return new RitualStats(activationCost, refreshCost, refreshTime, crystalLevel, Map.of(), true);
    }

    /**
     * Gets the range limit for a specific area, or a default if not specified.
     */
    public RangeLimit getRangeLimit(String rangeName, RangeLimit defaultLimit) {
        return rangeLimits.getOrDefault(rangeName, defaultLimit);
    }

    /**
     * Defines limits for a ritual's effect area.
     */
    public record RangeLimit(
            int maxVolume,
            int maxHorizontalRadius,
            int maxVerticalRadius
    ) {
        public static final Codec<RangeLimit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("max_volume", Integer.MAX_VALUE).forGetter(RangeLimit::maxVolume),
                Codec.INT.optionalFieldOf("max_horizontal_radius", 256).forGetter(RangeLimit::maxHorizontalRadius),
                Codec.INT.optionalFieldOf("max_vertical_radius", 256).forGetter(RangeLimit::maxVerticalRadius)
        ).apply(instance, RangeLimit::new));

        public static RangeLimit of(int maxVolume, int maxHorizontalRadius, int maxVerticalRadius) {
            return new RangeLimit(maxVolume, maxHorizontalRadius, maxVerticalRadius);
        }
    }
}
