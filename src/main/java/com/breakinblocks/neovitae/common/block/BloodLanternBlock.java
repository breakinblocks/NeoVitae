package com.breakinblocks.neovitae.common.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloodLanternBlock extends LanternBlock {

    public static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_LANTERN);
    }

    public BloodLanternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
