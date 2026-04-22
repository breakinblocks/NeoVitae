package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.Item;

public class MaterialItem extends Item {

    private final int materialColor;

    public MaterialItem(Item.Properties props, int materialColor) {
        super(props);
        this.materialColor = materialColor;
    }

    public int getMaterialColor() {
        return materialColor;
    }
}
