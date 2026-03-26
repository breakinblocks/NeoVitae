package com.breakinblocks.neovitae.common.block.dungeon;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public enum DungeonVariant {
    RAW("", "raw", SpiritusType.DEFAULT),
    CORROSIVE("_c", "corrosive", SpiritusType.CORROSIVE),
    DESTRUCTIVE("_d", "destructive", SpiritusType.DESTRUCTIVE),
    STEADFAST("_st", "steadfast", SpiritusType.STEADFAST),
    VENGEFUL("_v", "vengeful", SpiritusType.VENGEFUL);

    private final String suffix;
    private final String name;
    private final SpiritusType willType;

    DungeonVariant(String suffix, String name, SpiritusType willType) {
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

    public SpiritusType getWillType() {
        return willType;
    }

    public String getRegistryName(String baseName) {
        return baseName + suffix;
    }
}
