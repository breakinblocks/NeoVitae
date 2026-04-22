package com.breakinblocks.neovitae.common.item;

import com.breakinblocks.neovitae.NeoVitae;
import com.klikli_dev.modonomicon.item.ModonomiconCustomItemBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class NVGuideBookItem extends ModonomiconCustomItemBase {
    public NVGuideBookItem(Item.Properties props) {
        super(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "guide"), props.stacksTo(1));
    }
}
