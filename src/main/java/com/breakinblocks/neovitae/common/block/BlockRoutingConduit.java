package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingConduitBlockEntity;

import javax.annotation.Nullable;

/**
 * Concrete routing conduit block. The conduit is a graph-edge relay: it has
 * no menu, no inventory and no filters, it simply lets Input, Output and
 * Master routing nodes discover each other across distance.
 *
 * <p>Extends {@link BlockRoutingNode} to inherit the shared connection state
 * and multipart blockstate behaviour; only the block-entity factory differs.
 */
public class BlockRoutingConduit extends BlockRoutingNode {

    public static final MapCodec<BlockRoutingConduit> CODEC = simpleCodec(BlockRoutingConduit::new);

    public BlockRoutingConduit(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoutingConduitBlockEntity(pos, state);
    }
}
