package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
// EnumRuneType is in the same package - no import needed

/**
 * Represents a single rune component of a ritual.
 * Each component defines a position offset from the master ritual stone
 * and the type of rune required at that position.
 */
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

    /**
     * Gets the world position of this component given a master ritual stone position.
     */
    public BlockPos getBlockPos(BlockPos masterPos) {
        return masterPos.offset(offset);
    }
}
