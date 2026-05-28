package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.BaseBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;

public class SpikeTrapBlockEntity extends BaseBlockEntity {

    public static final int ALTAR_SEARCH_RADIUS = 16;
    public static final int ALTAR_RESCAN_COOLDOWN_TICKS = 100;
    public static final int STREAM_COOLDOWN_TICKS = 20;
    public static final double EV_MULTIPLIER = 0.325D;
    public static final double BABY_MODIFIER = 0.5D;

    @Nullable
    private BlockPos altarOffset = null;
    private long nextAltarScanTick = Long.MIN_VALUE;
    private long nextStreamTick = Long.MIN_VALUE;

    public SpikeTrapBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPIKE_TRAP_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpikeTrapBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        boolean active = state.getValue(BlockSpikeTrap.ACTIVE);
        Direction facing = state.getValue(BlockSpikeTrap.FACING);
        BlockPos spikePos = pos.relative(facing);

        if (active) {
            tile.extendSpikes(spikePos, facing);
        } else {
            tile.retractSpikes(spikePos);
        }
    }

    private void extendSpikes(BlockPos spikePos, Direction facing) {
        if (level == null) return;

        BlockState currentState = level.getBlockState(spikePos);
        if (currentState.isAir() || currentState.canBeReplaced()) {
            BlockState spikeState = DungeonBlocks.SPIKES.block().get().defaultBlockState()
                    .setValue(BlockSpikes.FACING, facing);
            level.setBlock(spikePos, spikeState, 3);
        }
    }

    private void retractSpikes(BlockPos spikePos) {
        if (level == null) return;

        BlockState currentState = level.getBlockState(spikePos);
        if (currentState.getBlock() instanceof BlockSpikes) {
            level.setBlock(spikePos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public void onSpikeKill(LivingEntity entity, float damage) {
        if (level == null || level.isClientSide) return;

        int ev = EntitySacrificeHelper.calculateEV(entity, damage);
        if (entity.isBaby()) {
            ev = (int) (ev * BABY_MODIFIER);
        }
        ev = (int) (ev * EV_MULTIPLIER);
        if (ev <= 0) return;

        AraVitaeTile altar = findAltar();
        if (altar == null) return;

        altar.addSacrificeEV(ev, true);

        if (level instanceof ServerLevel serverLevel && level.getGameTime() >= nextStreamTick) {
            BlockPos altarPos = worldPosition.offset(altarOffset);
            StreamPresets.bloodTendril(entity, altarPos)
                    .build()
                    .sendToNearby(serverLevel, altarPos, 48);
            nextStreamTick = level.getGameTime() + STREAM_COOLDOWN_TICKS;
        }
    }

    @Nullable
    private AraVitaeTile findAltar() {
        if (level == null) return null;

        if (altarOffset != null) {
            BlockPos cached = worldPosition.offset(altarOffset);
            BlockEntity be = level.getBlockEntity(cached);
            if (be instanceof AraVitaeTile altarTile) {
                return altarTile;
            }
            altarOffset = null;
            setChanged();
        }

        if (level.getGameTime() < nextAltarScanTick) {
            return null;
        }

        for (BlockPos pos : BlockPos.betweenClosed(
                worldPosition.offset(-ALTAR_SEARCH_RADIUS, -ALTAR_SEARCH_RADIUS, -ALTAR_SEARCH_RADIUS),
                worldPosition.offset(ALTAR_SEARCH_RADIUS, ALTAR_SEARCH_RADIUS, ALTAR_SEARCH_RADIUS))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AraVitaeTile altarTile) {
                altarOffset = pos.subtract(worldPosition);
                setChanged();
                return altarTile;
            }
        }

        nextAltarScanTick = level.getGameTime() + ALTAR_RESCAN_COOLDOWN_TICKS;
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (altarOffset != null) {
            tag.putInt("altarOffsetX", altarOffset.getX());
            tag.putInt("altarOffsetY", altarOffset.getY());
            tag.putInt("altarOffsetZ", altarOffset.getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("altarOffsetX")) {
            altarOffset = new BlockPos(
                    tag.getInt("altarOffsetX"),
                    tag.getInt("altarOffsetY"),
                    tag.getInt("altarOffsetZ")
            );
        } else {
            altarOffset = null;
        }
    }
}
