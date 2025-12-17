package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;
import com.breakinblocks.neovitae.structures.rooms.DungeonRoomPlacement;
import com.breakinblocks.neovitae.util.Constants;

import javax.annotation.Nullable;

/**
 * Block entity for the Dungeon Controller block.
 * Manages procedural dungeon generation via the DungeonSynthesizer.
 */
public class TileDungeonController extends BaseTile {

    private static final Logger LOGGER = LoggerFactory.getLogger(TileDungeonController.class);

    private DungeonSynthesizer dungeonSynthesizer;
    private boolean initialized = false;

    public TileDungeonController(BlockPos pos, BlockState state) {
        super(BMTiles.DUNGEON_CONTROLLER_TYPE.get(), pos, state);
        this.dungeonSynthesizer = new DungeonSynthesizer();
    }

    /**
     * Gets the dungeon synthesizer for this controller.
     */
    public DungeonSynthesizer getDungeonSynthesizer() {
        return dungeonSynthesizer;
    }

    /**
     * Sets the dungeon synthesizer, typically after generating an initial room.
     */
    public void setDungeonSynthesizer(DungeonSynthesizer synthesizer) {
        this.dungeonSynthesizer = synthesizer;
        this.initialized = true;
        setChanged();
    }

    /**
     * Checks if this controller has been initialized with a dungeon.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Handles a request from a dungeon seal to place a new room.
     *
     * @param sealPos        The position of the seal making the request
     * @param doorPos        The position of the door
     * @param doorDirection  The direction the door faces
     * @param doorType       The type of door
     * @param potentialRooms The list of potential room pool IDs
     * @param rand           Random source
     * @return true if a room was successfully placed
     */
    public boolean handleRequestForRoomPlacement(BlockPos sealPos, BlockPos doorPos,
                                                  Direction doorDirection, String doorType,
                                                  ResourceLocation[] potentialRooms, RandomSource rand) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (dungeonSynthesizer == null) {
            LOGGER.warn("DungeonSynthesizer is null for controller at {}", worldPosition);
            return false;
        }

        LOGGER.info("Processing room placement request: doorPos={}, direction={}, doorType={}, potentialPools={}",
                doorPos, doorDirection, doorType, potentialRooms.length);

        // Try each potential room type until one succeeds
        for (ResourceLocation roomType : potentialRooms) {
            LOGGER.debug("Trying room pool: {}", roomType);
            DungeonRoomPlacement placement = dungeonSynthesizer.getRandomPlacement(
                    serverLevel, roomType, rand, doorPos, doorDirection, doorType);

            if (placement != null) {
                LOGGER.info("Found valid placement from pool {}, placing room {} at {}",
                        roomType, placement.room.getKey(), placement.getRoomPosition());

                try {
                    // Place the room
                    placement.placeRoom(rand, serverLevel);

                    // Add the room's area descriptors to prevent future collisions
                    dungeonSynthesizer.getDescriptorList().addAll(placement.getAreaDescriptors());

                    // Update door tracking
                    dungeonSynthesizer.incrementActivatedDoors();

                    // Check for special room requirements based on progression
                    dungeonSynthesizer.checkSpecialRoomRequirements(
                            dungeonSynthesizer.getDescriptorList().size());

                    // Update available doors map
                    placement.updateDoorMasterMap(dungeonSynthesizer.getAvailableDoorMasterMap());

                    // Place new door seals for the placed room
                    placement.placeNewDoorSeals(serverLevel, worldPosition, dungeonSynthesizer);

                    setChanged();

                    LOGGER.info("Successfully placed room {} from pool {} at {}",
                            placement.room.getKey(), roomType, placement.getRoomPosition());
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Failed to place room {} from pool {} at {}: {}",
                            placement.room.getKey(), roomType, placement.getRoomPosition(), e.getMessage());
                    LOGGER.debug("Room placement exception details:", e);
                    // Continue to try other room pools
                }
            } else {
                LOGGER.debug("No valid placement found from pool {}", roomType);
            }
        }

        LOGGER.warn("Failed to place any room from {} potential pools at door {} (direction: {}, type: {})",
                potentialRooms.length, doorPos, doorDirection, doorType);
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putBoolean("initialized", initialized);

        if (dungeonSynthesizer != null) {
            CompoundTag synthTag = new CompoundTag();
            dungeonSynthesizer.writeToNBT(synthTag);
            tag.put(Constants.NBT.SYNTHESIZER, synthTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        initialized = tag.getBoolean("initialized");

        if (tag.contains(Constants.NBT.SYNTHESIZER)) {
            if (dungeonSynthesizer == null) {
                dungeonSynthesizer = new DungeonSynthesizer();
            }
            dungeonSynthesizer.readFromNBT(tag.getCompound(Constants.NBT.SYNTHESIZER));
        }
    }

    /**
     * Server-side tick for the dungeon controller.
     * Currently unused but available for future features like dungeon events.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, TileDungeonController tile) {
        if (level.isClientSide()) {
            return;
        }

        // Future: Handle dungeon events, mob spawning, etc.
    }
}
