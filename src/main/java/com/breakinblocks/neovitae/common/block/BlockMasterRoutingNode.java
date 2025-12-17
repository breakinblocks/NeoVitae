package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeTile;

import javax.annotation.Nullable;

/**
 * Master routing node - coordinates the entire routing network.
 */
public class BlockMasterRoutingNode extends BlockRoutingNode {

    public static final MapCodec<BlockMasterRoutingNode> CODEC = simpleCodec(BlockMasterRoutingNode::new);

    public BlockMasterRoutingNode(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MasterRoutingNodeTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof MasterRoutingNodeTile tile) {
                tile.tick(lvl, pos, st);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof MasterRoutingNodeTile menuProvider) {
                serverPlayer.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
