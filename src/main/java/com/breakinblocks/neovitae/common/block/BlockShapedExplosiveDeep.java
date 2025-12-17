package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.ShapedExplosiveTile;

/**
 * Block for deep shaped explosive charges.
 * Similar to regular shaped charges but with deeper penetration (3x9 instead of 3x7).
 */
public class BlockShapedExplosiveDeep extends BlockShapedExplosive {

    public BlockShapedExplosiveDeep(int explosionSize, Properties properties) {
        super(explosionSize, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Deep version: same radius but depth is explosionSize * 3 instead of * 2 + 1
        return new ShapedExplosiveTile(explosionSize, explosionSize * 3, pos, state);
    }
}
