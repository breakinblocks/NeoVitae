package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.DemonCrystalTile;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.DemonCrystalItem;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

/**
 * Demon Crystal block - grows from will aura.
 * AGE property (0-6) represents the number of crystals shown (1-7).
 * ATTACHED property indicates which direction the crystal attaches to.
 */
public class BlockDemonCrystal extends BaseEntityBlock {
    public static final MapCodec<BlockDemonCrystal> CODEC = simpleCodec(p -> new BlockDemonCrystal(EnumWillType.DEFAULT, p));

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);
    public static final EnumProperty<Direction> ATTACHED = EnumProperty.create("attached", Direction.class);

    public final EnumWillType willType;

    // VoxelShapes for different attachment directions - simplified to a single shape per direction
    private static final VoxelShape SHAPE_UP = Block.box(2, 0, 2, 14, 14, 14);
    private static final VoxelShape SHAPE_DOWN = Block.box(2, 2, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_NORTH = Block.box(2, 2, 3, 14, 14, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(2, 2, 0, 14, 14, 13);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 2, 2, 13, 14, 14);
    private static final VoxelShape SHAPE_WEST = Block.box(3, 2, 2, 16, 14, 14);

    public BlockDemonCrystal(EnumWillType willType, Properties properties) {
        super(properties);
        this.willType = willType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ATTACHED, Direction.UP)
                .setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ATTACHED, AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(ATTACHED)) {
            case DOWN -> SHAPE_DOWN;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_UP;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction attached = state.getValue(ATTACHED);
        BlockPos attachedPos = pos.relative(attached.getOpposite());
        BlockState attachedState = level.getBlockState(attachedPos);
        return attachedState.isFaceSturdy(level, attachedPos, attached);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] directions = context.getNearestLookingDirections();

        for (Direction dir : directions) {
            Direction attachDir = dir.getOpposite();
            BlockState state = this.defaultBlockState().setValue(ATTACHED, attachDir);
            if (state.canSurvive(level, pos)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                   LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        Direction attached = state.getValue(ATTACHED);
        if (facing.getOpposite() == attached && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DemonCrystalTile crystal)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        boolean isCreative = player.isCreative();
        boolean holdsCrystal = stack.getItem() instanceof DemonCrystalItem;

        // Creative players holding crystals can add crystals
        if (isCreative && holdsCrystal) {
            if (crystal.getCrystalCount() < 7) {
                crystal.internalCounter = 0;
                if (crystal.progressToNextCrystal > 0) {
                    crystal.progressToNextCrystal--;
                }
                crystal.setCrystalCount(crystal.getCrystalCount() + 1);
                crystal.setChanged();
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Players with sufficient will can extract crystals
        double playerWill = PlayerDemonWillHandler.getTotalDemonWill(
                PlayerDemonWillHandler.getLargestWillType(player), player);
        if (playerWill > 512) {
            if (crystal.dropSingleCrystal()) {
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DemonCrystalTile(willType, pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, BMTiles.DEMON_CRYSTAL_TYPE.get(), DemonCrystalTile::tick);
    }

    /**
     * Get the item dropped when a crystal is harvested.
     */
    public static ItemStack getItemStackDropped(EnumWillType type, int count) {
        ItemStack stack = switch (type) {
            case CORROSIVE -> new ItemStack(BMItems.CORROSIVE_CRYSTAL.get());
            case DESTRUCTIVE -> new ItemStack(BMItems.DESTRUCTIVE_CRYSTAL.get());
            case VENGEFUL -> new ItemStack(BMItems.VENGEFUL_CRYSTAL.get());
            case STEADFAST -> new ItemStack(BMItems.STEADFAST_CRYSTAL.get());
            default -> new ItemStack(BMItems.RAW_CRYSTAL.get());
        };
        stack.setCount(count);
        return stack;
    }

    public EnumWillType getWillType() {
        return willType;
    }
}
