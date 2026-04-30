package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import java.util.EnumMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class AthanorBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<SpiritusType> TYPE = EnumProperty.create("type", SpiritusType.class);

    public AthanorBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    private static final VoxelShape MAIN_BODY = Block.box(0, 0, 0, 16, 21, 16);
    private static final VoxelShape PROTRUSION_AT_EAST = Block.box(14, 10, 5.5, 18, 25, 9.5);
    private static final VoxelShape PROTRUSION_AT_SOUTH = Block.box(6.5, 10, 14, 10.5, 25, 18);
    private static final VoxelShape PROTRUSION_AT_WEST = Block.box(-2, 10, 6.5, 2, 25, 10.5);
    private static final VoxelShape PROTRUSION_AT_NORTH = Block.box(5.5, 10, -2, 9.5, 25, 2);

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);
    static {
        SHAPES.put(Direction.NORTH, Shapes.or(MAIN_BODY, PROTRUSION_AT_EAST));
        SHAPES.put(Direction.EAST, Shapes.or(MAIN_BODY, PROTRUSION_AT_SOUTH));
        SHAPES.put(Direction.SOUTH, Shapes.or(MAIN_BODY, PROTRUSION_AT_WEST));
        SHAPES.put(Direction.WEST, Shapes.or(MAIN_BODY, PROTRUSION_AT_NORTH));
    }

    private static final VoxelShape OCCLUSION_SHAPE = Block.box(0, 2, 0, 16, 16, 16);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), MAIN_BODY);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return OCCLUSION_SHAPE;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof AthanorBlockEntity arc) {
            BlockEntityHelper.dropContents(level, pos, arc.athanorInv);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LIT, false).setValue(TYPE, SpiritusType.RAW).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, TYPE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AthanorBlockEntity arc)) {
            return InteractionResult.PASS;
        }

        if (FluidUtil.interactWithFluidHandler(player, hand, pos, arc.inputTank)) {
            return InteractionResult.SUCCESS;
        }
        if (FluidUtil.interactWithFluidHandler(player, hand, pos, arc.outputTank)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos), buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity BE = level.getBlockEntity(pos);
        if (!(BE instanceof AthanorBlockEntity tile)) {
            return null;
        }
        return tile;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, NVTiles.ATHANOR_TYPE.get(), AthanorBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AthanorBlockEntity(pos, state);
    }
}
