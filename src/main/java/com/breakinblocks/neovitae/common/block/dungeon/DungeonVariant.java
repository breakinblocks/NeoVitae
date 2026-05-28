package com.breakinblocks.neovitae.common.block.dungeon;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public enum DungeonVariant {
    RAW("", "raw", SpiritusType.RAW),
    RUINA("_c", "ruina", SpiritusType.RUINA),
    NIHILUM("_d", "nihilum", SpiritusType.NIHILUM),
    INVICTUS("_st", "invictus", SpiritusType.INVICTUS),
    VINDICTA("_v", "vindicta", SpiritusType.VINDICTA);

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

    public SpiritusType getSpiritusType() {
        return spiritusType;
    }

    public String getRegistryName(String baseName) {
        return baseName + suffix;
    }
}
