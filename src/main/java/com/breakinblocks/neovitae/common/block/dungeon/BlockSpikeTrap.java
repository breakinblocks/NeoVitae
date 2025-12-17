package com.breakinblocks.neovitae.common.block.dungeon;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;

/**
 * Spike Trap - extends/retracts a spike block when powered by redstone.
 * Placed in a direction and shoots spikes out that direction when powered.
 */
public class BlockSpikeTrap extends BaseEntityBlock {
    public static final MapCodec<BlockSpikeTrap> CODEC = simpleCodec(BlockSpikeTrap::new);

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public BlockSpikeTrap(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered != state.getValue(ACTIVE)) {
                level.setBlock(pos, state.setValue(ACTIVE, powered), Block.UPDATE_ALL);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileSpikeTrap(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, BMTiles.SPIKE_TRAP_TYPE.get(), TileSpikeTrap::tick);
    }
}
