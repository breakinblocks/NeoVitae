package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;

public class SentientArmourItem extends Item implements UpgradeHolderBase {

    public SentientArmourItem(Item.Properties props) {
        super(props
                .humanoidArmor(NVMaterialsAndTiers.SENTIENT_ARMOUR_MATERIAL, ArmorType.CHESTPLATE)
                .component(NVDataComponents.REQUIRED_SET, NVTags.Items.SENTIENT_SET)
                .component(NVDataComponents.CURRENT_UPGRADE_POINTS, 0));
    }
}
