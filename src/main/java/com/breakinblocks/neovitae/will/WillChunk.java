package com.breakinblocks.neovitae.will;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stores demon will amounts for a chunk.
 *
 * <p>Each chunk can hold up to a configurable maximum of each will type.
 * The maximum is determined by:</p>
 * <ol>
 *   <li>Base maximum from server config ({@code neovitae-server.toml})</li>
 *   <li>Per-chunk bonuses from rituals or other effects</li>
 * </ol>
 *
 * <p>Effective maximum = base + bonus for each will type.</p>
 */
public class WillChunk {
    /**
     * @deprecated Use {@link #getMaxWill(EnumWillType)} instead for proper config + bonus support.
     * This constant is kept for backwards compatibility but should not be relied upon.
     */
    @Deprecated
    public static final double MAX_WILL = 100.0;
    public static final double DEFAULT_WILL = 0.0;

    public static final Codec<WillChunk> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("raw").forGetter(w -> w.getWill(EnumWillType.DEFAULT)),
            Codec.DOUBLE.fieldOf("corrosive").forGetter(w -> w.getWill(EnumWillType.CORROSIVE)),
            Codec.DOUBLE.fieldOf("destructive").forGetter(w -> w.getWill(EnumWillType.DESTRUCTIVE)),
            Codec.DOUBLE.fieldOf("vengeful").forGetter(w -> w.getWill(EnumWillType.VENGEFUL)),
            Codec.DOUBLE.fieldOf("steadfast").forGetter(w -> w.getWill(EnumWillType.STEADFAST)),
            // Bonus fields (optional, default to 0)
            Codec.DOUBLE.optionalFieldOf("bonus_raw", 0.0).forGetter(w -> w.getMaxBonus(EnumWillType.DEFAULT)),
            Codec.DOUBLE.optionalFieldOf("bonus_corrosive", 0.0).forGetter(w -> w.getMaxBonus(EnumWillType.CORROSIVE)),
            Codec.DOUBLE.optionalFieldOf("bonus_destructive", 0.0).forGetter(w -> w.getMaxBonus(EnumWillType.DESTRUCTIVE)),
            Codec.DOUBLE.optionalFieldOf("bonus_vengeful", 0.0).forGetter(w -> w.getMaxBonus(EnumWillType.VENGEFUL)),
            Codec.DOUBLE.optionalFieldOf("bonus_steadfast", 0.0).forGetter(w -> w.getMaxBonus(EnumWillType.STEADFAST))
    ).apply(instance, WillChunk::new));

    private final EnumMap<EnumWillType, Double> willAmounts;
    private final EnumMap<EnumWillType, Double> maxBonuses;

    public WillChunk() {
        this.willAmounts = new EnumMap<>(EnumWillType.class);
        this.maxBonuses = new EnumMap<>(EnumWillType.class);
        for (EnumWillType type : EnumWillType.values()) {
            willAmounts.put(type, DEFAULT_WILL);
            maxBonuses.put(type, 0.0);
        }
    }

    /**
     * Legacy constructor for backwards compatibility (no bonuses).
     */
    public WillChunk(double raw, double corrosive, double destructive, double vengeful, double steadfast) {
        this(raw, corrosive, destructive, vengeful, steadfast, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Full constructor with will amounts and bonuses.
     */
    public WillChunk(double raw, double corrosive, double destructive, double vengeful, double steadfast,
                     double bonusRaw, double bonusCorrosive, double bonusDestructive, double bonusVengeful, double bonusSteadfast) {
        this.willAmounts = new EnumMap<>(EnumWillType.class);
        this.maxBonuses = new EnumMap<>(EnumWillType.class);
        willAmounts.put(EnumWillType.DEFAULT, raw);
        willAmounts.put(EnumWillType.CORROSIVE, corrosive);
        willAmounts.put(EnumWillType.DESTRUCTIVE, destructive);
        willAmounts.put(EnumWillType.VENGEFUL, vengeful);
        willAmounts.put(EnumWillType.STEADFAST, steadfast);
        maxBonuses.put(EnumWillType.DEFAULT, bonusRaw);
        maxBonuses.put(EnumWillType.CORROSIVE, bonusCorrosive);
        maxBonuses.put(EnumWillType.DESTRUCTIVE, bonusDestructive);
        maxBonuses.put(EnumWillType.VENGEFUL, bonusVengeful);
        maxBonuses.put(EnumWillType.STEADFAST, bonusSteadfast);
    }

    /**
     * Gets the amount of will of the specified type in this chunk.
     */
    public double getWill(EnumWillType type) {
        return willAmounts.getOrDefault(type, DEFAULT_WILL);
    }

    /**
     * Sets the will amount of the specified type.
     * Clamped to 0 and the effective maximum for this type.
     */
    public void setWill(EnumWillType type, double amount) {
        double max = getMaxWill(type);
        willAmounts.put(type, Math.max(0, Math.min(max, amount)));
    }

    /**
     * Gets the maximum will capacity for a specific type in this chunk.
     * This is the base config value plus any per-chunk bonuses.
     *
     * @param type The will type
     * @return The effective maximum will capacity
     */
    public double getMaxWill(EnumWillType type) {
        double base = getBaseMaxWill(type);
        double bonus = getMaxBonus(type);
        return base + bonus;
    }

    /**
     * Gets the base maximum will from server config.
     * Falls back to 100.0 if config is not yet loaded.
     */
    private double getBaseMaxWill(EnumWillType type) {
        try {
            return NeoVitae.SERVER_CONFIG.getBaseMaxWill(type);
        } catch (Exception e) {
            // Config may not be loaded yet during deserialization
            return 100.0;
        }
    }

    /**
     * Gets the bonus maximum will capacity for a type in this chunk.
     * Bonuses are added by rituals or other effects.
     *
     * @param type The will type
     * @return The bonus capacity (0 if none)
     */
    public double getMaxBonus(EnumWillType type) {
        return maxBonuses.getOrDefault(type, 0.0);
    }

    /**
     * Sets the bonus maximum will capacity for a type.
     *
     * @param type The will type
     * @param bonus The new bonus value (must be >= 0)
     */
    public void setMaxBonus(EnumWillType type, double bonus) {
        maxBonuses.put(type, Math.max(0, bonus));
    }

    /**
     * Adds to the bonus maximum will capacity for a type.
     *
     * @param type The will type
     * @param amount The amount to add (can be negative to reduce)
     * @return The new bonus value
     */
    public double addMaxBonus(EnumWillType type, double amount) {
        double newBonus = Math.max(0, getMaxBonus(type) + amount);
        setMaxBonus(type, newBonus);
        return newBonus;
    }

    /**
     * Checks if this chunk has any max bonuses.
     *
     * @return true if any will type has a bonus
     */
    public boolean hasMaxBonuses() {
        for (double bonus : maxBonuses.values()) {
            if (bonus > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds will to the chunk.
     * @return The amount actually added (may be less if at cap)
     */
    public double addWill(EnumWillType type, double amount) {
        double current = getWill(type);
        double max = getMaxWill(type);
        double toAdd = Math.min(amount, max - current);
        if (toAdd > 0) {
            willAmounts.put(type, current + toAdd);
        }
        return toAdd;
    }

    /**
     * Drains will from the chunk.
     * @return The amount actually drained (may be less if not enough)
     */
    public double drainWill(EnumWillType type, double amount) {
        double current = getWill(type);
        double toDrain = Math.min(amount, current);
        if (toDrain > 0) {
            willAmounts.put(type, current - toDrain);
        }
        return toDrain;
    }

    /**
     * Gets the total will of all types in this chunk.
     */
    public double getTotalWill() {
        double total = 0;
        for (double amount : willAmounts.values()) {
            total += amount;
        }
        return total;
    }

    /**
     * Gets the dominant will type in this chunk (highest amount).
     */
    public EnumWillType getDominantType() {
        EnumWillType dominant = EnumWillType.DEFAULT;
        double maxAmount = 0;
        for (Map.Entry<EnumWillType, Double> entry : willAmounts.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                dominant = entry.getKey();
            }
        }
        return dominant;
    }

    /**
     * Checks if the chunk has any will.
     */
    public boolean hasWill() {
        return getTotalWill() > 0;
    }

    /**
     * Creates a copy of this WillChunk, including bonuses.
     */
    public WillChunk copy() {
        WillChunk copy = new WillChunk();
        for (EnumWillType type : EnumWillType.values()) {
            copy.willAmounts.put(type, getWill(type));
            copy.maxBonuses.put(type, getMaxBonus(type));
        }
        return copy;
    }

    /**
     * Gets the fill ratio (current/max) for a specific will type.
     * Useful for display purposes.
     *
     * @param type The will type
     * @return Ratio from 0.0 to 1.0
     */
    public double getFillRatio(EnumWillType type) {
        double max = getMaxWill(type);
        if (max <= 0) return 0;
        return Math.min(1.0, getWill(type) / max);
    }
}
