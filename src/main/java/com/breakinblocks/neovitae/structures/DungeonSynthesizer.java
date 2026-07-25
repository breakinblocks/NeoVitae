// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.blockentity.DungeonSealBlockEntity;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.structures.rooms.DungeonRoomPlacement;

import java.util.*;
import java.util.Map.Entry;

/**
 * The DungeonSynthesizer handles procedural dungeon generation.
 * It tracks available doors, placed rooms, and coordinates room placement.
 * Uses Codec-based serialization for modern NeoForge patterns.
 */
public class DungeonSynthesizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonSynthesizer.class);

    public static boolean displayDetailedInformation = false;

    /**
     * Codec for serializing door position lists by direction.
     */
    private static final Codec<Map<Direction, List<BlockPos>>> DIRECTION_DOOR_MAP_CODEC =
            Codec.unboundedMap(Direction.CODEC, BlockPos.CODEC.listOf());

    /**
     * Codec for the full door master map.
     */
    private static final Codec<Map<String, Map<Direction, List<BlockPos>>>> DOOR_MASTER_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, DIRECTION_DOOR_MAP_CODEC);

    /**
     * Codec for room placement tracking.
     */
    private static final Codec<Map<ResourceLocation, Integer>> PLACEMENT_TRACKER_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);

    // Map of door types to their available doors by direction
    private Map<String, Map<Direction, List<BlockPos>>> availableDoorMasterMap = new HashMap<>();

    // List of area descriptors for collision detection
    private List<AreaDescriptor> descriptorList = new ArrayList<>();

    // Room progression tracking
    private int activatedDoors = 0;
    private int activeSealCount = 0;
    private List<ResourceLocation> specialRoomBuffer = new ArrayList<>();
    private Map<ResourceLocation, Integer> placementsSinceLastSpecial = new HashMap<>();
    private final java.util.ArrayDeque<BlockPos> pendingValidation = new java.util.ArrayDeque<>();

    /**
     * Gets the available door master map.
     */
    public Map<String, Map<Direction, List<BlockPos>>> getAvailableDoorMasterMap() {
        return availableDoorMasterMap;
    }

    /**
     * Gets the area descriptor list.
     */
    public List<AreaDescriptor> getDescriptorList() {
        return descriptorList;
    }

    /**
     * Checks if an area descriptor is within world build height limits.
     */
    public boolean isAreaDescriptorInBounds(Level level, AreaDescriptor desc) {
        if (desc instanceof AreaDescriptor.Rectangle rect) {
            BlockPos maxOffset = rect.getMaximumOffset();
            BlockPos minOffset = rect.getMinimumOffset();
            return maxOffset.getY() < level.getMaxBuildHeight() && minOffset.getY() >= level.getMinBuildHeight();
        }
        return true;
    }

    /**
     * Saves synthesizer state to NBT using Codecs.
     */
    public void writeToNBT(CompoundTag tag) {
        // Serialize door master map using Codec
        DOOR_MASTER_MAP_CODEC.encodeStart(NbtOps.INSTANCE, availableDoorMasterMap)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("doorMasterMap", nbt));

        // Serialize area descriptors
        ListTag descriptorNbt = new ListTag();
        for (AreaDescriptor desc : descriptorList) {
            CompoundTag compoundnbt = new CompoundTag();
            desc.writeToNBT(compoundnbt);
            descriptorNbt.add(compoundnbt);
        }
        if (!descriptorNbt.isEmpty()) {
            tag.put("areaDescriptors", descriptorNbt);
        }

        // Serialize special room buffer using Codec
        ResourceLocation.CODEC.listOf().encodeStart(NbtOps.INSTANCE, specialRoomBuffer)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("specialRoomBuffer", nbt));

        // Serialize placements since last special using Codec
        PLACEMENT_TRACKER_CODEC.encodeStart(NbtOps.INSTANCE, placementsSinceLastSpecial)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("placementTracker", nbt));

        tag.putInt("activatedDoors", activatedDoors);
        tag.putInt("activeSealCount", activeSealCount);

        net.minecraft.nbt.ListTag validationQueue = new net.minecraft.nbt.ListTag();
        for (BlockPos pos : pendingValidation) {
            net.minecraft.nbt.CompoundTag posTag = new net.minecraft.nbt.CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            validationQueue.add(posTag);
        }
        if (!validationQueue.isEmpty()) tag.put("pendingValidation", validationQueue);
    }

    /**
     * Loads synthesizer state from NBT using Codecs.
     */
    public void readFromNBT(CompoundTag tag) {
        // Deserialize door master map using Codec
        if (tag.contains("doorMasterMap")) {
            Map<String, Map<Direction, List<BlockPos>>> parsed = DOOR_MASTER_MAP_CODEC.parse(NbtOps.INSTANCE, tag.get("doorMasterMap"))
                    .resultOrPartial(LOGGER::error)
                    .orElse(new HashMap<>());
            availableDoorMasterMap = new HashMap<>();
            for (var entry : parsed.entrySet()) {
                HashMap<Direction, List<BlockPos>> innerMap = new HashMap<>();
                for (var inner : entry.getValue().entrySet()) {
                    innerMap.put(inner.getKey(), new ArrayList<>(inner.getValue()));
                }
                availableDoorMasterMap.put(entry.getKey(), innerMap);
            }
        }

        // Deserialize area descriptors
        ListTag descriptorNbt = tag.getList("areaDescriptors", 10);
        descriptorList.clear();
        for (int i = 0; i < descriptorNbt.size(); i++) {
            CompoundTag compoundnbt = descriptorNbt.getCompound(i);
            AreaDescriptor.Rectangle rec = new AreaDescriptor.Rectangle(BlockPos.ZERO, 1, 1, 1);
            rec.readFromNBT(compoundnbt);
            descriptorList.add(rec);
        }

        // Deserialize special room buffer using Codec
        if (tag.contains("specialRoomBuffer")) {
            specialRoomBuffer = ResourceLocation.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("specialRoomBuffer"))
                    .resultOrPartial(LOGGER::error)
                    .map(ArrayList::new)
                    .orElse(new ArrayList<>());
        }

        // Deserialize placements since last special using Codec
        if (tag.contains("placementTracker")) {
            placementsSinceLastSpecial = PLACEMENT_TRACKER_CODEC.parse(NbtOps.INSTANCE, tag.get("placementTracker"))
                    .resultOrPartial(LOGGER::error)
                    .map(HashMap::new)
                    .orElse(new HashMap<>());
        }

        activatedDoors = tag.getInt("activatedDoors");
        activeSealCount = tag.getInt("activeSealCount");

        pendingValidation.clear();
        if (tag.contains("pendingValidation")) {
            net.minecraft.nbt.ListTag validationQueue = tag.getList("pendingValidation", 10);
            for (int i = 0; i < validationQueue.size(); i++) {
                net.minecraft.nbt.CompoundTag posTag = validationQueue.getCompound(i);
                pendingValidation.add(new BlockPos(posTag.getInt("X"), posTag.getInt("Y"), posTag.getInt("Z")));
            }
        }
    }

    /**
     * Generates the initial room of a dungeon at the specified position.
     *
     * @param initialType      The room pool to select the initial room from
     * @param rand             Random source
     * @param world            The server world
     * @param spawningPosition The position for the dungeon controller
     * @return Array of [playerSpawnPos, portalPos]
     */
    public BlockPos[] generateInitialRoom(ResourceLocation initialType, RandomSource rand,
                                          ServerLevel world, BlockPos spawningPosition) {
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setMirror(Mirror.NONE);
        settings.setRotation(Rotation.NONE);
        settings.setIgnoreEntities(true);
        settings.setKnownShape(true);

        DungeonRoom initialRoom = DungeonRoomRegistry.getRandomDungeonRoom(initialType, rand);
        if (initialRoom == null) {
            LOGGER.warn("Could not find initial room from pool: {}", initialType);
            return new BlockPos[] { spawningPosition.above(), spawningPosition };
        }

        BlockPos roomPlacementPosition = initialRoom.getInitialSpawnOffsetForControllerPos(settings, spawningPosition);

        // Add area descriptors for collision detection
        descriptorList.addAll(initialRoom.getAreaDescriptors(settings, roomPlacementPosition));

        // Register all doors from this room
        for (Direction facing : Direction.values()) {
            Map<String, List<BlockPos>> doorTypeMap = initialRoom.getAllDoorOffsetsForFacing(settings, facing, roomPlacementPosition);
            for (Entry<String, List<BlockPos>> entry : doorTypeMap.entrySet()) {
                availableDoorMasterMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>())
                        .computeIfAbsent(facing, k -> new ArrayList<>())
                        .addAll(entry.getValue());
            }
        }

        settings.addProcessor(new TrialSpawnerEntityProcessor());
        initialRoom.placeStructureAtPosition(rand, settings, world, roomPlacementPosition);

        world.setBlockAndUpdate(spawningPosition, NVBlocks.DUNGEON_CONTROLLER.block().get().defaultBlockState());
        if (world.getBlockEntity(spawningPosition) instanceof com.breakinblocks.neovitae.common.blockentity.DungeonControllerBlockEntity controller) {
            controller.setDungeonSynthesizer(this);
        }

        // Create door seal blocks for each potential connection
        List<DungeonDoor> doorTypeMap = initialRoom.getPotentialConnectedRoomTypes(settings, roomPlacementPosition);
        for (DungeonDoor dungeonDoor : doorTypeMap) {
            placeDoorSeal(world, spawningPosition, dungeonDoor);
        }

        BlockPos playerPos = initialRoom.getPlayerSpawnLocationForPlacement(settings, roomPlacementPosition);
        BlockPos portalLocation = initialRoom.getPortalOffsetLocationForPlacement(settings, roomPlacementPosition);

        return new BlockPos[] { playerPos, portalLocation };
    }

    /**
     * Places a door seal block at the given door position and fills in the doorway.
     * Checks for intersection with existing rooms - if intersection detected,
     * fills with solid wall instead of placing a seal.
     */
    private void placeDoorSeal(ServerLevel world, BlockPos controllerPos, DungeonDoor door) {
        BlockPos sealPos = door.doorPos().relative(door.doorDir()).above(2);

        // Check if this door would intersect with existing rooms
        AreaDescriptor doorDesc = door.descriptor();
        boolean wouldIntersect = doorDesc != null && doesDescriptorIntersect(doorDesc);

        // Fill in the doorway area with dungeon bricks using the door's descriptor
        if (doorDesc != null) {
            List<BlockPos> fillerPositions = doorDesc.getContainedPositions(sealPos);
            for (BlockPos fillerPos : fillerPositions) {
                world.setBlockAndUpdate(fillerPos, DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
            }
        } else {
            // Fallback: fill a 3x3 area if no descriptor
            Direction rightDir = door.doorDir().getClockWise();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    BlockPos fillerPos = sealPos.relative(rightDir, i).relative(Direction.UP, j);
                    world.setBlockAndUpdate(fillerPos, DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
                }
            }
        }

        // If door would intersect existing room, just leave as solid wall - no seal
        if (wouldIntersect) {
            LOGGER.debug("Door at {} would intersect existing room, filling with wall instead of seal", door.doorPos());
            return;
        }

        world.setBlockAndUpdate(sealPos, NVBlocks.DUNGEON_SEAL.block().get().defaultBlockState());
        activeSealCount++;

        // Configure the seal block entity
        if (world.getBlockEntity(sealPos) instanceof DungeonSealBlockEntity seal) {
            List<ResourceLocation> potentialRooms = new ArrayList<>();
            for (String roomType : door.getPotentialRoomTypes()) {
                potentialRooms.add(ResourceLocation.parse(roomType));
            }
            seal.initialize(controllerPos, door.doorPos(), door.doorDir(), door.doorType(), potentialRooms);
        }

        LOGGER.debug("Placed door seal at {} facing {}", sealPos, door.doorDir());
    }

    /**
     * Checks if a block position is within any placed room's area.
     */
    public boolean isBlockInDescriptor(BlockPos blockPos) {
        for (AreaDescriptor descriptor : descriptorList) {
            if (descriptor.isWithinArea(blockPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a block position is within or near any placed room's area.
     * Uses a tolerance to avoid ejecting players standing in doorways.
     */
    public boolean isBlockNearDescriptor(BlockPos blockPos, int tolerance) {
        for (AreaDescriptor descriptor : descriptorList) {
            if (descriptor instanceof AreaDescriptor.Rectangle rect) {
                if (blockPos.getX() >= rect.getMinimumOffset().getX() - tolerance &&
                    blockPos.getX() <= rect.getMaximumOffset().getX() + tolerance &&
                    blockPos.getY() >= rect.getMinimumOffset().getY() - tolerance &&
                    blockPos.getY() <= rect.getMaximumOffset().getY() + tolerance &&
                    blockPos.getZ() >= rect.getMinimumOffset().getZ() - tolerance &&
                    blockPos.getZ() <= rect.getMaximumOffset().getZ() + tolerance) {
                    return true;
                }
            } else if (descriptor.isWithinArea(blockPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any position in the list is within a placed room.
     */
    public boolean isAnyBlockInDescriptor(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            if (isBlockInDescriptor(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an area descriptor would intersect with any existing room.
     */
    public boolean doesDescriptorIntersect(AreaDescriptor desc) {
        for (AreaDescriptor descriptor : descriptorList) {
            if (descriptor.intersects(desc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a random room from the specified room pool.
     */
    public DungeonRoom getRandomRoom(ResourceLocation roomType, RandomSource rand) {
        return DungeonRoomRegistry.getRandomDungeonRoom(roomType, rand);
    }

    /**
     * Attempts to place a random room connecting to an existing door.
     * Tries all rooms in the pool (in weighted-random order) across all rotations
     * and door offsets before giving up.
     *
     * @return DungeonRoomPlacement if successful, null otherwise
     */
    public DungeonRoomPlacement getRandomPlacement(ServerLevel world, ResourceLocation roomType,
                                                    RandomSource rand, BlockPos activatedDoorPos,
                                                    Direction doorFacing, String activatedDoorType) {
        List<Pair<ResourceLocation, Integer>> poolEntries = DungeonRoomRegistry.getRoomPoolEntries(roomType);
        if (poolEntries == null || poolEntries.isEmpty()) {
            LOGGER.debug("No room pool found for type: {}", roomType);
            return null;
        }

        List<Pair<ResourceLocation, Integer>> shuffled = new ArrayList<>(poolEntries);
        Collections.shuffle(shuffled, new Random(rand.nextLong()));

        Direction oppositeDoorFacing = doorFacing.getOpposite();

        for (Pair<ResourceLocation, Integer> entry : shuffled) {
            DungeonRoom testingRoom = DungeonRoomRegistry.getDungeonRoom(entry.getLeft());
            if (testingRoom == null) continue;

            StructurePlaceSettings settings = new StructurePlaceSettings();
            settings.setMirror(Mirror.NONE);
            settings.setRotation(Rotation.NONE);
            settings.setIgnoreEntities(false);
            settings.setKnownShape(true);

            List<Rotation> rotationList = Rotation.getShuffled(rand);

            for (Rotation rotation : rotationList) {
                settings.setRotation(rotation);

                List<BlockPos> otherDoorList = testingRoom.getDoorOffsetsForFacing(settings, activatedDoorType,
                        oppositeDoorFacing, BlockPos.ZERO);

                if (otherDoorList == null || otherDoorList.isEmpty()) {
                    continue;
                }

                List<BlockPos> shuffledDoors = new ArrayList<>(otherDoorList);
                Collections.shuffle(shuffledDoors, new Random(rand.nextLong()));

                for (BlockPos testDoor : shuffledDoors) {
                    BlockPos roomLocation = activatedDoorPos.subtract(testDoor).offset(doorFacing.getNormal());

                    List<AreaDescriptor> descriptors = testingRoom.getAreaDescriptors(settings, roomLocation);
                    boolean valid = true;
                    String rejectReason = null;

                    for (AreaDescriptor testDesc : descriptors) {
                        if (!isAreaDescriptorInBounds(world, testDesc)) {
                            valid = false;
                            rejectReason = "out_of_bounds";
                            break;
                        }
                        for (AreaDescriptor currentDesc : descriptorList) {
                            if (testDesc.intersects(currentDesc)) {
                                valid = false;
                                if (testDesc instanceof AreaDescriptor.Rectangle tr && currentDesc instanceof AreaDescriptor.Rectangle cr) {
                                    rejectReason = "intersects existing descriptor [" +
                                            cr.getMinimumOffset() + " to " + cr.getMaximumOffset() +
                                            "] test=[" + tr.getMinimumOffset() + " to " + tr.getMaximumOffset() + "]";
                                } else {
                                    rejectReason = "intersects existing descriptor";
                                }
                                break;
                            }
                        }
                        if (!valid) break;
                    }

                    if (!valid) {
                        LOGGER.debug("Rejected {} rot={} at {}: {}",
                                testingRoom.getKey(), rotation, roomLocation, rejectReason);
                    }

                    if (valid) {
                        settings.clearProcessors();
                        settings.addProcessor(new StoneToOreProcessor(testingRoom.getOreDensity()));
                        settings.addProcessor(new TrialSpawnerEntityProcessor());
                        settings.addProcessor(new IronOreToGenerativeProcessor());

                        Pair<Direction, BlockPos> addedDoor = Pair.of(oppositeDoorFacing, testDoor.offset(roomLocation));
                        return new DungeonRoomPlacement(testingRoom, world, settings, roomLocation, addedDoor);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks and updates special room requirements based on progression.
     */
    public List<ResourceLocation> checkSpecialRoomRequirements(int currentRoomDepth) {
        for (ResourceLocation res : placementsSinceLastSpecial.keySet()) {
            placementsSinceLastSpecial.merge(res, 1, Integer::sum);
        }

        List<ResourceLocation> newSpecialPools = SpecialDungeonRoomPoolRegistry.getSpecialRooms(
                activatedDoors, currentRoomDepth, placementsSinceLastSpecial, specialRoomBuffer);
        specialRoomBuffer.addAll(newSpecialPools);
        return newSpecialPools;
    }

    /**
     * Gets the number of doors that have been activated (rooms placed).
     */
    public int getActivatedDoors() {
        return activatedDoors;
    }

    /**
     * Increments the activated door counter.
     */
    public void incrementActivatedDoors() {
        activatedDoors++;
    }

    public int getActiveSealCount() { return activeSealCount; }
    public List<ResourceLocation> getSpecialRoomBuffer() { return specialRoomBuffer; }
    public Map<ResourceLocation, Integer> getPlacementsSinceLastSpecial() { return placementsSinceLastSpecial; }
    public void incrementSealCount() { activeSealCount++; }
    public void decrementSealCount() { activeSealCount = Math.max(0, activeSealCount - 1); }

    public java.util.ArrayDeque<BlockPos> getPendingValidation() { return pendingValidation; }

    public void queueSealForValidation(BlockPos pos) {
        if (!pendingValidation.contains(pos)) pendingValidation.add(pos);
    }

    public boolean canAnythingFit(ServerLevel level, BlockPos doorPos, Direction doorFacing, String doorType, ResourceLocation[] pools) {
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setMirror(Mirror.NONE);
        settings.setIgnoreEntities(false);
        settings.setKnownShape(true);

        Direction opposite = doorFacing.getOpposite();

        for (ResourceLocation poolName : pools) {
            List<org.apache.commons.lang3.tuple.Pair<ResourceLocation, Integer>> poolEntries =
                    DungeonRoomRegistry.getRoomPoolEntries(poolName);
            if (poolEntries == null) continue;

            for (var entry : poolEntries) {
                DungeonRoom room = DungeonRoomRegistry.getDungeonRoom(entry.getLeft());
                if (room == null) continue;

                for (Rotation rotation : Rotation.values()) {
                    settings.setRotation(rotation);
                    List<BlockPos> doors = room.getDoorOffsetsForFacing(settings, doorType, opposite, BlockPos.ZERO);
                    if (doors == null || doors.isEmpty()) continue;

                    for (BlockPos testDoor : doors) {
                        BlockPos roomLocation = doorPos.subtract(testDoor).offset(doorFacing.getNormal());
                        List<AreaDescriptor> descs = room.getAreaDescriptors(settings, roomLocation);
                        boolean valid = true;

                        for (AreaDescriptor testDesc : descs) {
                            if (!isAreaDescriptorInBounds(level, testDesc)) { valid = false; break; }
                            for (AreaDescriptor current : descriptorList) {
                                if (testDesc.intersects(current)) { valid = false; break; }
                            }
                            if (!valid) break;
                        }

                        if (valid) return true;
                    }
                }
            }
        }
        return false;
    }
}
