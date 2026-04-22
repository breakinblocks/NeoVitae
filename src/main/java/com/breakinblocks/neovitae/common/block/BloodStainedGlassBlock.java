package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BloodStainedGlassBlock extends HalfTransparentBlock {
    public BloodStainedGlassBlock(BlockBehaviour.Properties props) {
        super(props
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .lightLevel(state -> 15)
                .isViewBlocking((s, b, p) -> false)
                .isSuffocating((s, b, p) -> false));
    }

    // @Override (removed: not an override in 26.1)
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
