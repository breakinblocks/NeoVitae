package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BloodOrb(int tier, int fluidCapacity, int animaCapacity, int fillRate) {
    public static Codec<BloodOrb> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.INT.fieldOf("tier").forGetter(BloodOrb::tier),
                    Codec.INT.fieldOf("fluidCapacity").forGetter(BloodOrb::fluidCapacity),
                    Codec.INT.fieldOf("animaCapacity").forGetter(BloodOrb::animaCapacity),
                    Codec.INT.fieldOf("fillRate").forGetter(BloodOrb::fillRate)
            ).apply(builder, BloodOrb::new)
    );
}
