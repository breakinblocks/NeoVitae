package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.common.blockentity.TileInversionPillar;

import javax.annotation.Nullable;

/**
 * Inversion Pillar block - teleporter portal for the dungeon system.
 * When right-clicked, teleports the player to the configured destination.
 */
public class BlockInversionPillar extends Block implements EntityBlock {

    protected static final VoxelShape BODY = Block.box(2, 1, 2, 14, 15, 14);

    public BlockInversionPillar(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BODY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileInversionPillar(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof TileInversionPillar inversionPillar) {
                inversionPillar.handlePlayerInteraction(player);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
