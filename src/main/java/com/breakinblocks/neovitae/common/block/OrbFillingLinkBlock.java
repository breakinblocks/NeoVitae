package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.OrbFillingLinkBlockEntity;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class OrbFillingLinkBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);

    public OrbFillingLinkBlock(BlockBehaviour.Properties props) {
        super(props
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OrbFillingLinkBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockEntityHelper.getTicker(type, NVTiles.ORB_FILLING_LINK_TYPE.get(), OrbFillingLinkBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof OrbFillingLinkBlockEntity be)) {
            return InteractionResult.FAIL;
        }

        ItemStack held = be.inv.getStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT);
        if (held.isEmpty() && stack.getItem() instanceof BloodOrbItem) {
            if (!level.isClientSide()) {
                be.inv.setStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT, stack.copyWithCount(1));
                stack.shrink(1);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
            return InteractionResult.SUCCESS;
        }
        if (!held.isEmpty()) {
            if (!level.isClientSide()) {
                player.getInventory().placeItemBackInInventory(held.copy());
                be.inv.setStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT, ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction fromDirection) {
        return level.getBlockEntity(pos) instanceof OrbFillingLinkBlockEntity be ? be.getComparatorSignal() : 0;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof OrbFillingLinkBlockEntity be) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    be.inv.getStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT));
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof OrbFillingLinkBlockEntity be) || be.isLinked()) {
            return;
        }
        double cx = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        level.addParticle(ParticleTypes.SMOKE, cx, cy, cz, 0.0, 0.02, 0.0);
    }
}
