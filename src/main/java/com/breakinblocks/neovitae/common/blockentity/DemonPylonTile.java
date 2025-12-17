package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

/**
 * Demon Pylon - pulls will from 16 blocks away in each cardinal direction.
 * Will flows from areas with higher will to the pylon's position.
 * Multiple pylons can be used to transfer will over larger distances.
 *
 * - Checks positions 16 blocks away in N/S/E/W directions
 * - Pulls will towards the pylon if the remote position has more
 * - Transfer rate: min((remoteAmount - localAmount) / 2, drainRate)
 * - drainRate = 1.0 per tick
 */
public class DemonPylonTile extends BaseTile {

    public static final int PULL_DISTANCE = 16;
    public static final double DRAIN_RATE = 1.0;

    public DemonPylonTile(BlockPos pos, BlockState state) {
        super(BMTiles.DEMON_PYLON_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DemonPylonTile tile) {
        if (level.isClientSide()) {
            return;
        }

        // For each will type, check all 4 cardinal directions
        for (EnumWillType type : EnumWillType.values()) {
            double currentAmount = WorldDemonWillHandler.getCurrentWill(level, pos, type);

            // Check each cardinal direction (N, S, E, W)
            for (int i = 0; i < 4; i++) {
                Direction side = Direction.from2DDataValue(i);
                BlockPos offsetPos = pos.relative(side, PULL_DISTANCE);

                double sideAmount = WorldDemonWillHandler.getCurrentWill(level, offsetPos, type);

                // Only pull will if the remote position has more than our position
                if (sideAmount > currentAmount) {
                    // Calculate transfer amount: half the difference, capped by drain rate
                    double drainAmount = Math.min((sideAmount - currentAmount) / 2, DRAIN_RATE);

                    // Drain from remote position and fill at pylon position
                    double drained = WorldDemonWillHandler.drainWillFromChunk(level, offsetPos, type, drainAmount);
                    if (drained > 0) {
                        WorldDemonWillHandler.addWillToChunk(level, pos, type, drained);
                    }
                }
            }
        }
    }
}
