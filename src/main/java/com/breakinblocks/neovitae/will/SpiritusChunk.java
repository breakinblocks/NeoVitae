package com.breakinblocks.neovitae.will;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stores spiritus amounts for a chunk.
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
public class SpiritusChunk {
    /**
     * @deprecated Use {@link #getMaxWill(SpiritusType)} instead for proper config + bonus support.
     * This constant is kept for backwards compatibility but should not be relied upon.
     */
    @Deprecated
    public static final double MAX_WILL = 100.0;
    public static final double DEFAULT_WILL = 0.0;

    public static final Codec<SpiritusChunk> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("raw").forGetter(w -> w.getWill(SpiritusType.DEFAULT)),
            Codec.DOUBLE.fieldOf("corrosive").forGetter(w -> w.getWill(SpiritusType.CORROSIVE)),
            Codec.DOUBLE.fieldOf("destructive").forGetter(w -> w.getWill(SpiritusType.DESTRUCTIVE)),
            Codec.DOUBLE.fieldOf("vengeful").forGetter(w -> w.getWill(SpiritusType.VENGEFUL)),
            Codec.DOUBLE.fieldOf("steadfast").forGetter(w -> w.getWill(SpiritusType.STEADFAST)),
            Codec.DOUBLE.optionalFieldOf("bonus_raw", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.DEFAULT)),
            Codec.DOUBLE.optionalFieldOf("bonus_corrosive", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.CORROSIVE)),
            Codec.DOUBLE.optionalFieldOf("bonus_destructive", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.DESTRUCTIVE)),
            Codec.DOUBLE.optionalFieldOf("bonus_vengeful", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.VENGEFUL)),
            Codec.DOUBLE.optionalFieldOf("bonus_steadfast", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.STEADFAST))
    ).apply(instance, SpiritusChunk::new));

    private final EnumMap<SpiritusType, Double> willAmounts;
    private final EnumMap<SpiritusType, Double> maxBonuses;

    public SpiritusChunk() {
        this.willAmounts = new EnumMap<>(SpiritusType.class);
        this.maxBonuses = new EnumMap<>(SpiritusType.class);
        for (SpiritusType type : SpiritusType.values()) {
            willAmounts.put(type, DEFAULT_WILL);
            maxBonuses.put(type, 0.0);
        }
    }

    public SpiritusChunk(double raw, double corrosive, double destructive, double vengeful, double steadfast) {
        this(raw, corrosive, destructive, vengeful, steadfast, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public SpiritusChunk(double raw, double corrosive, double destructive, double vengeful, double steadfast,
                     double bonusRaw, double bonusCorrosive, double bonusDestructive, double bonusVengeful, double bonusSteadfast) {
        this.willAmounts = new EnumMap<>(SpiritusType.class);
        this.maxBonuses = new EnumMap<>(SpiritusType.class);
        willAmounts.put(SpiritusType.DEFAULT, raw);
        willAmounts.put(SpiritusType.CORROSIVE, corrosive);
        willAmounts.put(SpiritusType.DESTRUCTIVE, destructive);
        willAmounts.put(SpiritusType.VENGEFUL, vengeful);
        willAmounts.put(SpiritusType.STEADFAST, steadfast);
        maxBonuses.put(SpiritusType.DEFAULT, bonusRaw);
        maxBonuses.put(SpiritusType.CORROSIVE, bonusCorrosive);
        maxBonuses.put(SpiritusType.DESTRUCTIVE, bonusDestructive);
        maxBonuses.put(SpiritusType.VENGEFUL, bonusVengeful);
        maxBonuses.put(SpiritusType.STEADFAST, bonusSteadfast);
    }

    public double getWill(SpiritusType type) {
        return willAmounts.getOrDefault(type, DEFAULT_WILL);
    }

    public void setWill(SpiritusType type, double amount) {
        double max = getMaxWill(type);
        willAmounts.put(type, Math.max(0, Math.min(max, amount)));
    }

    public double getMaxWill(SpiritusType type) {
        double base = getBaseMaxWill(type);
        double bonus = getMaxBonus(type);
        return base + bonus;
    }

    /**
     * Gets the base maximum will from server config.
     * Falls back to 100.0 if config is not yet loaded.
     */
    private double getBaseMaxWill(SpiritusType type) {
        try {
            return NeoVitae.SERVER_CONFIG.getBaseMaxWill(type);
        } catch (Exception e) {
            // Config may not be loaded yet during deserialization
            return 100.0;
        }
    }

    public double getMaxBonus(SpiritusType type) {
        return maxBonuses.getOrDefault(type, 0.0);
    }

    public void setMaxBonus(SpiritusType type, double bonus) {
        maxBonuses.put(type, Math.max(0, bonus));
    }

    public double addMaxBonus(SpiritusType type, double amount) {
        double newBonus = Math.max(0, getMaxBonus(type) + amount);
        setMaxBonus(type, newBonus);
        return newBonus;
    }

    public boolean hasMaxBonuses() {
        for (double bonus : maxBonuses.values()) {
            if (bonus > 0) {
                return true;
            }
        }
        return false;
    }

    public double addWill(SpiritusType type, double amount) {
        double current = getWill(type);
        double max = getMaxWill(type);
        double toAdd = Math.min(amount, max - current);
        if (toAdd > 0) {
            willAmounts.put(type, current + toAdd);
        }
        return toAdd;
    }

    public double drainWill(SpiritusType type, double amount) {
        double current = getWill(type);
        double toDrain = Math.min(amount, current);
        if (toDrain > 0) {
            willAmounts.put(type, current - toDrain);
        }
        return toDrain;
    }

    public double getTotalWill() {
        double total = 0;
        for (double amount : willAmounts.values()) {
            total += amount;
        }
        return total;
    }

    public SpiritusType getDominantType() {
        SpiritusType dominant = SpiritusType.DEFAULT;
        double maxAmount = 0;
        for (Map.Entry<SpiritusType, Double> entry : willAmounts.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                dominant = entry.getKey();
            }
        }
        return dominant;
    }

    public boolean hasWill() {
        return getTotalWill() > 0;
    }

    public SpiritusChunk copy() {
        SpiritusChunk copy = new SpiritusChunk();
        for (SpiritusType type : SpiritusType.values()) {
            copy.willAmounts.put(type, getWill(type));
            copy.maxBonuses.put(type, getMaxBonus(type));
        }
        return copy;
    }

    public double getFillRatio(SpiritusType type) {
        double max = getMaxWill(type);
        if (max <= 0) return 0;
        return Math.min(1.0, getWill(type) / max);
    }
}
