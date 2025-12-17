package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.core.BlockPos;

/**
 * Represents a single rune component of a ritual.
 * Each component defines a position offset from the master ritual stone
 * and the type of rune required at that position.
 *
 * @param offset   The position offset from the master ritual stone
 * @param runeType The type of rune required at this position
 */
public record RitualComponent(BlockPos offset, EnumRuneType runeType) {

    /**
     * Creates a ritual component with explicit coordinates.
     *
     * @param x        X offset from master ritual stone
     * @param y        Y offset from master ritual stone
     * @param z        Z offset from master ritual stone
     * @param runeType The type of rune required
     */
    public RitualComponent(int x, int y, int z, EnumRuneType runeType) {
        this(new BlockPos(x, y, z), runeType);
    }

    /**
     * Gets the X offset.
     */
    public int getX() {
        return offset.getX();
    }

    /**
     * Gets the Y offset.
     */
    public int getY() {
        return offset.getY();
    }

    /**
     * Gets the Z offset.
     */
    public int getZ() {
        return offset.getZ();
    }

    /**
     * Gets the world position of this component given a master ritual stone position.
     *
     * @param masterPos The position of the master ritual stone
     * @return The world position of this rune
     */
    public BlockPos getBlockPos(BlockPos masterPos) {
        return masterPos.offset(offset);
    }
}
