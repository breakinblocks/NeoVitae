package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class VitaeLinkBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);

    public VitaeLinkBlock(BlockBehaviour.Properties props) {
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
        return new VitaeLinkBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return BlockEntityHelper.getTicker(type, NVTiles.VITAE_LINK_TYPE.get(), VitaeLinkBlockEntity::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof VitaeLinkBlockEntity be)) {
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                boolean changed = be.cycleCraftTier();
                if (player instanceof ServerPlayer sp) {
                    Component msg = changed
                            ? Component.translatable("message.neovitae.vitae_link.tier",
                                    be.getCraftTier(), Math.max(0, be.getMaxLinkTier()))
                            : Component.translatable("message.neovitae.vitae_link.locked");
                    sp.connection.send(new ClientboundSetActionBarTextPacket(msg));
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (be.isClientCrafting()) {
            if (!level.isClientSide()) {
                be.cancelCraft(player);
            }
            return InteractionResult.SUCCESS;
        }

        if (!be.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT).isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack out = be.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
                player.getInventory().placeItemBackInInventory(out.copy());
                be.inv.setStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }

        if (!stack.isEmpty()) {
            if (!be.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
                return InteractionResult.CONSUME;
            }
            if (!level.isClientSide()) {
                be.inv.setStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT, stack.copyWithCount(1));
                stack.shrink(1);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
            return InteractionResult.SUCCESS;
        }

        if (!be.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT).isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack in = be.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT);
                player.getInventory().placeItemBackInInventory(in.copy());
                be.inv.setStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT, ItemStack.EMPTY);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
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
