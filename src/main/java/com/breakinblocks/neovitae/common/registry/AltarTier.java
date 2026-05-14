package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AltarTier(int tier, List<AltarComponent> components, List<AltarEffect> effects) implements Comparable<AltarTier> {
    public static final Codec<AltarTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("tier").forGetter(AltarTier::tier),
            Codec.list(AltarComponent.CODEC).fieldOf("components").forGetter(AltarTier::components),
            Codec.list(AltarEffect.CODEC).optionalFieldOf("effects", List.of()).forGetter(AltarTier::effects)
    ).apply(instance, AltarTier::new));

    public AltarTier(int tier, List<AltarComponent> components) {
        this(tier, components, List.of());
    }

    @Override
    public int compareTo(@NotNull AltarTier altarTier) {
        return Integer.compare(this.tier, altarTier.tier);
    }
}
