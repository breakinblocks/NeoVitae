package com.breakinblocks.neovitae.ritual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Datapack-defined rune layout for a ritual. Keyed in the
 * {@code neovitae:ritual_layout} registry by the ritual's id. A loaded layout
 * replaces the ritual's hardcoded {@link Ritual#gatherComponents} default and
 * drives both the activation structure check and the Modonomicon preview.
 */
public record RitualLayout(List<RitualComponent> components) {
    public static final Codec<RitualLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(RitualComponent.CODEC).fieldOf("components").forGetter(RitualLayout::components)
    ).apply(instance, RitualLayout::new));
}
