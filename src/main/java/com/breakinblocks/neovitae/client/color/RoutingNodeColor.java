package com.breakinblocks.neovitae.client.color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.block.BlockRoutingNode;
import com.breakinblocks.neovitae.common.routing.RoutingTint;

public class RoutingNodeColor implements BlockColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0 || !state.hasProperty(BlockRoutingNode.TINT)) return -1;
        return state.getValue(BlockRoutingNode.TINT).getColor();
    }
}
