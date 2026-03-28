package com.breakinblocks.neovitae.util.helper;

import net.minecraft.world.item.DyeColor;

public final class ColorHelper {

    private ColorHelper() {}

    public static float red(int color) {
        return ((color >> 16) & 0xFF) / 255.0f;
    }

    public static float green(int color) {
        return ((color >> 8) & 0xFF) / 255.0f;
    }

    public static float blue(int color) {
        return (color & 0xFF) / 255.0f;
    }

    public static float alpha(int color) {
        return ((color >> 24) & 0xFF) / 255.0f;
    }

    public static int fromDye(DyeColor dye) {
        return dye.getTextureDiffuseColor();
    }

    public static int pack(float r, float g, float b) {
        int ri = (int) (r * 255) & 0xFF;
        int gi = (int) (g * 255) & 0xFF;
        int bi = (int) (b * 255) & 0xFF;
        return (ri << 16) | (gi << 8) | bi;
    }

    public static int pack(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
