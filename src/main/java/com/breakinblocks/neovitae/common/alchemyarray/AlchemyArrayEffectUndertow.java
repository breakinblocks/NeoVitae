package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;

public class AlchemyArrayEffectUndertow extends AlchemyArrayEffect {

    private static final int REFRESH_INTERVAL = 20;

    private boolean dragDown = false;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        if (ticksActive % REFRESH_INTERVAL != 0) return false;

        refreshColumn(tile);
        return false;
    }

    private void refreshColumn(AlchemyArrayBlockEntity tile) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return;

        BlockPos above = tile.getBlockPos().above();
        BlockState aboveState = level.getBlockState(above);
        if (!canTarget(aboveState)) return;

        BlockState bubbleState = Blocks.BUBBLE_COLUMN.defaultBlockState()
                .setValue(BubbleColumnBlock.DRAG_DOWN, dragDown);
        BubbleColumnBlock.updateColumn(level, above, bubbleState);
    }

    private static boolean canTarget(BlockState state) {
        if (state.is(Blocks.BUBBLE_COLUMN)) return true;
        return state.is(Blocks.WATER)
                && state.getFluidState().isSource()
                && state.getFluidState().getAmount() >= 8;
    }

    @Override
    public boolean onUse(AlchemyArrayBlockEntity tile, Player player) {
        dragDown = !dragDown;
        refreshColumn(tile);
        Level level = tile.getLevel();
        if (level != null && !level.isClientSide) {
            BlockPos pos = tile.getBlockPos();
            level.playSound(null, pos,
                    dragDown ? SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT : SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS, 0.7f, 1.0f);
            player.displayClientMessage(Component.translatable(
                    dragDown ? "chat.neovitae.undertow.downward" : "chat.neovitae.undertow.upward"), true);
        }
        return true;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putBoolean("dragDown", dragDown);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        dragDown = tag.getBoolean("dragDown");
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectUndertow();
    }
}
