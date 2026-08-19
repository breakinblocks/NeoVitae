package com.breakinblocks.neovitae.ritual;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumFillMode implements StringRepresentable {
    SOLID,
    HOLLOW,
    FLOOR,
    WALLS,
    ROOF,
    FRAME;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String getSerializedName() {
        return name;
    }

    public String translationKey() {
        return "gui.neovitae.configurator.fill." + name;
    }

    public static EnumFillMode byName(String name, EnumFillMode fallback) {
        for (EnumFillMode mode : values()) {
            if (mode.name.equals(name)) return mode;
        }
        return fallback;
    }
}
