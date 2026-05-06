package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;

import java.util.Map;
import java.util.Optional;

public record ResetCooldownEffect(ResourceLocation id, LevelBasedValue amounts, Optional<SentientEntityEffect> effect) implements SentientEntityEffect {
    public static final MapCodec<ResetCooldownEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ResetCooldownEffect::id),
            LevelBasedValue.CODEC.fieldOf("amounts").forGetter(ResetCooldownEffect::amounts),
            SentientEntityEffect.CODEC.optionalFieldOf("reset_effect").forGetter(ResetCooldownEffect::effect)
    ).apply(builder, ResetCooldownEffect::new));

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        Map<ResourceLocation, Double> data = entity.getData(NVDataAttachments.SENTIENT_ADDITIONAL);
        data.compute(id, (key, amount) -> (double) amounts.calculate(upgradeLevel));
        entity.setData(NVDataAttachments.SENTIENT_ADDITIONAL, data);
        effect.ifPresent(livingEntityEffect -> livingEntityEffect.apply(upgradeLevel, entity));
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }
}
