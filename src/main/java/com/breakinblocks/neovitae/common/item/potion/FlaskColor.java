// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

import java.util.List;

/**
 * Tints the flask texture based on its effects.
 * Single-effect flasks show one solid color.
 * Multi-effect flasks split the color: bottom half shows the first effect,
 * top half shows the second effect.
 */
public class FlaskColor implements ItemColor {

    private static final int DEFAULT_COLOR = 0xFF385DC6;

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            return getBottomColor(stack);
        }
        if (layer == 1) {
            return getTopColor(stack);
        }
        return 0xFFFFFFFF;
    }

    private int getBottomColor(ItemStack stack) {
        FlaskEffects effects = stack.get(NVDataComponents.FLASK_EFFECTS.get());
        if (effects == null || effects.effects().isEmpty()) {
            return DEFAULT_COLOR;
        }
        List<EffectHolder> holders = effects.effects();
        if (holders.size() == 1) {
            return 0xFF000000 | holders.get(0).effect().value().getColor();
        }
        // Bottom half shows the first effect color
        return 0xFF000000 | holders.get(0).effect().value().getColor();
    }

    private int getTopColor(ItemStack stack) {
        FlaskEffects effects = stack.get(NVDataComponents.FLASK_EFFECTS.get());
        if (effects == null || effects.effects().isEmpty()) {
            return DEFAULT_COLOR;
        }
        List<EffectHolder> holders = effects.effects();
        if (holders.size() == 1) {
            // Single effect: top matches bottom
            return 0xFF000000 | holders.get(0).effect().value().getColor();
        }
        // Top half shows the second effect color
        return 0xFF000000 | holders.get(1).effect().value().getColor();
    }
}
