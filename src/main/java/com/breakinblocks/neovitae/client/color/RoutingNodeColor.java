package com.breakinblocks.neovitae.client.color;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import com.breakinblocks.neovitae.common.block.BlockRoutingNode;

import java.util.Set;

public class RoutingNodeColor implements BlockTintSource {

    @Override
    public int color(BlockState state) {
        if (!state.hasProperty(BlockRoutingNode.TINT)) return -1;
        return state.getValue(BlockRoutingNode.TINT).getColor();
    }

    @Override
    public Set<Property<?>> relevantProperties() {
        return Set.of(BlockRoutingNode.TINT);
    }
}
