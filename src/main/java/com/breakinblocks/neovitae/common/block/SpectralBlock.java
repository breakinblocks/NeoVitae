package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.SpectralBlockTile;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

/**
 * Spectral Block - An invisible placeholder that temporarily replaces fluids.
 * Used by the Sigil of Suppression to suppress fluids in an area.
 * When it expires, it restores the original fluid block.
 */
public class SpectralBlock extends BaseEntityBlock {

    public static final MapCodec<SpectralBlock> CODEC = simpleCodec(p -> new SpectralBlock());

    public SpectralBlock() {
        super(Properties.of()
                .noCollission()
                .noOcclusion()
                .noLootTable()
                .air()
                .replaceable());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpectralBlockTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return BlockEntityHelper.getTicker(blockEntityType, BMTiles.SPECTRAL_BLOCK_TYPE.get(), SpectralBlockTile::tick);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SpectralBlockTile spectral) {
                // If being replaced by something other than spectral, don't restore
                // This handles cases where something else placed a block here
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
