package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.ImperfectRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.RitualResult;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Imperfect Ritual Stone - a simple ritual block for one-time effects.
 * Place a specific block above it and right-click to activate.
 */
public class BlockImperfectRitualStone extends Block implements EntityBlock {

    public BlockImperfectRitualStone(BlockBehaviour.Properties props) {
        super(props
                .sound(SoundType.STONE)
                .strength(2.0F, 5.0F)
                .requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImperfectRitualStoneBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ImperfectRitualStoneBlockEntity tile)) {
            return InteractionResult.PASS;
        }

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.isAir()) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.imperfect.noBlock").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        RitualRegistry.ImperfectRitualLookupResult lookupResult = RitualRegistry.findRitualForBlock(aboveState);

        if (lookupResult == null) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.imperfect.noMatch").withStyle(ChatFormatting.YELLOW));
            return InteractionResult.FAIL;
        }

        RitualResult result = tile.performRitual(level, pos, lookupResult.ritual(), lookupResult.stats(), player);
        if (result.successful()) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.imperfect.activated",
                            Component.translatable(lookupResult.ritual().getTranslationKey())).withStyle(ChatFormatting.GREEN));
            return InteractionResult.SUCCESS;
        } else {
            Component errorMsg = result.getErrorMessage();
            if (errorMsg != null) {
                player.sendOverlayMessage(errorMsg.copy().withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }
    }

    // @Override (removed: not an override in 26.1)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.imperfectRitualStone.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.imperfectRitualStone.hint").withStyle(ChatFormatting.BLUE));}
}
