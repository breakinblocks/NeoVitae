// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2020 Arcaratus <https://github.com/Arcaratus>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.incense;

/**
 * Holds the tranquility type and value for valid tranquility modifiers.
 */
public class TranquilityStack {
    public final EnumTranquilityType type;
    public double value;

    public TranquilityStack(EnumTranquilityType type, double value) {
        this.type = type;
        this.value = value;
    }
}
