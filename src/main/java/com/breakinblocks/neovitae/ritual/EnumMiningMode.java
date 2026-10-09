package com.breakinblocks.neovitae.ritual;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumMiningMode implements StringRepresentable {
    SILK_TOUCH,
    FORTUNE;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String getSerializedName() {
        return name;
    }

    public String translationKey() {
        return "gui.neovitae.configurator.mining." + name;
    }

    public static EnumMiningMode byName(String name, EnumMiningMode fallback) {
        for (EnumMiningMode mode : values()) {
            if (mode.name.equals(name)) return mode;
        }
        return fallback;
    }
}
