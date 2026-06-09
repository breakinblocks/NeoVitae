package com.breakinblocks.neovitae.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class HUDElement {
    private final int width;
    private final int height;

    public HUDElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean shouldRender(Minecraft minecraft) {
        return true;
    }

    public abstract void draw(GuiGraphics guiGraphics, float partialTicks, int drawX, int drawY);

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }
}
