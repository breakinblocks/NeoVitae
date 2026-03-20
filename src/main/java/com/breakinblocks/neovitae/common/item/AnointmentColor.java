package com.breakinblocks.neovitae.common.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

/**
 * Tints layer 0 (alchemic_liquid) based on the anointment's configured color.
 * Layers 1 (alchemic_vial) and 2 (alchemic_ribbon) remain untinted.
 */
public class AnointmentColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0 && stack.getItem() instanceof ItemAnointmentProvider anointmentProvider) {
            return 0xFF000000 | anointmentProvider.getColor();
        }

        return 0xFFFFFFFF;
    }
}
