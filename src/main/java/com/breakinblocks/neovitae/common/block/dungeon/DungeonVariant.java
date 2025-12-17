package com.breakinblocks.neovitae.common.block.dungeon;

import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Represents the demon will variants for dungeon blocks.
 * Each variant has a suffix used in registry names and an associated will type.
 */
public enum DungeonVariant {
    RAW("", "raw", EnumWillType.DEFAULT),
    CORROSIVE("_c", "corrosive", EnumWillType.CORROSIVE),
    DESTRUCTIVE("_d", "destructive", EnumWillType.DESTRUCTIVE),
    STEADFAST("_s", "steadfast", EnumWillType.STEADFAST),
    VENGEFUL("_v", "vengeful", EnumWillType.VENGEFUL);

    private final String suffix;
    private final String name;
    private final EnumWillType willType;

    DungeonVariant(String suffix, String name, EnumWillType willType) {
        this.suffix = suffix;
        this.name = name;
        this.willType = willType;
    }

    /**
     * @return The suffix to append to block registry names (e.g., "_c" for corrosive)
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @return The human-readable name of this variant
     */
    public String getName() {
        return name;
    }

    /**
     * @return The associated demon will type
     */
    public EnumWillType getWillType() {
        return willType;
    }

    /**
     * Creates a registry name with this variant's suffix.
     * @param baseName The base block name (e.g., "dungeon_brick1")
     * @return The full registry name (e.g., "dungeon_brick1_c" for corrosive)
     */
    public String getRegistryName(String baseName) {
        return baseName + suffix;
    }
}
