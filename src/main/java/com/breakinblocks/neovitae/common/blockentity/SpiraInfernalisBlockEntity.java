package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

/**
 * Spira Infernalis - pulls will from 16 blocks away in each cardinal direction.
 * Spiritus flows from areas with higher spiritus to the pylon's position.
 * Multiple pylons can be used to transfer will over larger distances.
 *
 * - Checks positions 16 blocks away in N/S/E/W directions
 * - Pulls will towards the pylon if the remote position has more
 * - Transfer rate: min((remoteAmount - localAmount) / 2, drainRate)
 * - drainRate = 1.0 per tick
 */
public class SpiraInfernalisBlockEntity extends BaseBlockEntity {

    public static final int PULL_DISTANCE = 16;
    public static final double DRAIN_RATE = 1.0;

    public SpiraInfernalisBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPIRA_INFERNALIS_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiraInfernalisBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        for (SpiritusType type : SpiritusType.values()) {
            double currentAmount = WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);

            for (int i = 0; i < 4; i++) {
                Direction side = Direction.from2DDataValue(i);
                BlockPos offsetPos = pos.relative(side, PULL_DISTANCE);

                double sideAmount = WorldSpiritusHandler.getCurrentSpiritus(level, offsetPos, type);

                if (sideAmount > currentAmount) {
                    double drainAmount = Math.min((sideAmount - currentAmount) / 2, DRAIN_RATE);

                    double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, offsetPos, type, drainAmount);
                    if (drained > 0) {
                        WorldSpiritusHandler.addSpiritusToChunk(level, pos, type, drained);
                    }
                }
            }
        }
    }
}
