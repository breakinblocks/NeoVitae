package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Tints the flask texture based on its potion contents.
 */
public class FlaskColor implements ItemColor {

    // Default tint color when no potion effects (slight blue tint)
    private static final int DEFAULT_COLOR = 0xFF385DC6;

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.hasEffects()) {
                int color = contents.getColor();
                return 0xFF000000 | color;
            }
            return DEFAULT_COLOR;
        }

        return 0xFFFFFFFF;
    }
}
