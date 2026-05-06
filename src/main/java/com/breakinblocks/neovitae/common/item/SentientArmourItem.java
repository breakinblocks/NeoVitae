package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ArmorItem;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;

public class SentientArmourItem extends ArmorItem implements UpgradeHolderBase {

    public SentientArmourItem() {
        super(
                NVMaterialsAndTiers.SENTIENT_ARMOUR_MATERIAL,
                Type.CHESTPLATE,
                new Properties()
                        .durability(Type.CHESTPLATE.getDurability(33))
                        .component(NVDataComponents.REQUIRED_SET, NVTags.Items.SENTIENT_SET)
                        .component(NVDataComponents.CURRENT_UPGRADE_POINTS, 0)
                );
    }
}
