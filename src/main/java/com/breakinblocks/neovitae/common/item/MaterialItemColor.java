package com.breakinblocks.neovitae.common.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaterialItemColor implements ItemColor {

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0 && stack.getItem() instanceof MaterialItem materialItem) {
            return 0xFF000000 | materialItem.getMaterialColor();
        }
        return 0xFFFFFFFF;
    }
}
