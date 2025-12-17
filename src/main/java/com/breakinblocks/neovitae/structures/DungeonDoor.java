package com.breakinblocks.neovitae.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a door in a dungeon room that can connect to other rooms.
 * Doors store information about their position, facing direction, type,
 * and what room pools they can connect to.
 *
 * Implemented as a record for immutability and modern Java patterns.
 */
public record DungeonDoor(
        BlockPos doorPos,
        Direction doorDir,
        String doorType,
        List<String> roomList,
        AreaDescriptor descriptor
) {
    /**
     * Compact constructor to ensure roomList is immutable.
     */
    public DungeonDoor {
        roomList = List.copyOf(roomList);
    }

    /**
     * Gets all potential room type strings (raw form, includes prefixes).
     */
    public List<String> getPotentialRoomTypes() {
        return roomList;
    }

    /**
     * Gets the list of normal room pools this door can connect to.
     * Normal rooms are listed without any prefix.
     */
    public List<ResourceLocation> getRoomList() {
        List<ResourceLocation> rlRoomList = new ArrayList<>();
        for (String room : roomList) {
            if (!room.startsWith("#") && !room.startsWith("$")) {
                rlRoomList.add(ResourceLocation.parse(room));
            }
        }
        return rlRoomList;
    }

    /**
     * Gets the list of special room pools this door can connect to.
     * Special rooms are prefixed with '#'.
     */
    public List<ResourceLocation> getSpecialRoomList() {
        List<ResourceLocation> rlRoomList = new ArrayList<>();
        for (String room : roomList) {
            if (room.startsWith("#")) {
                String[] splitString = room.split("#");
                if (splitString.length > 1) {
                    rlRoomList.add(ResourceLocation.parse(splitString[1]));
                }
            }
        }
        return rlRoomList;
    }

    /**
     * Gets the list of dead-end room pools this door can connect to.
     * Dead-end rooms are prefixed with '$'.
     * Returns the default dead-end pool if none are specified.
     */
    public List<ResourceLocation> getDeadendRoomList() {
        List<ResourceLocation> rlRoomList = new ArrayList<>();
        for (String room : roomList) {
            if (room.startsWith("$")) {
                String[] splitString = room.split("\\$");
                if (splitString.length > 1) {
                    rlRoomList.add(ResourceLocation.parse(splitString[1]));
                }
            }
        }

        if (rlRoomList.isEmpty()) {
            rlRoomList.add(ModRoomPools.DEFAULT_DEADEND);
        }

        return rlRoomList;
    }

    /**
     * Determines if this door should be a dead-end based on the current depth.
     *
     * @param roomDepth    Current depth in the dungeon
     * @param maxRoomDepth Maximum depth allowed
     * @return true if this should be a dead-end
     */
    public boolean isDeadend(int roomDepth, int maxRoomDepth) {
        return roomDepth < maxRoomDepth - 1;
    }

    @Override
    public String toString() {
        return "DungeonDoor{pos=" + doorPos + ", dir=" + doorDir + ", type=" + doorType + "}";
    }
}
