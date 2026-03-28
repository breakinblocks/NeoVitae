package com.breakinblocks.neovitae.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ColoredParticleOptions(ParticleType<ColoredParticleOptions> type, int color) implements ParticleOptions {

    public static MapCodec<ColoredParticleOptions> codec(ParticleType<ColoredParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("color").forGetter(ColoredParticleOptions::color)
        ).apply(instance, color -> new ColoredParticleOptions(type, color)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ColoredParticleOptions> streamCodec(ParticleType<ColoredParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ColoredParticleOptions::color,
                color -> new ColoredParticleOptions(type, color)
        );
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }
}
