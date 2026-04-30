package com.breakinblocks.neovitae.common.block.dungeon;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public enum DungeonVariant {
    RAW("", "raw", SpiritusType.RAW),
    RUINA("_c", "corrosive", SpiritusType.RUINA),
    NIHILUM("_d", "destructive", SpiritusType.NIHILUM),
    INVICTUS("_st", "steadfast", SpiritusType.INVICTUS),
    VINDICTA("_v", "vengeful", SpiritusType.VINDICTA);

    private final String suffix;
    private final String name;
    private final SpiritusType spiritusType;

    DungeonVariant(String suffix, String name, SpiritusType spiritusType) {
        this.suffix = suffix;
        this.name = name;
        this.spiritusType = spiritusType;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getName() {
        return name;
    }

    public SpiritusType getWillType() {
        return spiritusType;
    }

    public String getRegistryName(String baseName) {
        return baseName + suffix;
    }
}
