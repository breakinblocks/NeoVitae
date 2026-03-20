package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.FungalChargeBlockEntity;

public class BlockFungalCharge extends BlockShapedExplosive {
    private final int maxBlocks;

    public BlockFungalCharge(int maxBlocks, Properties properties) {
        super(1, properties); // explosionSize not used for fungal, just pass 1
        this.maxBlocks = maxBlocks;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FungalChargeBlockEntity(maxBlocks, pos, state);
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }
}
