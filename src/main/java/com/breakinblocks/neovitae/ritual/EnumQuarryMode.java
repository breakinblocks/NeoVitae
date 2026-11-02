package com.breakinblocks.neovitae.ritual;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumQuarryMode implements StringRepresentable {
    BACKFILL,
    LEAVE_HOLES,
    CLEAR_ROCK;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String getSerializedName() {
        return name;
    }

    public String translationKey() {
        return "gui.neovitae.configurator.quarry." + name;
    }

    public static EnumQuarryMode byName(String name, EnumQuarryMode fallback) {
        for (EnumQuarryMode mode : values()) {
            if (mode.name.equals(name)) return mode;
        }
        return fallback;
    }
}
