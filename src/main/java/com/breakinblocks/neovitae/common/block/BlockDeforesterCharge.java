package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.DeforesterChargeBlockEntity;

public class BlockDeforesterCharge extends BlockShapedExplosive {
    private final int maxLogs;

    public BlockDeforesterCharge(int maxLogs, Properties properties) {
        super(1, properties); // explosionSize not used for deforester, just pass 1
        this.maxLogs = maxLogs;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeforesterChargeBlockEntity(maxLogs, pos, state);
    }

    public int getMaxLogs() {
        return maxLogs;
    }
}
