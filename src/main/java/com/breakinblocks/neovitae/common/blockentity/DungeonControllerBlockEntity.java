package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;
import com.breakinblocks.neovitae.structures.ModRoomPools;
import com.breakinblocks.neovitae.structures.rooms.DungeonRoomPlacement;
import com.breakinblocks.neovitae.util.Constants;

import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

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
                                                  Identifier[] potentialRooms, RandomSource rand) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (dungeonSynthesizer == null) {
            LOGGER.warn("DungeonSynthesizer is null for controller at {}", worldPosition);
            return false;
        }

        LOGGER.debug("[GEN] Controller {} processing placement: doorPos={}, dir={}, doorType={}, pools={} (existing rooms={}, activatedDoors={}, seals={})",
                worldPosition, doorPos, doorDirection, doorType, potentialRooms.length,
                dungeonSynthesizer.getDescriptorList().size(),
                dungeonSynthesizer.getActivatedDoors(),
                dungeonSynthesizer.getActiveSealCount());

        for (int p = 0; p < potentialRooms.length; p++) {
            Identifier roomType = potentialRooms[p];
            LOGGER.debug("[GEN]   Pool {}/{}: trying {}", p + 1, potentialRooms.length, roomType);

            DungeonRoomPlacement placement;
            try {
                placement = dungeonSynthesizer.getRandomPlacement(
                        serverLevel, roomType, rand, doorPos, doorDirection, doorType);
            } catch (Exception e) {
                LOGGER.error("[GEN]   Exception while finding placement from pool {} at door {}: {}",
                        roomType, doorPos, e.getMessage(), e);
                continue;
            }

            if (placement != null) {
                LOGGER.debug("[GEN]   Pool {} -> SELECTED room={} pos={} rot={} (placing now)",
                        roomType, placement.room.getKey(), placement.getRoomPosition(),
                        placement.settings.getRotation());

                try {
                    placement.placeRoom(rand, serverLevel);

                    int beforeDescs = dungeonSynthesizer.getDescriptorList().size();
                    dungeonSynthesizer.getDescriptorList().addAll(placement.getAreaDescriptors());

                    dungeonSynthesizer.incrementActivatedDoors();

                    List<Identifier> newlyUnlocked = dungeonSynthesizer.checkSpecialRoomRequirements(
                            dungeonSynthesizer.getDescriptorList().size());

                    placement.updateDoorMasterMap(dungeonSynthesizer.getAvailableDoorMasterMap());

                    placement.placeNewDoorSeals(serverLevel, worldPosition, dungeonSynthesizer);

                    setChanged();

                    notifyProgressionThresholds(serverLevel, newlyUnlocked);
                    notifySpatialDistortion(serverLevel, roomType, placement);

                    LOGGER.debug("[GEN]   Placement done: room={} pool={} pos={}; descs {} -> {}, doors={}, seals={}, unlockedSpecials={}",
                            placement.room.getKey(), roomType, placement.getRoomPosition(),
                            beforeDescs, dungeonSynthesizer.getDescriptorList().size(),
                            dungeonSynthesizer.getActivatedDoors(),
                            dungeonSynthesizer.getActiveSealCount(),
                            newlyUnlocked);
                    return true;
                } catch (Exception e) {
                    LOGGER.error("[GEN]   Failed to place room {} from pool {} at {}",
                            placement.room.getKey(), roomType, placement.getRoomPosition(), e);
                    // Continue to try other room pools
                }
            } else {
                LOGGER.debug("[GEN]   Pool {} -> no valid placement", roomType);
            }
        }

        LOGGER.debug("[GEN] Controller {} EXHAUSTED all {} pools at door {} (dir={}, type={})",
                worldPosition, potentialRooms.length, doorPos, doorDirection, doorType);
        return false;
    }

    private void notifyProgressionThresholds(ServerLevel level, List<Identifier> newlyUnlocked) {
        for (Identifier pool : newlyUnlocked) {
            String msgKey;
            if (pool.equals(ModRoomPools.MINE_ENTRANCES)) {
                msgKey = "chat.neovitae.dungeon.threshold.mine_entrance";
            } else if (pool.equals(ModRoomPools.MINE_KEY)) {
                msgKey = "chat.neovitae.dungeon.threshold.mine_key";
            } else {
                continue;
            }

            Component msg = Component.translatable(msgKey).withStyle(ChatFormatting.DARK_PURPLE);
            for (ServerPlayer player : level.players()) {
                player.connection.send(new ClientboundSetActionBarTextPacket(msg));
                player.sendSystemMessage(msg);
                level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 0.6F, 0.5F);
            }
        }
    }

    private void notifySpatialDistortion(ServerLevel level, Identifier roomType, DungeonRoomPlacement placement) {
        boolean isSpecial = roomType.getPath().contains("special") || roomType.getPath().contains("mine_key");
        if (!isSpecial) return;
        if (spatialDistortionPlayed) return;

        spatialDistortionPlayed = true;
        setChanged();

        BlockPos roomCenter = placement.getRoomPosition().offset(
                placement.room.getAreaDescriptors(new StructurePlaceSettings(), placement.getRoomPosition())
                        .stream().findFirst()
                        .map(d -> {
                            if (d instanceof AreaDescriptor.Rectangle r) {
                                BlockPos min = r.getMinimumOffset();
                                BlockPos max = r.getMaximumOffset();
                                return new BlockPos((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
                            }
                            return BlockPos.ZERO;
                        }).orElse(BlockPos.ZERO));

        Component msg = Component.translatable("chat.neovitae.dungeon.spatial_distortion").withStyle(ChatFormatting.DARK_PURPLE);
        for (ServerPlayer player : level.players()) {
            player.connection.send(new ClientboundSetActionBarTextPacket(msg));
            player.sendSystemMessage(msg);
        }

        for (int i = 0; i < 4; i++) {
            BlockPos offset = roomCenter.offset(level.getRandom().nextInt(6) - 3, level.getRandom().nextInt(3), level.getRandom().nextInt(6) - 3);
            StreamPresets.voidTendril(offset, roomCenter).build().sendToNearby(level, roomCenter, 128);
            StreamPresets.demonTether(roomCenter, offset).build().sendToNearby(level, roomCenter, 128);
        }

        level.playSound(null, roomCenter, SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 1.0F, 0.3F);
    }

    private boolean spatialDistortionPlayed = false;
    private boolean riftOpened = false;
    private BlockPos portalPos;

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("initialized", initialized);
        tag.putBoolean("riftOpened", riftOpened);
        tag.putBoolean("spatialDistortionPlayed", spatialDistortionPlayed);
        if (portalPos != null) {
            tag.putInt("portalX", portalPos.getX());
            tag.putInt("portalY", portalPos.getY());
            tag.putInt("portalZ", portalPos.getZ());
        }

        if (dungeonSynthesizer != null) {
            CompoundTag synthTag = new CompoundTag();
            dungeonSynthesizer.writeToNBT(synthTag);
            tag.store(Constants.NBT.SYNTHESIZER, CompoundTag.CODEC, synthTag);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);

        initialized = tag.getBooleanOr("initialized", false);
        riftOpened = tag.getBooleanOr("riftOpened", false);
        spatialDistortionPlayed = tag.getBooleanOr("spatialDistortionPlayed", false);
        int px = tag.getIntOr("portalX", Integer.MIN_VALUE);
        if (px != Integer.MIN_VALUE) {
            portalPos = new BlockPos(px, tag.getIntOr("portalY", 0), tag.getIntOr("portalZ", 0));
        }

        tag.read(Constants.NBT.SYNTHESIZER, CompoundTag.CODEC).ifPresent(synthTag -> {
            if (dungeonSynthesizer == null) {
                dungeonSynthesizer = new DungeonSynthesizer();
            }
            dungeonSynthesizer.readFromNBT(synthTag);
        });
    }

    public void setPortalPos(BlockPos pos) { this.portalPos = pos; }
    public BlockPos getPortalPos() { return portalPos; }
    public boolean isRiftOpened() { return riftOpened; }

    public void queueSealForValidation(BlockPos sealPos) {
        if (dungeonSynthesizer != null) {
            dungeonSynthesizer.queueSealForValidation(sealPos);
            setChanged();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DungeonControllerBlockEntity tile) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
        if (tile.dungeonSynthesizer == null || !tile.initialized) return;

        var queue = tile.dungeonSynthesizer.getPendingValidation();
        if (!queue.isEmpty()) {
            BlockPos sealPos = queue.poll();
            LOGGER.debug("[GEN] tick: validating seal at {} (queue remaining={})", sealPos, queue.size());
            if (serverLevel.getBlockEntity(sealPos) instanceof DungeonSealBlockEntity seal) {
                var sealData = seal.getData();
                if (sealData != null) {
                    Identifier[] pools = sealData.potentialRoomTypes().toArray(new Identifier[0]);
                    boolean canFit = tile.dungeonSynthesizer.canAnythingFit(
                            serverLevel, sealData.doorPos(), sealData.doorDirection(), sealData.doorType(), pools);
                    if (!canFit) {
                        LOGGER.debug("[GEN]   walling off seal {} (no remaining fit)", sealPos);
                        serverLevel.setBlockAndUpdate(sealPos,
                                DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get().defaultBlockState());
                        tile.dungeonSynthesizer.decrementSealCount();
                        tile.setChanged();
                    }
                }
            } else {
                LOGGER.debug("[GEN]   seal {} no longer exists; decrementing seal count", sealPos);
                tile.dungeonSynthesizer.decrementSealCount();
                tile.setChanged();
            }
        }

        if (!tile.riftOpened
                && tile.dungeonSynthesizer.getActiveSealCount() <= 0
                && tile.dungeonSynthesizer.getActivatedDoors() > 0
                && tile.portalPos != null) {
            tile.openSpatialRift(serverLevel);
        }
    }

    private void openSpatialRift(ServerLevel level) {
        riftOpened = true;
        setChanged();

        BlockPos riftPos = portalPos.above(3);

        level.setBlockAndUpdate(riftPos, NVBlocks.SPATIAL_RIFT.block().get().defaultBlockState());

        Component msg = Component.translatable("chat.neovitae.dungeon.rift_opened").withStyle(ChatFormatting.DARK_PURPLE);
        for (ServerPlayer player : level.players()) {
            player.connection.send(new ClientboundSetActionBarTextPacket(msg));
            player.sendSystemMessage(msg);
        }

        level.playSound(null, riftPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 0.5F);

        for (int i = 0; i < 6; i++) {
            BlockPos offset = riftPos.offset(level.getRandom().nextInt(8) - 4, level.getRandom().nextInt(4) - 2, level.getRandom().nextInt(8) - 4);
            StreamPresets.bloodTendril(offset, riftPos).build().sendToNearby(level, riftPos, 128);
        }

        LOGGER.info("Spatial rift opened at {} for dungeon controller at {}", riftPos, worldPosition);
    }
}
