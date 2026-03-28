package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.Item;

public class MaterialItem extends Item {

    private final int materialColor;

    public MaterialItem(int materialColor) {
        super(new Item.Properties());
        this.materialColor = materialColor;
    }

    public int getMaterialColor() {
        return materialColor;
    }
}
