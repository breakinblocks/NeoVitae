package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientValueEffect;

public record DelegateEffect(SentientEntityEffect effect) implements SentientValueEffect {
    public static final MapCodec<DelegateEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientEntityEffect.CODEC.fieldOf("delegate_effect").forGetter(DelegateEffect::effect)
    ).apply(builder, DelegateEffect::new));

    @Override
    public float process(int level, LootContext lootContext, float value) {
        effect.apply(level, lootContext.getOptionalParameter(LootContextParams.ATTACKING_ENTITY));
        return value;
    }

    @Override
    public MapCodec<? extends SentientValueEffect> codec() {
        return CODEC;
    }
}
