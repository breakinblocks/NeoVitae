package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;

public class AlchemyArrayBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<AlchemyArrayBlock> CODEC = simpleCodec(AlchemyArrayBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape BODY = Block.box(1, 0, 1, 15, 1, 15);

    public AlchemyArrayBlock() {
        super(BlockBehaviour.Properties.of().strength(1.0F, 0).noCollission().ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public AlchemyArrayBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(1.0F, 0).noCollission().ignitedByLava());
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BODY;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyArrayBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, NVTiles.ALCHEMY_ARRAY_TYPE.get(), AlchemyArrayBlockEntity::tick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof AlchemyArrayBlockEntity arrayTile) {
            arrayTile.onEntityCollidedWithBlock(state, entity);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        AlchemyArrayBlockEntity array = (AlchemyArrayBlockEntity) world.getBlockEntity(pos);

        if (array == null || player.isShiftKeyDown())
            return ItemInteractionResult.FAIL;

        ItemStack playerItem = player.getItemInHand(hand);

        if (!playerItem.isEmpty()) {
            if (array.getItem(0).isEmpty()) {
                ItemStack toInsert = playerItem.copy();
                toInsert.setCount(1);
                array.inv.setStackInSlot(0, toInsert);
                if (!player.isCreative()) {
                    playerItem.shrink(1);
                }
                world.sendBlockUpdated(pos, state, state, 3);
            } else if (array.getItem(1).isEmpty()) {
                ItemStack toInsert = playerItem.copy();
                toInsert.setCount(1);
                array.inv.setStackInSlot(1, toInsert);
                if (!player.isCreative()) {
                    playerItem.shrink(1);
                }
                array.attemptCraft();
                world.sendBlockUpdated(pos, state, state, 3);
            } else {
                return ItemInteractionResult.SUCCESS;
            }
        }

        world.sendBlockUpdated(pos, state, state, 3);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AlchemyArrayBlockEntity arrayTile) {
            arrayTile.onNeighborChanged(neighborPos);
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof AlchemyArrayBlockEntity arrayTile) {
            return arrayTile.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof AlchemyArrayBlockEntity alchemyArray) {
            alchemyArray.dropItems();
        }

        super.destroy(world, blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof AlchemyArrayBlockEntity alchemyArray) {
                alchemyArray.dropItems();
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }
}
