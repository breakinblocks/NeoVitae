package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeTile;

import javax.annotation.Nullable;

/**
 * Input routing node - pulls items from adjacent inventories.
 */
public class BlockInputRoutingNode extends BlockRoutingNode {

    public static final MapCodec<BlockInputRoutingNode> CODEC = simpleCodec(BlockInputRoutingNode::new);

    public BlockInputRoutingNode(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InputRoutingNodeTile(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof InputRoutingNodeTile menuProvider) {
                serverPlayer.openMenu(menuProvider, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
