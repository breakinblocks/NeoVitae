package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;

public class TabulaVitaeBlock extends BaseEntityBlock {
    public static final MapCodec<TabulaVitaeBlock> CODEC = simpleCodec(TabulaVitaeBlock::new);
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class, Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new));
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    protected static final VoxelShape BODY = Block.box(1, 0, 1, 15, 15, 15);

    public TabulaVitaeBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(2.0F, 5.0F).noOcclusion().isRedstoneConductor(TabulaVitaeBlock::isntSolid).isViewBlocking(TabulaVitaeBlock::isntSolid).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(INVISIBLE, false));
    }

    private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BODY;
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TabulaVitaeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (state.getValue(INVISIBLE)) {
            return null;
        }
        return createTickerHelper(type, NVTiles.TABULA_VITAE_TYPE.get(), TabulaVitaeBlockEntity::tick);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.FAIL;
        }

        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TabulaVitaeBlockEntity tableTile) {
            if (tableTile.isSlave()) {
                BlockPos masterPos = tableTile.getConnectedPos();
                BlockEntity masterTile = world.getBlockEntity(masterPos);
                if (masterTile instanceof TabulaVitaeBlockEntity masterTable) {
                    serverPlayer.openMenu(masterTable, buf -> buf.writeBlockPos(masterPos));
                }
            } else {
                serverPlayer.openMenu(tableTile, buf -> buf.writeBlockPos(pos));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION, INVISIBLE);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        TabulaVitaeBlockEntity tile = (TabulaVitaeBlockEntity) world.getBlockEntity(blockPos);
        if (tile != null) {
            tile.dropItems();
        }
        super.destroy(world, blockPos, blockState);
    }

    private static BlockPos partnerPos(BlockState state, BlockPos pos) {
        Direction dir = state.getValue(DIRECTION);
        return state.getValue(INVISIBLE) ? pos.relative(dir.getOpposite()) : pos.relative(dir);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel worldIn, BlockPos pos, boolean isMoving) {
        BlockPos partner = partnerPos(state, pos);
        if (worldIn.getBlockState(partner).is(this)) {
            worldIn.setBlock(partner, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }
        super.affectNeighborsAfterRemoval(state, worldIn, pos, isMoving);
    }
}
