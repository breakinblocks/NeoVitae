// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * Types of ritual runes used in ritual construction.
 * Each type has an associated color for visual identification.
 */
public enum EnumRuneType implements StringRepresentable {
    /** Basic rune, no elemental affinity */
    BLANK(ChatFormatting.GRAY),
    /** Water elemental rune */
    WATER(ChatFormatting.AQUA),
    /** Fire elemental rune */
    FIRE(ChatFormatting.RED),
    /** Earth elemental rune */
    EARTH(ChatFormatting.GREEN),
    /** Air elemental rune */
    AIR(ChatFormatting.WHITE),
    /** Tenebrae rune for advanced rituals */
    TENEBRAE(ChatFormatting.DARK_GRAY),
    /** Deus rune for the most powerful rituals */
    DEUS(ChatFormatting.GOLD);

    /** The chat formatting color for this rune type */
    public final ChatFormatting colorCode;
    /** Translation key for this rune type */
    public final String translationKey;
    /** Book formatting color reference */
    public final String bookColor;

    EnumRuneType(ChatFormatting colorCode) {
        this.colorCode = colorCode;
        this.translationKey = this.name().toLowerCase(Locale.ROOT) + "Rune";
        this.bookColor = "$(" + this.name().toLowerCase(Locale.ROOT) + ")";
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return this.toString();
    }

    /**
     * Gets a rune type by its ordinal value.
     *
     * @param meta The ordinal value
     * @return The rune type, or BLANK if invalid
     */
    public static EnumRuneType byMetadata(int meta) {
        if (meta < 0 || meta >= values().length)
            meta = 0;
        return values()[meta];
    }
}
