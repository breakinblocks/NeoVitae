package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import com.breakinblocks.neovitae.common.sentient.SentientValueEffect;

public record MultiplyIncreaseValue(LevelBasedValue amounts) implements SentientValueEffect {
    public static final MapCodec<MultiplyIncreaseValue> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            LevelBasedValue.CODEC.fieldOf("amounts").forGetter(MultiplyIncreaseValue::amounts)
    ).apply(builder, MultiplyIncreaseValue::new));

    @Override
    public float process(int level, LootContext lootContext, float value) {
        return value * (1 + amounts.calculate(level));
    }

    @Override
    public MapCodec<? extends SentientValueEffect> codec() {
        return CODEC;
    }
}
