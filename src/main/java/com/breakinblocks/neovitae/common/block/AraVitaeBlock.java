package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class AraVitaeBlock extends Block implements EntityBlock {
    public AraVitaeBlock(BlockBehaviour.Properties props) {
        super(props
                .forceSolidOn()
                .requiresCorrectToolForDrops()
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE));
    }

    public static final VoxelShape BOX = box(0, 0, 0, 16, 12, 16);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOX;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (true) {
            if (level.getBlockEntity(pos) instanceof AraVitaeTile tile) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), tile.inv.getStackInSlot(0));
    }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction fromDirection) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof AraVitaeTile altar)) {
            return 0;
        }
        return altar.analogSignal();
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof AraVitaeTile altar)) {
            return 0;
        }
        return altar.isSignaling() ? 15 : 0;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AraVitaeTile(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, NVTiles.ARA_VITAE_TYPE.get(), AraVitaeTile::tick);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.CONSUME;
        }

        // Let sigils handle their own interaction - don't place them into the altar
        if (stack.getItem() instanceof com.breakinblocks.neovitae.common.item.sigil.ISigil) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AraVitaeTile tile)) {
            return InteractionResult.FAIL;
        }
        ItemStack altarStack = tile.inv.getStackInSlot(0);
        if (altarStack.isEmpty() && !stack.isEmpty()) {
            ItemStack toPlace = stack.copyWithCount(1);
            tile.inv.setStackInSlot(0, toPlace);
            stack.shrink(1);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        } else if (!altarStack.isEmpty() && stack.isEmpty()) {
            player.setItemInHand(hand, altarStack.copy());
            tile.inv.setStackInSlot(0, ItemStack.EMPTY);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
