package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.BaseTile;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;

/**
 * Tile entity for the Spike Trap block.
 * Extends and retracts spike blocks based on redstone signal.
 */
public class TileSpikeTrap extends BaseTile {

    public TileSpikeTrap(BlockPos pos, BlockState state) {
        super(BMTiles.SPIKE_TRAP_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileSpikeTrap tile) {
        if (level.isClientSide) {
            return;
        }

        boolean active = state.getValue(BlockSpikeTrap.ACTIVE);
        Direction facing = state.getValue(BlockSpikeTrap.FACING);
        BlockPos spikePos = pos.relative(facing);

        if (active) {
            // Extend spikes
            tile.extendSpikes(spikePos, facing);
        } else {
            // Retract spikes
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
}
