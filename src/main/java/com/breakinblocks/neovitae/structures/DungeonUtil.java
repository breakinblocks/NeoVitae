package com.breakinblocks.neovitae.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for dungeon-related calculations,
 * particularly rotation and mirroring transformations.
 */
public final class DungeonUtil {

    private DungeonUtil() {}

    /**
     * Rotates a direction by applying mirror first, then rotation.
     */
    public static Direction rotate(Mirror mirror, Rotation rotation, Direction original) {
        return rotation.rotate(mirror.mirror(original));
    }

    /**
     * Reverses a rotation transformation to get the original direction.
     */
    public static Direction reverseRotate(Mirror mirror, Rotation rotation, Direction original) {
        return mirror.mirror(getOppositeRotation(rotation).rotate(original));
    }

    /**
     * Gets the facing direction after applying the structure placement settings.
     */
    public static Direction getFacingForSettings(StructurePlaceSettings settings, Direction original) {
        return rotate(settings.getMirror(), settings.getRotation(), original);
    }

    /**
     * Gets the opposite rotation (for reversing transformations).
     */
    public static Rotation getOppositeRotation(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            default -> rotation;
        };
    }

    /**
     * Adds a door position to the door map for the given facing direction.
     */
    public static void addRoom(Map<Direction, List<BlockPos>> doorMap, Direction facing, BlockPos offsetPos) {
        doorMap.computeIfAbsent(facing, k -> new ArrayList<>()).add(offsetPos);
    }

    /**
     * Gets the rotation needed to rotate from NORTH to the target direction.
     */
    public static Rotation getRotationFromNorth(Direction target) {
        return switch (target) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
}
