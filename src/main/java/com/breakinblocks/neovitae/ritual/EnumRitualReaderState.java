package com.breakinblocks.neovitae.ritual;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumRitualReaderState implements StringRepresentable {
    INFORMATION,
    SET_AREA_CORNER_1,
    SET_AREA_CORNER_2,
    SET_WILL_CONFIG;

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public EnumRitualReaderState next() {
        int nextOrdinal = (ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}
