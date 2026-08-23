// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.ChatFormatting;

public enum EnumRuneType implements StringRepresentable {
    BLANK(ChatFormatting.GRAY),
    WATER(ChatFormatting.AQUA),
    FIRE(ChatFormatting.RED),
    EARTH(ChatFormatting.GREEN),
    AIR(ChatFormatting.WHITE),
    TENEBRAE(ChatFormatting.DARK_GRAY),
    DEUS(ChatFormatting.GOLD);

    public final ChatFormatting colorCode;
    public final String translationKey = this.name().toLowerCase(Locale.ROOT) + "Rune";
    public final String bookColor = "$(" + this.name().toLowerCase(Locale.ROOT) + ")";

    EnumRuneType(ChatFormatting colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return this.toString();
    }

    public static EnumRuneType byMetadata(int meta) {
        if (meta < 0 || meta >= values().length)
            meta = 0;
        return values()[meta];
    }
}
