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

public class DungeonControllerBlockEntity extends BaseBlockEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonControllerBlockEntity.class);

    private DungeonSynthesizer dungeonSynthesizer;
    private boolean initialized = false;

    public DungeonControllerBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.DUNGEON_CONTROLLER_TYPE.get(), pos, state);
        this.dungeonSynthesizer = new DungeonSynthesizer();
    }

    public DungeonSynthesizer getDungeonSynthesizer() {
        return dungeonSynthesizer;
    }

    public void setDungeonSynthesizer(DungeonSynthesizer synthesizer) {
        this.dungeonSynthesizer = synthesizer;
        this.initialized = true;
        setChanged();
    }

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

        for (ResourceLocation roomType : potentialRooms) {
            LOGGER.debug("Trying room pool: {}", roomType);

            DungeonRoomPlacement placement;
            try {
                placement = dungeonSynthesizer.getRandomPlacement(
                        serverLevel, roomType, rand, doorPos, doorDirection, doorType);
            } catch (Exception e) {
                LOGGER.error("Exception while finding placement from pool {} at door {}: {}",
                        roomType, doorPos, e.getMessage(), e);
                continue;
            }

            if (placement != null) {
                LOGGER.info("Found valid placement from pool {}, placing room {} at {}",
                        roomType, placement.room.getKey(), placement.getRoomPosition());

                try {
                    placement.placeRoom(rand, serverLevel);

                    dungeonSynthesizer.getDescriptorList().addAll(placement.getAreaDescriptors());

                    dungeonSynthesizer.incrementActivatedDoors();

                    dungeonSynthesizer.checkSpecialRoomRequirements(
                            dungeonSynthesizer.getDescriptorList().size());

                    placement.updateDoorMasterMap(dungeonSynthesizer.getAvailableDoorMasterMap());

                    placement.placeNewDoorSeals(serverLevel, worldPosition, dungeonSynthesizer);

                    setChanged();

                    LOGGER.info("Successfully placed room {} from pool {} at {}",
                            placement.room.getKey(), roomType, placement.getRoomPosition());
                    return true;
                } catch (Exception e) {
                    LOGGER.error("Failed to place room {} from pool {} at {}",
                            placement.room.getKey(), roomType, placement.getRoomPosition(), e);
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

    public static void tick(Level level, BlockPos pos, BlockState state, DungeonControllerBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        // Future: Handle dungeon events, mob spawning, etc.
    }
}
