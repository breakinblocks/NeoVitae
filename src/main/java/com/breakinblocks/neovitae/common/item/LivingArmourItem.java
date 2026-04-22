package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;

public class LivingArmourItem extends Item implements UpgradeHolderBase {

    public LivingArmourItem(Item.Properties props) {
        super(props
                .humanoidArmor(NVMaterialsAndTiers.LIVING_ARMOUR_MATERIAL, ArmorType.CHESTPLATE)
                .component(NVDataComponents.REQUIRED_SET, NVTags.Items.LIVING_SET)
                .component(NVDataComponents.CURRENT_UPGRADE_POINTS, 0));
    }
}
