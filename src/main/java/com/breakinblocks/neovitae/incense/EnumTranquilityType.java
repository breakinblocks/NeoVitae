// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2020 Arcaratus <https://github.com/Arcaratus>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.incense;

/**
 * Types of tranquility that blocks can provide.
 * Each type contributes to the overall tranquility value around an Incense Altar.
 */
public enum EnumTranquilityType {
    PLANT,
    CROP,
    TREE,
    EARTHEN,
    WATER,
    FIRE,
    LAVA;

    public static EnumTranquilityType getType(String type) {
        for (EnumTranquilityType t : values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }
}
