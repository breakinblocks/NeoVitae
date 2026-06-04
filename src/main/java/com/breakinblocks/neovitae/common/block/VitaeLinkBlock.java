package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class VitaeLinkBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);

    public VitaeLinkBlock() {
        super(BlockBehaviour.Properties.of()
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VitaeLinkBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockEntityHelper.getTicker(type, NVTiles.VITAE_LINK_TYPE.get(), VitaeLinkBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof VitaeLinkBlockEntity be)) {
            return ItemInteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (be.cycleCraftTier()) {
                    player.displayClientMessage(Component.translatable("message.neovitae.vitae_link.tier",
                            be.getCraftTier(), Math.max(0, be.getMaxLinkTier())), true);
                } else {
                    player.displayClientMessage(Component.translatable("message.neovitae.vitae_link.locked"), true);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (be.isClientCrafting()) {
            if (!level.isClientSide) {
                be.cancelCraft(player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!be.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT).isEmpty()) {
            if (!level.isClientSide) {
                ItemStack out = be.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
                player.getInventory().placeItemBackInInventory(out.copy());
                be.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!stack.isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.sidedSuccess(true);
            }
            ItemStack toInsert = stack.copy();
            ItemStack remaining = be.inv.insertItem(VitaeLinkBlockEntity.INPUT_SLOT, toInsert, false);
            int inserted = toInsert.getCount() - remaining.getCount();
            if (inserted <= 0) {
                return ItemInteractionResult.CONSUME;
            }
            stack.shrink(inserted);
            return ItemInteractionResult.sidedSuccess(false);
        }

        if (!be.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
            if (!level.isClientSide) {
                ItemStack in = be.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT);
                player.getInventory().placeItemBackInInventory(in.copy());
                be.inv.setStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT, ItemStack.EMPTY);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof VitaeLinkBlockEntity be) {
                BlockEntityHelper.dropContents(level, pos, be.inv);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof VitaeLinkBlockEntity be) || be.isLinked()) {
            return;
        }
        double cx = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        level.addParticle(ParticleTypes.SMOKE, cx, cy, cz, 0.0, 0.02, 0.0);
    }
}
