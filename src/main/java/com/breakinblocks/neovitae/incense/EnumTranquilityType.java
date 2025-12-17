package com.breakinblocks.neovitae.incense;

/**
 * Types of tranquility that blocks can provide.
 * Each type contributes to the overall tranquility value around an Incense Altar.
 */
public enum EnumTranquilityType {
    PLANT,
    CROP,
    TREE,
    EARTHEN,
    WATER,
    FIRE,
    LAVA;

    public static EnumTranquilityType getType(String type) {
        for (EnumTranquilityType t : values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }
}
