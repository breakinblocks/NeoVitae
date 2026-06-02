package com.breakinblocks.neovitae.compat.jei.tome;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;

public class UpgradeTomeSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final UpgradeTomeSubtypeInterpreter INSTANCE = new UpgradeTomeSubtypeInterpreter();

    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        UpgradeTome tome = ingredient.get(NVDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return "";
        }
        return tome.upgrade().unwrapKey().map(key -> key.location().toString()).orElse("unknown");
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return getSubtypeData(ingredient, context).toString();
    }
}
