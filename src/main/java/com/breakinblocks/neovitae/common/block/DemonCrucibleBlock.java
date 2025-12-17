package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.DemonCrucibleTile;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

/**
 * Demon Crucible - burns demon will items to release will into the chunk aura.
 */
public class DemonCrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<DemonCrucibleBlock> CODEC = simpleCodec(p -> new DemonCrucibleBlock());

    public DemonCrucibleBlock() {
        super(Properties.of()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DemonCrucibleTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, BMTiles.DEMON_CRUCIBLE_TYPE.get(), DemonCrucibleTile::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DemonCrucibleTile crucible) {
            ItemStack heldItem = player.getItemInHand(hand);

            if (heldItem.isEmpty()) {
                // Extract item
                ItemStack contained = crucible.getInventory().getStackInSlot(0);
                if (!contained.isEmpty()) {
                    ItemStack extracted = crucible.getInventory().extractItem(0, 1, false);
                    if (!player.getInventory().add(extracted)) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, extracted);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            } else {
                // Try to insert item
                if (crucible.getInventory().isItemValid(0, heldItem)) {
                    ItemStack remainder = crucible.getInventory().insertItem(0, heldItem.copy(), false);
                    if (remainder.getCount() < heldItem.getCount()) {
                        heldItem.setCount(remainder.getCount());
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DemonCrucibleTile crucible) {
                ItemStack stack = crucible.getInventory().getStackInSlot(0);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
