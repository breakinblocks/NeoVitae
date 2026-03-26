package com.breakinblocks.neovitae.common.item;

import com.breakinblocks.neovitae.NeoVitae;
import com.klikli_dev.modonomicon.item.ModonomiconCustomItemBase;
import net.minecraft.resources.ResourceLocation;

public class NVGuideBookItem extends ModonomiconCustomItemBase {
    public NVGuideBookItem() {
        super(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "guide"), new Properties().stacksTo(1));
    }
}
