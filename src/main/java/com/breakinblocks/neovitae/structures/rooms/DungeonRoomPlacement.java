package com.breakinblocks.neovitae.structures.rooms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.apache.commons.lang3.tuple.Pair;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.blockentity.TileDungeonSeal;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.structures.DungeonDoor;
import com.breakinblocks.neovitae.structures.DungeonRoom;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a pending dungeon room placement with all the data needed
 * to place the room into the world.
 */
public class DungeonRoomPlacement {

    public final DungeonRoom room;
    public final RandomSource rand;
    public final StructurePlaceSettings settings;
    public final ServerLevel world;
    public final BlockPos roomLocation;

    private final List<AreaDescriptor> descriptorList;
    private final List<DungeonDoor> containedDoorList;
    private final Pair<Direction, BlockPos> entrance;

    public DungeonRoomPlacement(DungeonRoom room, ServerLevel world, StructurePlaceSettings settings,
                                 BlockPos roomLocation, Pair<Direction, BlockPos> entrance) {
        this.rand = world.random;
        this.room = room;
        this.world = world;
        this.settings = settings;
        this.roomLocation = roomLocation;
        this.descriptorList = room.getAreaDescriptors(settings, roomLocation);
        this.containedDoorList = room.getPotentialConnectedRoomTypes(settings, roomLocation);
        this.entrance = entrance;
    }

    /**
     * Places the structure in the world.
     */
    public void placeStructure() {
        room.placeStructureAtPosition(rand, settings, world, roomLocation);
    }

    /**
     * Gets door positions for the given door type and direction.
     */
    public List<BlockPos> getDoorOffsetsForFacing(String doorType, Direction dir) {
        return room.getDoorOffsetsForFacing(settings, doorType, dir, roomLocation);
    }

    /**
     * Gets the area descriptors for collision checking.
     */
    public List<AreaDescriptor> getAreaDescriptors() {
        return descriptorList;
    }

    /**
     * Gets the potential room types this room can connect to.
     */
    public List<DungeonDoor> getPotentialConnectedRoomTypes() {
        return containedDoorList;
    }

    /**
     * Gets all door type keys from this room.
     */
    public Set<String> getAllRoomTypes() {
        return room.getDoorMap().keySet();
    }

    /**
     * Gets the entrance door info.
     */
    public Pair<Direction, BlockPos> getEntrance() {
        return entrance;
    }

    /**
     * Gets the room placement position.
     */
    public BlockPos getRoomPosition() {
        return roomLocation;
    }

    /**
     * Places the room in the world.
     */
    public void placeRoom(RandomSource rand, ServerLevel world) {
        room.placeStructureAtPosition(rand, settings, world, roomLocation);
    }

    /**
     * Updates the door master map with doors from this room,
     * excluding the entrance door that was just used.
     */
    public void updateDoorMasterMap(Map<String, Map<Direction, List<BlockPos>>> doorMasterMap) {
        for (Direction facing : Direction.values()) {
            Map<String, List<BlockPos>> doorTypeMap = room.getAllDoorOffsetsForFacing(settings, facing, roomLocation);
            for (Map.Entry<String, List<BlockPos>> entry : doorTypeMap.entrySet()) {
                String doorType = entry.getKey();
                List<BlockPos> doorPosList = entry.getValue();

                // Skip the entrance door
                List<BlockPos> filteredDoors = new ArrayList<>();
                for (BlockPos doorPos : doorPosList) {
                    if (entrance != null && entrance.getRight().equals(doorPos) && entrance.getLeft() == facing) {
                        continue;  // Skip entrance
                    }
                    filteredDoors.add(doorPos);
                }

                if (!filteredDoors.isEmpty()) {
                    doorMasterMap.computeIfAbsent(doorType, k -> new java.util.HashMap<>())
                            .computeIfAbsent(facing, k -> new ArrayList<>())
                            .addAll(filteredDoors);
                }
            }
        }
    }

    /**
     * Places door seals for all doors in this room (except the entrance).
     * Checks for intersection with existing rooms before placing - if a door
     * would lead into an existing room, it becomes a solid wall instead of a seal.
     */
    public void placeNewDoorSeals(ServerLevel world, BlockPos controllerPos, DungeonSynthesizer synthesizer) {
        for (DungeonDoor door : containedDoorList) {
            // Skip the entrance door
            if (entrance != null && entrance.getRight().equals(door.doorPos())) {
                continue;
            }

            // Check if this door would intersect with existing rooms
            // If so, just fill with wall - no seal (it can never lead anywhere)
            AreaDescriptor doorDesc = door.descriptor();
            boolean wouldIntersect = doorDesc != null && synthesizer.doesDescriptorIntersect(doorDesc);

            // Fill the doorway with blocks to prevent seeing void
            fillDoorway(world, door);

            if (wouldIntersect) {
                // Door would intersect existing room - just leave as solid wall, no seal
                continue;
            }

            // Place seal block above the door
            BlockPos sealPos = door.doorPos().relative(door.doorDir()).above(2);
            world.setBlockAndUpdate(sealPos, BMBlocks.DUNGEON_SEAL.block().get().defaultBlockState());

            // Configure the seal
            if (world.getBlockEntity(sealPos) instanceof TileDungeonSeal seal) {
                List<ResourceLocation> potentialRooms = new ArrayList<>();
                for (String roomType : door.getPotentialRoomTypes()) {
                    // Strip prefix characters (# for special, $ for deadend) before parsing
                    String cleanRoomType = roomType;
                    if (roomType.startsWith("#") || roomType.startsWith("$")) {
                        cleanRoomType = roomType.substring(1);
                    }
                    potentialRooms.add(ResourceLocation.parse(cleanRoomType));
                }
                seal.initialize(controllerPos, door.doorPos(), door.doorDir(), door.doorType(), potentialRooms);
            }
        }
    }

    /**
     * Fills the doorway opening with dungeon bricks to prevent seeing into the void.
     * Matches 1.20.1 implementation in DungeonSynthesizer.addNewDoorBlock
     */
    private void fillDoorway(ServerLevel world, DungeonDoor door) {
        AreaDescriptor desc = door.descriptor();
        // Calculate seal position (same as where seal will be placed)
        BlockPos sealPos = door.doorPos().relative(door.doorDir()).above(2);

        // Get all positions that need to be filled using the descriptor
        List<BlockPos> fillerList = desc.getContainedPositions(sealPos);

        // Fill each position with dungeon brick
        for (BlockPos fillerPos : fillerList) {
            world.setBlockAndUpdate(fillerPos,
                    com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
        }
    }
}
