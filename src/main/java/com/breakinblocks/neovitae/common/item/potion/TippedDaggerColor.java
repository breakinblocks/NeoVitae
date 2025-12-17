package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

/**
 * Item color handler for tipped throwing daggers.
 * The model has 2 layers:
 * - Layer 0: dagger texture (untinted)
 * - Layer 1: potion overlay (tinted with potion color)
 */
public class TippedDaggerColor implements ItemColor {

    // Default water color when no effects (with full alpha)
    private static final int DEFAULT_POTION_COLOR = 0xFF385DC6;

    @Override
    public int getColor(ItemStack stack, int layer) {
        // Only tint layer 1 (the potion overlay)
        if (layer == 1) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.hasEffects()) {
                // Return potion color with full alpha
                return 0xFF000000 | contents.getColor();
            }
            // Return water color if no effects
            return DEFAULT_POTION_COLOR;
        }

        // Layer 0 and other layers - no tint (white with full alpha)
        return 0xFFFFFFFF;
    }
}
