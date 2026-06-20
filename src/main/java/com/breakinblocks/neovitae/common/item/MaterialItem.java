package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.Item;

public class MaterialItem extends Item {

    private final int materialColor;
    private final boolean staticTexture;

    public MaterialItem(int materialColor) {
        this(materialColor, false);
    }

    public MaterialItem(int materialColor, boolean staticTexture) {
        super(new Item.Properties());
        this.materialColor = materialColor;
        this.staticTexture = staticTexture;
    }

    public int getMaterialColor() {
        return materialColor;
    }

    public boolean isStaticTexture() {
        return staticTexture;
    }
}
