package com.breakinblocks.neovitae.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;

import java.util.*;
import java.util.Map.Entry;

/**
 * Represents a dungeon room definition with structures, doors, and area descriptors.
 * Rooms are loaded from JSON files and used by the DungeonSynthesizer.
 * Encapsulated fields with getters/setters for proper data hiding.
 */
public class DungeonRoom {

    private ResourceLocation key;
    private int dungeonWeight = 1;

    // Map of structure resource locations to their offset positions
    private final Map<String, BlockPos> structureMap = new TreeMap<>();

    // Map of door types to their positions by facing direction
    private final Map<String, Map<Direction, List<BlockPos>>> doorMap = new TreeMap<>();

    // Area descriptors for collision detection
    private final List<AreaDescriptor.Rectangle> descriptorList = new ArrayList<>();

    // Post-processing settings
    private float oreDensity = 0;
    private BlockPos spawnLocation = BlockPos.ZERO;
    private BlockPos controllerOffset = BlockPos.ZERO;
    private BlockPos portalOffset = BlockPos.ZERO;

    // Door index mappings for room type connections
    private final Map<Integer, List<BlockPos>> indexToDoorMap = new TreeMap<>();
    private final Map<Integer, List<String>> indexToRoomTypeMap = new TreeMap<>();
    private final Map<String, List<BlockPos>> requiredDoorMap = new TreeMap<>();
    private final Map<Integer, AreaDescriptor.Rectangle> doorCoverMap = new TreeMap<>();

    public DungeonRoom(Map<String, BlockPos> structureMap,
                       Map<String, Map<Direction, List<BlockPos>>> doorMap,
                       List<AreaDescriptor.Rectangle> descriptorList) {
        this.structureMap.putAll(structureMap);
        this.doorMap.putAll(doorMap);
        this.descriptorList.addAll(descriptorList);
    }

    public DungeonRoom() {
        this(new TreeMap<>(), new TreeMap<>(), new ArrayList<>());
    }

    // ==================== Getters ====================

    public ResourceLocation getKey() {
        return key;
    }

    public int getDungeonWeight() {
        return dungeonWeight;
    }

    public Map<String, BlockPos> getStructureMap() {
        return Collections.unmodifiableMap(structureMap);
    }

    public Map<String, Map<Direction, List<BlockPos>>> getDoorMap() {
        return Collections.unmodifiableMap(doorMap);
    }

    public List<AreaDescriptor.Rectangle> getDescriptorList() {
        return Collections.unmodifiableList(descriptorList);
    }

    public float getOreDensity() {
        return oreDensity;
    }

    public BlockPos getSpawnLocation() {
        return spawnLocation;
    }

    public BlockPos getControllerOffset() {
        return controllerOffset;
    }

    public BlockPos getPortalOffset() {
        return portalOffset;
    }

    // ==================== Setters ====================

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public void setDungeonWeight(int weight) {
        this.dungeonWeight = weight;
    }

    public void setOreDensity(float oreDensity) {
        this.oreDensity = oreDensity;
    }

    public void setSpawnLocation(BlockPos spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setControllerOffset(BlockPos controllerOffset) {
        this.controllerOffset = controllerOffset;
    }

    public void setPortalOffset(BlockPos portalOffset) {
        this.portalOffset = portalOffset;
    }

    // ==================== Builder Methods ====================

    public DungeonRoom addStructure(String location, BlockPos pos) {
        structureMap.put(location, pos);
        return this;
    }

    public DungeonRoom addAreaDescriptor(AreaDescriptor.Rectangle descriptor) {
        descriptorList.add(descriptor);
        return this;
    }

    public DungeonRoom addNonstandardDoor(BlockPos pos, Direction dir, String thisDoorType,
                                          int index, String wantedDoorType) {
        requiredDoorMap.computeIfAbsent(wantedDoorType, k -> new ArrayList<>()).add(pos);
        return addDoor(pos, dir, thisDoorType, index);
    }

    public DungeonRoom addDoor(BlockPos pos, Direction dir, String doorType, int index) {
        doorMap.computeIfAbsent(doorType, k -> new TreeMap<>())
               .computeIfAbsent(dir, k -> new ArrayList<>())
               .add(pos);

        indexToDoorMap.computeIfAbsent(index, k -> new ArrayList<>()).add(pos);
        return this;
    }

    public DungeonRoom addDoors(Direction dir, String doorType, int index, BlockPos... positions) {
        for (BlockPos pos : positions) {
            addDoor(pos, dir, doorType, index);
        }
        return this;
    }

    public DungeonRoom addNormalRoomPool(int index, ResourceLocation roomPool) {
        return addRoomPool(index, roomPool.toString());
    }

    public DungeonRoom addSpecialRoomPool(int index, ResourceLocation roomPool) {
        return addRoomPool(index, "#" + roomPool.toString());
    }

    public DungeonRoom addDeadendRoomPool(int index, ResourceLocation roomPool) {
        return addRoomPool(index, "$" + roomPool.toString());
    }

    public DungeonRoom addRoomPool(int index, String roomPool) {
        indexToRoomTypeMap.computeIfAbsent(index, k -> new ArrayList<>()).add(roomPool);
        return this;
    }

    public DungeonRoom withOreDensity(float oreDensity) {
        this.oreDensity = oreDensity;
        return this;
    }

    public DungeonRoom registerDoorFill(int index, AreaDescriptor.Rectangle desc) {
        doorCoverMap.put(index, desc);
        return this;
    }

    // ==================== Position Calculation Methods ====================

    public BlockPos getOriginalBlockPos(BlockPos worldDoorPos, StructurePlaceSettings settings, BlockPos offset) {
        StructurePlaceSettings oppositeSettings = settings.copy();
        oppositeSettings.setRotation(DungeonUtil.getOppositeRotation(settings.getRotation()));
        return StructureTemplate.calculateRelativePosition(oppositeSettings, worldDoorPos).subtract(offset);
    }

    public int getIndexForDoor(BlockPos originalDoorPos) {
        for (Entry<Integer, List<BlockPos>> entry : indexToDoorMap.entrySet()) {
            if (entry.getValue().contains(originalDoorPos)) {
                return entry.getKey();
            }
        }
        return 1;
    }

    public AreaDescriptor getDoorFillDescriptor(BlockPos originalDoorPos) {
        int index = getIndexForDoor(originalDoorPos);
        if (doorCoverMap.containsKey(index)) {
            return doorCoverMap.get(index);
        }
        return new AreaDescriptor.Rectangle(new BlockPos(-1, -1, 0), 3, 3, 1);
    }

    public AreaDescriptor getDoorFillDescriptor(StructurePlaceSettings settings, BlockPos originalDoorPos,
                                                 BlockPos newDoorPos, Direction dir) {
        StructurePlaceSettings rotatedSettings = settings.copy();
        rotatedSettings.setRotation(rotatedSettings.getRotation().getRotated(getRotationForDirectionFromNorth(dir)));
        AreaDescriptor desc = getDoorFillDescriptor(originalDoorPos);
        return rotateDescriptor(desc, rotatedSettings);
    }

    public Rotation getRotationForDirectionFromNorth(Direction dir) {
        return switch (dir) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    // ==================== Door Connection Methods ====================

    public List<DungeonDoor> getPotentialConnectedRoomTypes(StructurePlaceSettings settings, BlockPos offset) {
        List<DungeonDoor> dungeonDoorList = new ArrayList<>();

        for (Entry<String, Map<Direction, List<BlockPos>>> entry : doorMap.entrySet()) {
            Map<Direction, List<BlockPos>> doorDirMap = entry.getValue();
            String doorType = entry.getKey();

            for (int i = 0; i < 4; i++) {
                Direction originalFacing = Direction.from2DDataValue(i);
                if (!doorDirMap.containsKey(originalFacing)) continue;

                Direction rotatedFacing = DungeonUtil.getFacingForSettings(settings, originalFacing);
                List<BlockPos> doorList = doorDirMap.get(originalFacing);

                if (indexToDoorMap.isEmpty()) {
                    List<String> roomTypeList = new ArrayList<>();
                    for (BlockPos doorPos : doorList) {
                        BlockPos newDoorPos = StructureTemplate.calculateRelativePosition(settings, doorPos).offset(offset);
                        dungeonDoorList.add(new DungeonDoor(newDoorPos, rotatedFacing, doorType, roomTypeList,
                                getDoorFillDescriptor(settings, doorPos, newDoorPos, originalFacing)));
                    }
                    continue;
                }

                for (Entry<Integer, List<BlockPos>> rotatedIndexEntry : indexToDoorMap.entrySet()) {
                    int index = rotatedIndexEntry.getKey();
                    List<String> roomTypeList = indexToRoomTypeMap.get(index);
                    List<BlockPos> indexedDoorList = rotatedIndexEntry.getValue();

                    for (BlockPos indexPos : indexedDoorList) {
                        if (doorList.contains(indexPos)) {
                            String requiredType = getRequiredDoorType(doorType, indexPos);
                            BlockPos newDoorPos = StructureTemplate.calculateRelativePosition(settings, indexPos).offset(offset);
                            dungeonDoorList.add(new DungeonDoor(newDoorPos, rotatedFacing, requiredType, roomTypeList,
                                    getDoorFillDescriptor(settings, indexPos, newDoorPos, originalFacing)));
                        }
                    }
                }
            }
        }

        return dungeonDoorList;
    }

    public String getRequiredDoorType(String type, BlockPos indexPos) {
        for (Entry<String, List<BlockPos>> entry : requiredDoorMap.entrySet()) {
            if (entry.getValue().contains(indexPos)) {
                return entry.getKey();
            }
        }
        return type;
    }

    // ==================== Area Descriptor Methods ====================

    public List<AreaDescriptor> getAreaDescriptors(StructurePlaceSettings settings, BlockPos offset) {
        List<AreaDescriptor> newList = new ArrayList<>();
        for (AreaDescriptor desc : descriptorList) {
            newList.add(rotateDescriptor(desc, settings).offset(offset));
        }
        return newList;
    }

    private AreaDescriptor rotateDescriptor(AreaDescriptor desc, StructurePlaceSettings settings) {
        if (desc instanceof AreaDescriptor.Rectangle rect) {
            BlockPos min = StructureTemplate.calculateRelativePosition(settings, rect.getMinimumOffset());
            BlockPos max = StructureTemplate.calculateRelativePosition(settings, rect.getMaximumOffset());
            return new AreaDescriptor.Rectangle(
                    new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ())),
                    new BlockPos(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()))
            );
        }
        return desc.copy();
    }

    // ==================== Spawn/Portal Position Methods ====================

    public BlockPos getPlayerSpawnLocationForPlacement(StructurePlaceSettings settings, BlockPos offset) {
        return StructureTemplate.calculateRelativePosition(settings, spawnLocation).offset(offset);
    }

    public BlockPos getPortalOffsetLocationForPlacement(StructurePlaceSettings settings, BlockPos offset) {
        return StructureTemplate.calculateRelativePosition(settings, portalOffset).offset(offset);
    }

    public BlockPos getInitialSpawnOffsetForControllerPos(StructurePlaceSettings settings, BlockPos controllerPos) {
        if (controllerOffset == null) {
            return controllerPos;
        }
        return controllerPos.subtract(StructureTemplate.calculateRelativePosition(settings, controllerOffset));
    }

    // ==================== Door Offset Methods ====================

    public List<BlockPos> getDoorOffsetsForFacing(StructurePlaceSettings settings, String doorType,
                                                   Direction facing, BlockPos offset) {
        List<BlockPos> offsetList = new ArrayList<>();

        if (doorMap.containsKey(doorType)) {
            Map<Direction, List<BlockPos>> doorDirMap = doorMap.get(doorType);
            Direction originalFacing = DungeonUtil.reverseRotate(settings.getMirror(), settings.getRotation(), facing);

            if (doorDirMap.containsKey(originalFacing)) {
                List<BlockPos> doorList = doorDirMap.get(originalFacing);
                for (BlockPos doorPos : doorList) {
                    offsetList.add(StructureTemplate.calculateRelativePosition(settings, doorPos).offset(offset));
                }
            }
        }

        return offsetList;
    }

    public Map<String, List<BlockPos>> getAllDoorOffsetsForFacing(StructurePlaceSettings settings,
                                                                   Direction facing, BlockPos offset) {
        Map<String, List<BlockPos>> offsetMap = new TreeMap<>();
        for (String type : doorMap.keySet()) {
            offsetMap.put(type, getDoorOffsetsForFacing(settings, type, facing, offset));
        }
        return offsetMap;
    }

    // ==================== Structure Placement ====================

    public boolean placeStructureAtPosition(RandomSource rand, StructurePlaceSettings settings,
                                            ServerLevel world, BlockPos pos) {
        Map<BlockPos, List<String>> compositeMap = new TreeMap<>();

        for (Entry<String, BlockPos> entry : structureMap.entrySet()) {
            BlockPos structureKey = entry.getValue();
            String structure = entry.getKey();
            compositeMap.computeIfAbsent(structureKey, k -> new ArrayList<>()).add(structure);
        }

        for (Entry<BlockPos, List<String>> entry : compositeMap.entrySet()) {
            List<String> structures = entry.getValue();
            ResourceLocation location = ResourceLocation.parse(structures.get(rand.nextInt(structures.size())));
            BlockPos offsetPos = StructureTemplate.calculateRelativePosition(settings, entry.getKey());
            DungeonStructure structure = new DungeonStructure(location);
            structure.placeStructureAtPosition(rand, settings, world, pos.offset(offsetPos));
        }

        return true;
    }

    @Override
    public String toString() {
        return "DungeonRoom{key=" + key + ", weight=" + dungeonWeight + ", structures=" + structureMap.size() + "}";
    }
}
