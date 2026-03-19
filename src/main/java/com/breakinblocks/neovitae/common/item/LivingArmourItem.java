package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ArmorItem;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;

public class LivingArmourItem extends ArmorItem implements UpgradeHolderBase {

    public LivingArmourItem() {
        super(
                NVMaterialsAndTiers.LIVING_ARMOUR_MATERIAL,
                Type.CHESTPLATE,
                new Properties()
                        .durability(Type.CHESTPLATE.getDurability(33))
                        .component(NVDataComponents.REQUIRED_SET, NVTags.Items.LIVING_SET)
                        .component(NVDataComponents.CURRENT_UPGRADE_POINTS, 0)
                );
    }
}
