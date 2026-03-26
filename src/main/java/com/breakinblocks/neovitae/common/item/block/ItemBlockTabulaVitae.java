package com.breakinblocks.neovitae.common.item.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.TabulaVitaeBlock;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;

public class ItemBlockTabulaVitae extends BlockItem {
    public ItemBlockTabulaVitae(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        Player player = context.getPlayer();

        if (direction.getStepY() != 0) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!world.isEmptyBlock(pos.relative(direction))) {
            return InteractionResult.FAIL;
        }

        BlockState thisState = this.getBlock().defaultBlockState().setValue(TabulaVitaeBlock.DIRECTION, direction).setValue(TabulaVitaeBlock.INVISIBLE, false);
        BlockState newState = this.getBlock().defaultBlockState().setValue(TabulaVitaeBlock.DIRECTION, direction).setValue(TabulaVitaeBlock.INVISIBLE, true);

        if (!this.canPlace(context, thisState) || !world.setBlock(pos.relative(direction), newState, 3)) {
            return InteractionResult.FAIL;
        }

        if (!world.setBlock(pos, thisState, 3)) {
            return InteractionResult.FAIL;
        }

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.getBlock()) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TabulaVitaeBlockEntity) {
                ((TabulaVitaeBlockEntity) tile).setInitialTableParameters(direction, false, pos.relative(direction));
            }

            BlockEntity slaveTile = world.getBlockEntity(pos.relative(direction));
            if (slaveTile instanceof TabulaVitaeBlockEntity) {
                ((TabulaVitaeBlockEntity) slaveTile).setInitialTableParameters(direction, true, pos);
            }

            updateCustomBlockEntityTag(world, context.getPlayer(), pos, context.getItemInHand());
            this.getBlock().setPlacedBy(world, pos, state, context.getPlayer(), context.getItemInHand());
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, pos, context.getItemInHand());
            }
        }

        SoundType soundtype = state.getSoundType(world, pos, context.getPlayer());
        world.playSound(player, pos, this.getPlaceSound(state, world, pos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
