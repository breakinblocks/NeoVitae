package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.SentientValueEffect;

public record MultiplyReduceValue(LevelBasedValue amounts) implements SentientValueEffect {
    public static final MapCodec<MultiplyReduceValue> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            LevelBasedValue.CODEC.fieldOf("amounts").forGetter(MultiplyReduceValue::amounts)
    ).apply(builder, MultiplyReduceValue::new));

    @Override
    public float process(int level, LootContext lootContext, float value) {
        float multi = amounts.calculate(level);
        float ret = value * (1 - multi);
        // Debug: NeoVitae.LOGGER.info("multi: {} for level {}, ret: {}", multi, level, ret);
        return ret;
    }

    @Override
    public MapCodec<? extends SentientValueEffect> codec() {
        return CODEC;
    }
}
