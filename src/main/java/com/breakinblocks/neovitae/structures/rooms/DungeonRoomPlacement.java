// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.structures.rooms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.common.block.BlockDungeonSeal;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.DungeonSealBlockEntity;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.structures.DungeonDoor;
import com.breakinblocks.neovitae.structures.DungeonRoom;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;

import java.util.ArrayList;
import java.util.HashMap;
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
                    doorMasterMap.computeIfAbsent(doorType, k -> new HashMap<>())
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonRoomPlacement.class);

    public void placeNewDoorSeals(ServerLevel world, BlockPos controllerPos, DungeonSynthesizer synthesizer) {
        LOGGER.info("Placing seals for room {} at {}. Doors: {}, entrance: {}",
                room.getKey(), roomLocation, containedDoorList.size(),
                entrance != null ? entrance.getRight() : "none");

        for (DungeonDoor door : containedDoorList) {
            if (entrance != null && entrance.getRight().equals(door.doorPos())) {
                LOGGER.info("  Skip entrance door at {}", door.doorPos());
                continue;
            }

            AreaDescriptor doorDesc = door.descriptor();
            boolean wouldIntersect = doorDesc != null && synthesizer.doesDescriptorIntersect(doorDesc);

            LOGGER.info("  Door at {} dir={} type={} desc={} intersect={}",
                    door.doorPos(), door.doorDir(), door.doorType(),
                    doorDesc != null ? "yes" : "NULL", wouldIntersect);

            fillDoorway(world, door);

            if (wouldIntersect) {
                continue;
            }

            // Build pool list, filtering out special pools that haven't been unlocked
            BlockPos sealPos = door.doorPos().relative(door.doorDir()).above(2);
            List<ResourceLocation> unlockedSpecials = synthesizer.getSpecialRoomBuffer();
            List<ResourceLocation> potentialRooms = new ArrayList<>();
            boolean hasUnlockedSpecial = false;
            for (String roomType : door.getPotentialRoomTypes()) {
                if (roomType.startsWith("#")) {
                    ResourceLocation parsed = ResourceLocation.parse(roomType.substring(1));
                    if (unlockedSpecials.contains(parsed)) {
                        potentialRooms.add(parsed);
                        hasUnlockedSpecial = true;
                    }
                } else {
                    String cleanRoomType = roomType.startsWith("$") ? roomType.substring(1) : roomType;
                    potentialRooms.add(ResourceLocation.parse(cleanRoomType));
                }
            }

            if (potentialRooms.isEmpty()) {
                // All pools were locked special pools; wall it off
                LOGGER.info("  Walled off door at {} - all special pools locked", door.doorPos());
                continue;
            }

            world.setBlockAndUpdate(sealPos, NVBlocks.DUNGEON_SEAL.block().get().defaultBlockState()
                    .setValue(BlockDungeonSeal.SPECIAL, hasUnlockedSpecial));
            synthesizer.incrementSealCount();

            if (world.getBlockEntity(sealPos) instanceof DungeonSealBlockEntity seal) {
                seal.initialize(controllerPos, door.doorPos(), door.doorDir(), door.doorType(), potentialRooms);
            }

            if (hasUnlockedSpecial) {
                spawnSpecialSealLights(world, sealPos);
            }
        }
    }

    private void spawnSpecialSealLights(ServerLevel world, BlockPos sealPos) {
        RandomSource rand = world.getRandom();
        BlockState lightState = NVBlocks.BLOOD_LIGHT.get().defaultBlockState();
        int count = 3 + rand.nextInt(4);
        for (int i = 0; i < count; i++) {
            for (int attempt = 0; attempt < 10; attempt++) {
                BlockPos lightPos = sealPos.offset(rand.nextInt(7) - 3, rand.nextInt(5) - 2, rand.nextInt(7) - 3);
                if (world.isEmptyBlock(lightPos)) {
                    world.setBlockAndUpdate(lightPos, lightState);
                    if (world.getBlockEntity(lightPos) instanceof BloodLightBlockEntity lightBE) {
                        lightBE.setColor(DyeColor.CYAN);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Fills the doorway opening with dungeon bricks to prevent seeing into the void.
     * Matches 1.20.1 implementation in DungeonSynthesizer.addNewDoorBlock
     */
    private void fillDoorway(ServerLevel world, DungeonDoor door) {
        AreaDescriptor desc = door.descriptor();
        BlockPos sealPos = door.doorPos().relative(door.doorDir()).above(2);

        if (desc != null) {
            List<BlockPos> fillerList = desc.getContainedPositions(sealPos);
            for (BlockPos fillerPos : fillerList) {
                world.setBlockAndUpdate(fillerPos,
                        DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
            }
        } else {
            Direction rightDir = door.doorDir().getClockWise();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    BlockPos fillerPos = sealPos.relative(rightDir, i).relative(Direction.UP, j);
                    world.setBlockAndUpdate(fillerPos,
                            DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
                }
            }
        }
    }
}
