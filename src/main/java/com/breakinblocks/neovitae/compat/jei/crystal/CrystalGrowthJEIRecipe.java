package com.breakinblocks.neovitae.compat.jei.crystal;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import net.minecraft.world.item.ItemStack;

public record CrystalGrowthJEIRecipe(
        SpiritusType type,
        ItemStack cluster,
        ItemStack shard,
        double spiritusToForm,
        int formationTicks,
        double spiritusPerSegment,
        int maxSegments,
        int harvestSpiritus
) {
}
