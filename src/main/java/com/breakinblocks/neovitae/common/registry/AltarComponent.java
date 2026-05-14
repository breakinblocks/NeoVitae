package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;
import net.minecraft.util.ExtraCodecs;

public record AltarComponent(BlockPos pos, TagOrElementLocation material, boolean isUpgrade, boolean optional) {
    public static final Codec<AltarComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(AltarComponent::pos),
            ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("valid").forGetter(AltarComponent::material),
            Codec.BOOL.fieldOf("upgrade").forGetter(AltarComponent::isUpgrade),
            Codec.BOOL.optionalFieldOf("optional", false).forGetter(AltarComponent::optional)
    ).apply(instance, AltarComponent::new));

    public AltarComponent(BlockPos pos, TagOrElementLocation material, boolean isUpgrade) {
        this(pos, material, isUpgrade, false);
    }
}
