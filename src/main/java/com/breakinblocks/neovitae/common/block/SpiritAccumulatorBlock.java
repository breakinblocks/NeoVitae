package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;
import com.breakinblocks.neovitae.api.routing.IRoutingNodeHost;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.SpiritusCrystalItem;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

public class SpiritAccumulatorBlock extends BaseEntityBlock implements IRoutingNodeHost {

    public static final MapCodec<SpiritAccumulatorBlock> CODEC = simpleCodec(p -> new SpiritAccumulatorBlock());

    private static final double VENT_AMOUNT = 50.0;

    private static final VoxelShape SHAPE = Shapes.or(
            box(7.2, 2.8, 7.2, 8.8, 5, 8.8),
            box(6.2, 5, 6.2, 9.8, 7, 9.8),
            box(4.8, 7, 4.8, 11.2, 10.6, 11.2),
            box(6.2, 10.6, 6.2, 9.8, 12.6, 9.8),
            box(7.2, 12.6, 7.2, 8.8, 14.4, 8.8));

    public SpiritAccumulatorBlock() {
        super(Properties.of()
                .strength(1.5F, 4.0F)
                .sound(SoundType.AMETHYST)
                .noOcclusion()
                .lightLevel(state -> 6));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpiritAccumulatorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, NVTiles.SPIRIT_ACCUMULATOR_TYPE.get(), SpiritAccumulatorBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof IRoutingNode node) {
            RoutingLinkHelper.tryAutoBind(level, pos, node);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof IRoutingNode node) {
                node.removeAllConnections();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SpiritAccumulatorBlockEntity accumulator)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            return toggleLock(level, pos, player, accumulator);
        }

        if (accumulator.isLocked()) {
            sendAttunement(player, accumulator.getAttunedType(), "chat.neovitae.spirit_accumulator.locked");
            return InteractionResult.SUCCESS;
        }

        SpiritusType cycled = accumulator.cycleAttunement();
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.5f, cycled == null ? 0.8f : 1.2f);
        if (cycled == null) {
            player.displayClientMessage(Component.translatable("chat.neovitae.spirit_accumulator.unattuned"), true);
        } else {
            sendAttunement(player, cycled, "chat.neovitae.spirit_accumulator.attuned");
        }
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult toggleLock(Level level, BlockPos pos, Player player, SpiritAccumulatorBlockEntity accumulator) {
        if (!accumulator.isLocked()) {
            if (!accumulator.lock()) {
                player.displayClientMessage(Component.translatable("chat.neovitae.spirit_accumulator.select_first"), true);
                return InteractionResult.SUCCESS;
            }
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.6f, 1.2f);
            sendAttunement(player, accumulator.getAttunedType(), "chat.neovitae.spirit_accumulator.locked");
            return InteractionResult.SUCCESS;
        }

        if (accumulator.unlock()) {
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.6f, 0.8f);
            player.displayClientMessage(Component.translatable("chat.neovitae.spirit_accumulator.unlocked"), true);
            return InteractionResult.SUCCESS;
        }

        if (accumulator.vent(VENT_AMOUNT) > 0) {
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.6f, 0.8f);
            player.displayClientMessage(Component.translatable("chat.neovitae.spirit_accumulator.venting"), true);
        }
        return InteractionResult.SUCCESS;
    }

    private static void sendAttunement(Player player, @Nullable SpiritusType type, String key) {
        if (type == null) return;
        player.displayClientMessage(Component.translatable(key,
                Component.translatable("tooltip.neovitae.spiritus." + type.getSerializedName())
                        .withColor(SpiritusTooltipHelper.spiritusColor(type))), true);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SpiritAccumulatorBlockEntity accumulator)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(stack.getItem() instanceof SpiritusCrystalItem crystal)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        SpiritusType type = crystal.getSpiritusType();
        if (accumulator.attuneTo(type)) {
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.6f, 1.2f);
            sendAttunement(player, type, "chat.neovitae.spirit_accumulator.locked");
        } else {
            sendAttunement(player, accumulator.getAttunedType(), "chat.neovitae.spirit_accumulator.locked");
        }
        return ItemInteractionResult.SUCCESS;
    }
}
