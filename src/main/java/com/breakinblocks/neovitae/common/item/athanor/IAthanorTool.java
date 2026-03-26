package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

public interface IAthanorTool {
    default double getCraftingSpeedMultiplier(ItemStack stack) {
        return 1;
    }

    default double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return 1;
    }

    default EnumWillType getDominantWillType(ItemStack stack) {
        return EnumWillType.DEFAULT;
    }
}
