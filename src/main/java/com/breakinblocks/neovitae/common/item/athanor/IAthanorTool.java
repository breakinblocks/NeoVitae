package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public interface IAthanorTool {
    default double getCraftingSpeedMultiplier(ItemStack stack) {
        return 1;
    }

    default double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return 1;
    }

    default SpiritusType getDominantSpiritusType(ItemStack stack) {
        return SpiritusType.RAW;
    }
}
