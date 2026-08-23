// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record RitualComponent(BlockPos offset, EnumRuneType runeType) {

    public static final Codec<EnumRuneType> RUNE_TYPE_CODEC = Codec.STRING.comapFlatMap(
            RitualComponent::parseRuneType, EnumRuneType::getSerializedName);

    public static final Codec<RitualComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(RitualComponent::offset),
            RUNE_TYPE_CODEC.fieldOf("rune").forGetter(RitualComponent::runeType)
    ).apply(instance, RitualComponent::new));

    private static DataResult<EnumRuneType> parseRuneType(String name) {
        String resolved = switch (name) {
            case "dusk" -> "tenebrae";
            case "dawn" -> "deus";
            default -> name;
        };
        for (EnumRuneType type : EnumRuneType.values()) {
            if (type.getSerializedName().equals(resolved)) return DataResult.success(type);
        }
        return DataResult.error(() -> "Unknown rune type: " + name);
    }

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
