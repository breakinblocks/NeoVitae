package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Pack-author-controllable visual effect bound to an altar tier.
 *
 * Each effect lists the offsets (relative to the Ara Vitae core) where it
 * originates and the colour it tints particles/streams with. The {@link AltarEffectType}
 * names the rendering style; AraVitaeTile (server-side particles) and
 * AraVitaeRenderer (client-side hover quads) decide what to do with each type.
 */
public record AltarEffect(AltarEffectType type, List<BlockPos> origins, int color) {
    public static final Codec<AltarEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AltarEffectType.CODEC.fieldOf("type").forGetter(AltarEffect::type),
            Codec.list(BlockPos.CODEC).fieldOf("origins").forGetter(AltarEffect::origins),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AltarEffect::color)
    ).apply(instance, AltarEffect::new));
}
