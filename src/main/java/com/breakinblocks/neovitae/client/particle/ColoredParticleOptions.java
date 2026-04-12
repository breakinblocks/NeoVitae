package com.breakinblocks.neovitae.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ColoredParticleOptions(ParticleType<ColoredParticleOptions> type, int color, boolean rawColor) implements ParticleOptions {

    public ColoredParticleOptions(ParticleType<ColoredParticleOptions> type, int color) {
        this(type, color, false);
    }

    public static MapCodec<ColoredParticleOptions> codec(ParticleType<ColoredParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("color").forGetter(ColoredParticleOptions::color),
                Codec.BOOL.optionalFieldOf("raw_color", false).forGetter(ColoredParticleOptions::rawColor)
        ).apply(instance, (color, raw) -> new ColoredParticleOptions(type, color, raw)));
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ColoredParticleOptions> streamCodec(ParticleType<ColoredParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ColoredParticleOptions::color,
                ByteBufCodecs.BOOL, ColoredParticleOptions::rawColor,
                (color, raw) -> new ColoredParticleOptions(type, color, raw)
        );
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }
}
