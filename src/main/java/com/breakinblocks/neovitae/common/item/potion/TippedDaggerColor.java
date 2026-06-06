// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Layer 0: dagger texture (untinted), Layer 1: potion overlay (tinted with potion color).
 */
public class TippedDaggerColor implements ItemColor {

    // Default water color when no effects (with full alpha)
    private static final int DEFAULT_POTION_COLOR = 0xFF385DC6;

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 1) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.hasEffects()) {
                return 0xFF000000 | contents.getColor();
            }
            return DEFAULT_POTION_COLOR;
        }

        return 0xFFFFFFFF;
    }
}
