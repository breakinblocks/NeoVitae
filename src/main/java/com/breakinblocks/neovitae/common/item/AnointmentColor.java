package com.breakinblocks.neovitae.common.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

/**
 * Item color handler for anointment items.
 * Tints layer 0 (alchemic_liquid) based on the anointment's configured color.
 * Layers 1 (alchemic_vial) and 2 (alchemic_ribbon) remain untinted.
 */
public class AnointmentColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int layer) {
        // Only tint layer 0 (the liquid layer)
        if (layer == 0 && stack.getItem() instanceof ItemAnointmentProvider anointmentProvider) {
            // Add full alpha to the color (colors are stored as RGB without alpha)
            return 0xFF000000 | anointmentProvider.getColor();
        }

        // Any other layers - no tint (white with full alpha)
        return 0xFFFFFFFF;
    }
}
