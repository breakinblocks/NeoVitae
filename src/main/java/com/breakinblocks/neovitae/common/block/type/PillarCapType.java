package com.breakinblocks.neovitae.common.block.type;

import net.minecraft.util.StringRepresentable;

/**
 * Enum representing pillar cap orientation (top or bottom).
 */
public enum PillarCapType implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom");

    private final String name;

    PillarCapType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
