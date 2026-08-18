package com.breakinblocks.neovitae.common.routing;

import net.minecraft.util.StringRepresentable;

public enum RoutingTint implements StringRepresentable {
    NONE("none", -1),
    INPUT("input", 0xFF4488FF),
    OUTPUT("output", 0xFFFF8744),
    BOTH("both", 0xFFAA66FF);

    private final String name;
    private final int color;

    RoutingTint(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public static RoutingTint of(boolean pulls, boolean pushes) {
        if (pulls && pushes) return BOTH;
        if (pulls) return INPUT;
        if (pushes) return OUTPUT;
        return NONE;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
