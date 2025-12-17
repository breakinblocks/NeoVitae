package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Item color handler for alchemy flasks.
 * Tints the flask texture based on its potion contents.
 *
 * Since the flask only has one layer, we tint the entire flask
 * with the potion color (or default color if no potion).
 */
public class FlaskColor implements ItemColor {

    // Default tint color when no potion effects (slight blue tint)
    private static final int DEFAULT_COLOR = 0xFF385DC6;

    @Override
    public int getColor(ItemStack stack, int layer) {
        // Only tint layer 0 (the only layer)
        if (layer == 0) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.hasEffects()) {
                // Return the potion color with full alpha
                int color = contents.getColor();
                return 0xFF000000 | color;
            }
            return DEFAULT_COLOR;
        }

        // Any other layers - no tint (white with full alpha)
        return 0xFFFFFFFF;
    }
}
