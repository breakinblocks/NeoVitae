package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.ARCTile;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class ARCBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<EnumWillType> TYPE = EnumProperty.create("type", EnumWillType.class);

    public ARCBlock() {
        super(Properties.ofFullCopy(Blocks.FURNACE));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof ARCTile arc) {
                BlockEntityHelper.dropContents(level, pos, arc.arcInv);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LIT, false).setValue(TYPE, EnumWillType.DEFAULT).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, TYPE);
    }

    /**
     * Handle fluid container interaction with ARC tanks.
     * Priority: Fill input tank > Empty output tank > Empty input tank > Open GUI
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ARCTile arc)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Check if held item is a fluid container
        if (!FluidUtil.getFluidHandler(stack).isPresent()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Try to fill input tank from held container
        FluidActionResult fillResult = FluidUtil.tryEmptyContainerAndStow(
                stack, arc.inputTank, null, Integer.MAX_VALUE, player, true);
        if (fillResult.isSuccess()) {
            player.setItemInHand(hand, fillResult.getResult());
            playFluidSound(level, pos, arc.inputTank, true);
            return ItemInteractionResult.SUCCESS;
        }

        // Try to empty output tank into held container
        FluidActionResult drainOutputResult = FluidUtil.tryFillContainerAndStow(
                stack, arc.outputTank, null, Integer.MAX_VALUE, player, true);
        if (drainOutputResult.isSuccess()) {
            player.setItemInHand(hand, drainOutputResult.getResult());
            playFluidSound(level, pos, arc.outputTank, false);
            return ItemInteractionResult.SUCCESS;
        }

        // Try to empty input tank into held container
        FluidActionResult drainInputResult = FluidUtil.tryFillContainerAndStow(
                stack, arc.inputTank, null, Integer.MAX_VALUE, player, true);
        if (drainInputResult.isSuccess()) {
            player.setItemInHand(hand, drainInputResult.getResult());
            playFluidSound(level, pos, arc.inputTank, false);
            return ItemInteractionResult.SUCCESS;
        }

        // No fluid interaction possible, fall through to open GUI
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void playFluidSound(Level level, BlockPos pos, IFluidHandler tank, boolean fill) {
        if (tank.getFluidInTank(0).isEmpty()) return;

        net.neoforged.neoforge.fluids.FluidStack fluid = tank.getFluidInTank(0);
        SoundEvent sound = fill
                ? fluid.getFluidType().getSound(fluid, net.neoforged.neoforge.common.SoundActions.BUCKET_FILL)
                : fluid.getFluidType().getSound(fluid, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);

        if (sound != null) {
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos), buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity BE = level.getBlockEntity(pos);
        if (!(BE instanceof ARCTile tile)) {
            return null;
        }
        return tile;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, BMTiles.ARC_TYPE.get(), ARCTile::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ARCTile(pos, state);
    }
}
