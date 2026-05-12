package com.breakinblocks.neovitae.ritual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;

public record RitualComponent(BlockPos offset, EnumRuneType runeType) {

    public static final Codec<EnumRuneType> RUNE_TYPE_CODEC = StringRepresentable.fromEnum(EnumRuneType::values);

    public static final Codec<RitualComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(RitualComponent::offset),
            RUNE_TYPE_CODEC.fieldOf("rune").forGetter(RitualComponent::runeType)
    ).apply(instance, RitualComponent::new));

    public RitualComponent(int x, int y, int z, EnumRuneType runeType) {
        this(new BlockPos(x, y, z), runeType);
    }

    public int getX() {
        return offset.getX();
    }

    public int getY() {
        return offset.getY();
    }

    public int getZ() {
        return offset.getZ();
    }

    public BlockPos getBlockPos(BlockPos masterPos) {
        return masterPos.offset(offset);
    }
}
