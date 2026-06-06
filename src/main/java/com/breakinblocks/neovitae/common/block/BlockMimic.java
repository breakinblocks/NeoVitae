// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.common.blockentity.MimicBlockEntity;

public class BlockMimic extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0.01, 0, 0.01, 0.99, 1, 0.99);

    public BlockMimic(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        BlockEntity te = reader.getBlockEntity(pos);
        if (te instanceof MimicBlockEntity mimic) {
            BlockState mimicState = mimic.getMimic();
            if (mimicState != null && !(mimicState.getBlock() instanceof BlockMimic)) {
                return mimicState.getShape(reader, pos, context);
            }
        }
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MimicBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MimicBlockEntity mimic) {
            if (mimic.onBlockActivated(world, pos, state, player, hand, stack, hitResult.getDirection())) {
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MimicBlockEntity mimic) {
            if (mimic.onBlockActivated(world, pos, state, player, InteractionHand.MAIN_HAND,
                    player.getItemInHand(InteractionHand.MAIN_HAND), hitResult.getDirection())) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof MimicBlockEntity mimic && !level.isClientSide) {
            mimic.dropItems();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
