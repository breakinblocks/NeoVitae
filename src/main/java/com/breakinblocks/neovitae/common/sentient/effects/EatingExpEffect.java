package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public record EatingExpEffect(Holder<SentientUpgrade> upgrade) implements SentientEntityEffect {
    public static final MapCodec<EatingExpEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(EatingExpEffect::upgrade)
    ).apply(builder, EatingExpEffect::new));

    private static final Map<UUID, Integer> LAST_FOOD_LEVELS = new ConcurrentHashMap<>();

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        Player wearer = (Player) entity;
        int current = wearer.getFoodData().getFoodLevel();
        int last = LAST_FOOD_LEVELS.getOrDefault(wearer.getUUID(), current);
        LAST_FOOD_LEVELS.put(wearer.getUUID(), current);
        int exp = Math.max(last - current, 0);
        SentientHelper.applyExp(wearer, upgrade, exp);
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }
}
