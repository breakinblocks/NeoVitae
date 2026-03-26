package com.breakinblocks.neovitae.compat.jei.bloodtank;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public class BloodTankSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final BloodTankSubtypeInterpreter INSTANCE = new BloodTankSubtypeInterpreter();

    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        int tier = ingredient.getOrDefault(NVDataComponents.CONTAINER_TIER, 0);
        return String.valueOf(tier);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return String.valueOf(ingredient.getOrDefault(NVDataComponents.CONTAINER_TIER, 0));
    }
}
