package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.BlockInversionPillar;
import com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.type.PillarCapType;
import com.breakinblocks.neovitae.common.dataattachment.DungeonExitData;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;
import com.breakinblocks.neovitae.structures.ModRoomPools;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import javax.annotation.Nullable;

public class SpatialRiftBlockEntity extends BaseBlockEntity {

    @Nullable private BlockPos destinationSpawnPos;
    @Nullable private BlockPos destinationControllerPos;
    private int cooldown = 0;

    public SpatialRiftBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPATIAL_RIFT_TYPE.get(), pos, state);
    }

    public void handlePlayerEntry(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (cooldown > 0) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        cooldown = 100;

        if (destinationSpawnPos != null && destinationControllerPos != null) {
            DungeonDimensionHelper.teleportToDungeon(serverPlayer, destinationSpawnPos);
            DungeonExitData exitData = serverPlayer.getData(NVDataAttachments.DUNGEON_EXIT);
            serverPlayer.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData.withControllerPos(destinationControllerPos));
            return;
        }

        ServerLevel dungeonWorld = DungeonDimensionHelper.getDungeonWorld(serverLevel);
        if (dungeonWorld == null) return;

        BlockPos newControllerPos = AnimaHelper.getSpawnPositionOfDungeon();
        if (newControllerPos == null) return;
        AnimaHelper.incrementDungeonCounter();

        DungeonSynthesizer synthesizer = new DungeonSynthesizer();
        BlockPos[] positions = synthesizer.generateInitialRoom(
                ModRoomPools.STANDARD_DUNGEON_ENTRANCES,
                dungeonWorld.random,
                dungeonWorld,
                newControllerPos
        );

        BlockPos playerSpawnPos = positions[0];
        BlockPos portalPos = positions[1];

        if (dungeonWorld.getBlockEntity(newControllerPos) instanceof DungeonControllerBlockEntity controller) {
            controller.setPortalPos(portalPos);
        }

        DungeonExitData exitData = serverPlayer.getData(NVDataAttachments.DUNGEON_EXIT);

        spawnReturnPillar(dungeonWorld, portalPos, worldPosition);

        if (exitData.isValid()) {
            spawnHomePillar(dungeonWorld, portalPos.above(3), exitData.getExitPosOrNull(), exitData.getExitDimensionOrNull());
        }

        destinationSpawnPos = playerSpawnPos;
        destinationControllerPos = newControllerPos;
        setChanged();

        DungeonDimensionHelper.teleportToDungeon(serverPlayer, playerSpawnPos);
        serverPlayer.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData.withControllerPos(newControllerPos));
    }

    private void spawnReturnPillar(ServerLevel world, BlockPos nearPos, BlockPos returnTo) {
        BlockPos pillarPos = nearPos.east(3);
        world.setBlockAndUpdate(pillarPos, NVBlocks.INVERSION_PILLAR.block().get().defaultBlockState()
                .setValue(BlockInversionPillar.RIFT_RETURN, true));
        if (world.getBlockEntity(pillarPos) instanceof InversionPillarBlockEntity pillar) {
            pillar.setDestination(returnTo, DungeonDimensionHelper.getDungeonDimensionId());
        }
        world.setBlockAndUpdate(pillarPos.below(),
                NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                        .setValue(BlockInversionPillarEnd.TYPE,
                                PillarCapType.BOTTOM)
                        .setValue(BlockInversionPillarEnd.RIFT_RETURN, true));
        world.setBlockAndUpdate(pillarPos.above(),
                NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                        .setValue(BlockInversionPillarEnd.TYPE,
                                PillarCapType.TOP)
                        .setValue(BlockInversionPillarEnd.RIFT_RETURN, true));
    }

    private void spawnHomePillar(ServerLevel world, BlockPos pillarPos, BlockPos exitPos, ResourceKey<Level> exitDim) {
        world.setBlockAndUpdate(pillarPos, NVBlocks.INVERSION_PILLAR.block().get().defaultBlockState());
        if (world.getBlockEntity(pillarPos) instanceof InversionPillarBlockEntity pillar) {
            pillar.setDestination(exitPos, exitDim.location());
        }
        world.setBlockAndUpdate(pillarPos.below(),
                NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                        .setValue(BlockInversionPillarEnd.TYPE,
                                PillarCapType.BOTTOM));
        world.setBlockAndUpdate(pillarPos.above(),
                NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                        .setValue(BlockInversionPillarEnd.TYPE,
                                PillarCapType.TOP));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpatialRiftBlockEntity tile) {
        if (tile.cooldown > 0) tile.cooldown--;

        if (!level.isClientSide && level instanceof ServerLevel serverLevel && level.getGameTime() % 20 == 0) {
            for (int i = 0; i < 3; i++) {
                BlockPos offset = pos.offset(
                        serverLevel.random.nextInt(8) - 4,
                        serverLevel.random.nextInt(4) - 2,
                        serverLevel.random.nextInt(8) - 4);
                StreamPresets.bloodTendril(offset, pos).build().sendToNearby(serverLevel, pos, 64);
            }
            StreamPresets.voidTendril(pos, pos.offset(
                    serverLevel.random.nextInt(6) - 3, serverLevel.random.nextInt(3), serverLevel.random.nextInt(6) - 3))
                    .build().sendToNearby(serverLevel, pos, 64);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (destinationSpawnPos != null) {
            tag.putInt("destX", destinationSpawnPos.getX());
            tag.putInt("destY", destinationSpawnPos.getY());
            tag.putInt("destZ", destinationSpawnPos.getZ());
        }
        if (destinationControllerPos != null) {
            tag.putInt("destCtrlX", destinationControllerPos.getX());
            tag.putInt("destCtrlY", destinationControllerPos.getY());
            tag.putInt("destCtrlZ", destinationControllerPos.getZ());
        }
        tag.putInt("cooldown", cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("destX")) {
            destinationSpawnPos = new BlockPos(tag.getInt("destX"), tag.getInt("destY"), tag.getInt("destZ"));
        }
        if (tag.contains("destCtrlX")) {
            destinationControllerPos = new BlockPos(tag.getInt("destCtrlX"), tag.getInt("destCtrlY"), tag.getInt("destCtrlZ"));
        }
        cooldown = tag.getInt("cooldown");
    }
}
