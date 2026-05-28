package com.breakinblocks.neovitae.spiritus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;
import java.util.Map;

public class SpiritusChunk {
    @Deprecated
    public static final double MAX_SPIRITUS = 100.0;
    public static final double DEFAULT_SPIRITUS = 0.0;

    public static final Codec<SpiritusChunk> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("raw").forGetter(w -> w.getSpiritus(SpiritusType.RAW)),
            Codec.DOUBLE.fieldOf("ruina").forGetter(w -> w.getSpiritus(SpiritusType.RUINA)),
            Codec.DOUBLE.fieldOf("nihilum").forGetter(w -> w.getSpiritus(SpiritusType.NIHILUM)),
            Codec.DOUBLE.fieldOf("vindicta").forGetter(w -> w.getSpiritus(SpiritusType.VINDICTA)),
            Codec.DOUBLE.fieldOf("invictus").forGetter(w -> w.getSpiritus(SpiritusType.INVICTUS)),
            Codec.DOUBLE.optionalFieldOf("bonus_raw", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.RAW)),
            Codec.DOUBLE.optionalFieldOf("bonus_ruina", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.RUINA)),
            Codec.DOUBLE.optionalFieldOf("bonus_nihilum", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.NIHILUM)),
            Codec.DOUBLE.optionalFieldOf("bonus_vindicta", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.VINDICTA)),
            Codec.DOUBLE.optionalFieldOf("bonus_invictus", 0.0).forGetter(w -> w.getMaxBonus(SpiritusType.INVICTUS))
    ).apply(instance, SpiritusChunk::new));

    private final EnumMap<SpiritusType, Double> spiritusAmounts;
    private final EnumMap<SpiritusType, Double> maxBonuses;

    private double growthMultiplier = 1.0;
    private long growthMultiplierExpiryTick = 0L;
    private double injectionMultiplier = 1.0;
    private long injectionMultiplierExpiryTick = 0L;
    private SpiritusType injectionAspectBias = SpiritusType.RAW;
    private long currentTick = 0L;

    public SpiritusChunk() {
        this.spiritusAmounts = new EnumMap<>(SpiritusType.class);
        this.maxBonuses = new EnumMap<>(SpiritusType.class);
        for (SpiritusType type : SpiritusType.values()) {
            spiritusAmounts.put(type, DEFAULT_SPIRITUS);
            maxBonuses.put(type, 0.0);
        }
    }

    public SpiritusChunk(double raw, double ruina, double nihilum, double vindicta, double invictus) {
        this(raw, ruina, nihilum, vindicta, invictus, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public SpiritusChunk(double raw, double ruina, double nihilum, double vindicta, double invictus,
                     double bonusRaw, double bonusRuina, double bonusNihilum, double bonusVindicta, double bonusInvictus) {
        this.spiritusAmounts = new EnumMap<>(SpiritusType.class);
        this.maxBonuses = new EnumMap<>(SpiritusType.class);
        spiritusAmounts.put(SpiritusType.RAW, raw);
        spiritusAmounts.put(SpiritusType.RUINA, ruina);
        spiritusAmounts.put(SpiritusType.NIHILUM, nihilum);
        spiritusAmounts.put(SpiritusType.VINDICTA, vindicta);
        spiritusAmounts.put(SpiritusType.INVICTUS, invictus);
        maxBonuses.put(SpiritusType.RAW, bonusRaw);
        maxBonuses.put(SpiritusType.RUINA, bonusRuina);
        maxBonuses.put(SpiritusType.NIHILUM, bonusNihilum);
        maxBonuses.put(SpiritusType.VINDICTA, bonusVindicta);
        maxBonuses.put(SpiritusType.INVICTUS, bonusInvictus);
    }

    public double getSpiritus(SpiritusType type) {
        return spiritusAmounts.getOrDefault(type, DEFAULT_SPIRITUS);
    }

    public void setSpiritus(SpiritusType type, double amount) {
        double max = getMaxSpiritus(type);
        spiritusAmounts.put(type, Math.max(0, Math.min(max, amount)));
    }

    public double getMaxSpiritus(SpiritusType type) {
        double base = getBaseMaxSpiritus(type);
        double bonus = getMaxBonus(type);
        return base + bonus;
    }

    private double getBaseMaxSpiritus(SpiritusType type) {
        try {
            return NeoVitae.SERVER_CONFIG.getBaseMaxSpiritus(type);
        } catch (Exception e) {
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

    public double addSpiritus(SpiritusType type, double amount) {
        InjectionResult result = applyInjectionMultiplier(type, amount);
        return addRaw(result.baseType(), result.baseAmount()) + addRaw(result.biasType(), result.biasAmount());
    }

    private double addRaw(SpiritusType type, double amount) {
        if (amount <= 0) return 0;
        double current = getSpiritus(type);
        double max = getMaxSpiritus(type);
        double toAdd = Math.min(amount, max - current);
        if (toAdd > 0) {
            spiritusAmounts.put(type, current + toAdd);
        }
        return toAdd;
    }

    private record InjectionResult(SpiritusType baseType, double baseAmount, SpiritusType biasType, double biasAmount) {}

    private InjectionResult applyInjectionMultiplier(SpiritusType type, double amount) {
        double mult = getInjectionMultiplier();
        if (mult <= 1.0 || amount <= 0) {
            return new InjectionResult(type, amount, type, 0);
        }
        double total = amount * mult;
        double bonus = total - amount;
        SpiritusType biasTarget = (type == SpiritusType.RAW && injectionAspectBias != SpiritusType.RAW)
                ? injectionAspectBias
                : type;
        return new InjectionResult(type, amount, biasTarget, bonus);
    }

    public double drainSpiritus(SpiritusType type, double amount) {
        double current = getSpiritus(type);
        double toDrain = Math.min(amount, current);
        if (toDrain > 0) {
            spiritusAmounts.put(type, current - toDrain);
        }
        return toDrain;
    }

    public double getTotalSpiritus() {
        double total = 0;
        for (double amount : spiritusAmounts.values()) {
            total += amount;
        }
        return total;
    }

    public SpiritusType getDominantType() {
        SpiritusType dominant = SpiritusType.RAW;
        double maxAmount = 0;
        for (Map.Entry<SpiritusType, Double> entry : spiritusAmounts.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                dominant = entry.getKey();
            }
        }
        return dominant;
    }

    public boolean hasSpiritus() {
        return getTotalSpiritus() > 0;
    }

    public SpiritusChunk copy() {
        SpiritusChunk copy = new SpiritusChunk();
        for (SpiritusType type : SpiritusType.values()) {
            copy.spiritusAmounts.put(type, getSpiritus(type));
            copy.maxBonuses.put(type, getMaxBonus(type));
        }
        copy.growthMultiplier = this.growthMultiplier;
        copy.growthMultiplierExpiryTick = this.growthMultiplierExpiryTick;
        copy.injectionMultiplier = this.injectionMultiplier;
        copy.injectionMultiplierExpiryTick = this.injectionMultiplierExpiryTick;
        copy.injectionAspectBias = this.injectionAspectBias;
        copy.currentTick = this.currentTick;
        return copy;
    }

    public double getFillRatio(SpiritusType type) {
        double max = getMaxSpiritus(type);
        if (max <= 0) return 0;
        return Math.min(1.0, getSpiritus(type) / max);
    }

    public void tickRitualBuffs(long gameTime) {
        this.currentTick = gameTime;
        if (gameTime > growthMultiplierExpiryTick && growthMultiplier != 1.0) {
            growthMultiplier = 1.0;
        }
        if (gameTime > injectionMultiplierExpiryTick && injectionMultiplier != 1.0) {
            injectionMultiplier = 1.0;
            injectionAspectBias = SpiritusType.RAW;
        }
    }

    public double getGrowthMultiplier() {
        if (currentTick > growthMultiplierExpiryTick) return 1.0;
        return growthMultiplier;
    }

    public void setGrowthMultiplier(double multiplier, long durationTicks, long currentGameTime) {
        this.growthMultiplier = multiplier;
        this.growthMultiplierExpiryTick = currentGameTime + durationTicks;
        this.currentTick = currentGameTime;
    }

    public double getInjectionMultiplier() {
        if (currentTick > injectionMultiplierExpiryTick) return 1.0;
        return injectionMultiplier;
    }

    public void setInjectionMultiplier(double multiplier, SpiritusType aspectBias, long durationTicks, long currentGameTime) {
        this.injectionMultiplier = multiplier;
        this.injectionAspectBias = aspectBias == null ? SpiritusType.RAW : aspectBias;
        this.injectionMultiplierExpiryTick = currentGameTime + durationTicks;
        this.currentTick = currentGameTime;
    }

    public SpiritusType getInjectionAspectBias() {
        return injectionAspectBias;
    }
}
