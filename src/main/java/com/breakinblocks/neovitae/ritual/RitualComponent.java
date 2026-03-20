package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;

public record RitualComponent(BlockPos offset, EnumRuneType runeType) {

    public RitualComponent(int x, int y, int z, EnumRuneType runeType) {
        this(new BlockPos(x, y, z), runeType);
    }

    public int getX() {
        return offset.getX();
    }

    public int getY() {
        return offset.getY();
    }

    public int getZ() {
        return offset.getZ();
    }

    public BlockPos getBlockPos(BlockPos masterPos) {
        return masterPos.offset(offset);
    }
}
