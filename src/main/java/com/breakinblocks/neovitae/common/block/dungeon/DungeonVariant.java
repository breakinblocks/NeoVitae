package com.breakinblocks.neovitae.common.block.dungeon;

import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

public enum DungeonVariant {
    RAW("", "raw", EnumWillType.DEFAULT),
    CORROSIVE("_c", "corrosive", EnumWillType.CORROSIVE),
    DESTRUCTIVE("_d", "destructive", EnumWillType.DESTRUCTIVE),
    STEADFAST("_st", "steadfast", EnumWillType.STEADFAST),
    VENGEFUL("_v", "vengeful", EnumWillType.VENGEFUL);

    private final String suffix;
    private final String name;
    private final EnumWillType willType;

    DungeonVariant(String suffix, String name, EnumWillType willType) {
        this.suffix = suffix;
        this.name = name;
        this.willType = willType;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getName() {
        return name;
    }

    public EnumWillType getWillType() {
        return willType;
    }

    public String getRegistryName(String baseName) {
        return baseName + suffix;
    }
}
