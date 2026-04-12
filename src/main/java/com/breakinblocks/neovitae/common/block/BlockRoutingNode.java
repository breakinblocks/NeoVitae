package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.api.routing.*;

import javax.annotation.Nullable;

/**
 * Shared base for all routing-node blocks (conduit, input, output, master).
 * Holds the multipart connection state and the graph-teardown hook; concrete
 * subclasses supply their own {@link #codec()} and {@link #newBlockEntity}.
 */
public abstract class BlockRoutingNode extends BaseEntityBlock {

    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    protected static final VoxelShape SHAPE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    protected BlockRoutingNode(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = super.getStateForPlacement(context);

        for (Direction dir : Direction.values()) {
            BlockPos attachedPos = pos.relative(dir);
            BlockState attachedState = level.getBlockState(attachedPos);
            BooleanProperty prop = getPropertyForDirection(dir);
            state = state.setValue(prop, canConnect(attachedState, level, attachedPos, dir));
        }

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BooleanProperty prop = getPropertyForDirection(direction);
        return state.setValue(prop, canConnect(neighborState, level, neighborPos, direction));
    }

    protected boolean canConnect(BlockState state, BlockGetter level, BlockPos neighborPos, Direction direction) {
        if (state.getBlock() instanceof BlockRoutingNode) return true;
        if (hasRoutingCapability(level, neighborPos, direction)) return true;
        return state.isFaceSturdy(level, neighborPos, direction.getOpposite());
    }

    private static boolean hasRoutingCapability(BlockGetter getter, BlockPos neighborPos, Direction dirFromNode) {
        if (!(getter instanceof Level level)) return false;
        Direction side = dirFromNode.getOpposite();
        if (level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, side) != null) return true;
        if (level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, side) != null) return true;
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, side) != null;
    }

    private BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) return;
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof IRoutingNode node && !(tile instanceof IMasterRoutingNode)) {
            RoutingLinkHelper.tryAutoBind(level, pos, node);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof IRoutingNode node) {
                node.removeAllConnections();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof RoutingNodeBlockEntity tile) {
                tile.tick(lvl, pos, st);
            }
        };
    }
}
