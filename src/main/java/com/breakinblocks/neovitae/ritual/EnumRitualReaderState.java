package com.breakinblocks.neovitae.ritual;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * States for the Ritual Reader item when configuring ritual areas.
 */
public enum EnumRitualReaderState implements StringRepresentable {
    /**
     * Display information about the ritual
     */
    INFORMATION,
    /**
     * Set the first corner of an area
     */
    SET_AREA_CORNER_1,
    /**
     * Set the second corner of an area
     */
    SET_AREA_CORNER_2,
    /**
     * Set the current demon will type configuration
     */
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
