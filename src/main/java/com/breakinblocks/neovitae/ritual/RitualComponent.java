// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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
