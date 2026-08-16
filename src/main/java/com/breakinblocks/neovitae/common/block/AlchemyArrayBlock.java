package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectLight;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.redstone.Orientation;

public class AlchemyArrayBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<AlchemyArrayBlock> CODEC = simpleCodec(AlchemyArrayBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape BODY = Block.box(1, 0, 1, 15, 1, 15);

    public AlchemyArrayBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(1.0F, 0).noCollision().ignitedByLava());
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
    protected BlockState updateShape(BlockState state, LevelReader level,
                                     ScheduledTickAccess tickAccess, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState,
                                     RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
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
        // 26.1: ENTITYBLOCK_ANIMATED removed; renderer is attached via BlockEntityRenderer only.
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier applier, boolean intersects) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof AlchemyArrayBlockEntity arrayTile) {
            arrayTile.onEntityCollidedWithBlock(state, entity);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        AlchemyArrayBlockEntity array = (AlchemyArrayBlockEntity) world.getBlockEntity(pos);

        if (array == null || player.isShiftKeyDown())
            return InteractionResult.FAIL;

        ItemStack playerItem = player.getItemInHand(hand);
        if (playerItem.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (playerItem.is(Items.GLOWSTONE) && array.arrayEffect instanceof AlchemyArrayEffectLight lightEffect) {
            if (world.isClientSide()) return InteractionResult.SUCCESS;
            if (lightEffect.isPersistent()) {
                player.sendOverlayMessage(Component.translatable("chat.neovitae.light_array.already_persistent"));
                return InteractionResult.SUCCESS;
            }
            lightEffect.setPersistent(true);
            playerItem.consume(1, player);
            array.setChanged();
            world.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.6f, 1.4f);
            player.sendOverlayMessage(Component.translatable("chat.neovitae.light_array.persistent"));
            return InteractionResult.SUCCESS;
        }

        for (int slot = 0; slot < 2; slot++) {
            if (array.getItem(slot).isEmpty()) {
                ItemStack toInsert = playerItem.copy();
                toInsert.setCount(1);
                array.inv.setStackInSlot(slot, toInsert);
                if (!player.isCreative()) playerItem.shrink(1);
                if (slot == 1) array.attemptCraft();
                world.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
        }

        for (int slot = 0; slot < 2; slot++) {
            ItemStack inSlot = array.getItem(slot);
            if (ItemStack.isSameItemSameComponents(inSlot, playerItem)
                    && inSlot.getCount() < inSlot.getMaxStackSize()) {
                inSlot.grow(1);
                array.inv.setStackInSlot(slot, inSlot);
                if (!player.isCreative()) playerItem.shrink(1);
                array.attemptCraft();
                world.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof AlchemyArrayBlockEntity array && array.arrayEffect != null) {
            boolean handled = array.arrayEffect.onUse(array, player);
            if (handled && !world.isClientSide()) {
                world.sendBlockUpdated(pos, state, state, 3);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   @Nullable Orientation orientation,
                                   boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AlchemyArrayBlockEntity arrayTile) {
            arrayTile.onNeighborChanged(pos);
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
}
