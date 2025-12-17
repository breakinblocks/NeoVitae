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
import com.breakinblocks.neovitae.common.blockentity.PhantomBridgeTile;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

/**
 * Phantom Bridge Block - A semi-transparent, solid block that players can walk on.
 * Created by the Sigil of the Phantom Bridge and the Ritual of the Phantom Bridge.
 * The block disappears after a set duration, reverting to air or the original block.
 */
public class PhantomBridgeBlock extends BaseEntityBlock {

    public static final MapCodec<PhantomBridgeBlock> CODEC = simpleCodec(p -> new PhantomBridgeBlock());

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public PhantomBridgeBlock() {
        super(Properties.of()
                .strength(-1.0F, 3600000.0F) // Unbreakable like bedrock
                .noLootTable()
                .noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PhantomBridgeTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return BlockEntityHelper.getTicker(blockEntityType, BMTiles.PHANTOM_BRIDGE_TYPE.get(), PhantomBridgeTile::tick);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            // Block is being replaced - tile entity will handle cleanup
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
