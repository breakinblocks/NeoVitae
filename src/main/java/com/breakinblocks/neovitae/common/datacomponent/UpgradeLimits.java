package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

public record UpgradeLimits(boolean allowOthers, Object2FloatOpenHashMap<Holder<SentientUpgrade>> limits) {
    public static final Codec<UpgradeLimits> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.fieldOf("allow_others").forGetter(UpgradeLimits::allowOthers),
            NVDataComponents.UPGRADE_HOLDER_CODEC.fieldOf("limits").forGetter(UpgradeLimits::limits)
    ).apply(builder, UpgradeLimits::new));

    public static final UpgradeLimits EMPTY = new UpgradeLimits(true, SentientHelper.EMPTY_UPGRADE_MAP);

    public float getLimit(Holder<SentientUpgrade> upgrade) {
        float def = allowOthers ? -1 : 0;
        return limits.getOrDefault(upgrade, def);
    }

    public int capLevel(Holder<SentientUpgrade> upgrade) {
        int max = SentientHelper.getMaxLevel(upgrade);
        if (!limits.containsKey(upgrade)) {
            return allowOthers ? max : 0;
        }
        return Math.min(max, SentientHelper.getLevelFromXp(upgrade, limits.getFloat(upgrade)));
    }

    public UpgradeLimits withCap(Holder<SentientUpgrade> upgrade, int level, Iterable<? extends Holder<SentientUpgrade>> trainable) {
        Object2FloatOpenHashMap<Holder<SentientUpgrade>> map = limits.clone();
        if (!allowOthers) {
            for (Holder<SentientUpgrade> other : trainable) {
                if (!map.containsKey(other)) {
                    map.put(other, 0f);
                }
            }
        }
        int max = SentientHelper.getMaxLevel(upgrade);
        int clamped = Mth.clamp(level, 0, max);
        if (clamped >= max) {
            map.removeFloat(upgrade);
        } else {
            map.put(upgrade, SentientHelper.getExpForLevel(upgrade, clamped));
        }
        return new UpgradeLimits(true, map);
    }
}
