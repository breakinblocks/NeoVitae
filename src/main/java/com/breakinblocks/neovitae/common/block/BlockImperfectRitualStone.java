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
import com.breakinblocks.neovitae.common.blockentity.TileImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.RitualResult;

import java.util.List;

/**
 * Imperfect Ritual Stone - a simple ritual block for one-time effects.
 * Place a specific block above it and right-click to activate.
 */
public class BlockImperfectRitualStone extends Block implements EntityBlock {

    public BlockImperfectRitualStone() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(2.0F, 5.0F)
                .requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileImperfectRitualStone(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TileImperfectRitualStone tile)) {
            return InteractionResult.PASS;
        }

        // Check for a block above
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.isAir()) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.imperfect.noBlock").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        // Find matching imperfect ritual using O(1) lookup cache
        RitualRegistry.ImperfectRitualLookupResult lookupResult = RitualRegistry.findRitualForBlock(aboveState);

        if (lookupResult == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.imperfect.noMatch").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.FAIL;
        }

        // Try to perform the ritual
        RitualResult result = tile.performRitual(level, pos, lookupResult.ritual(), lookupResult.stats(), player);
        if (result.successful()) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.imperfect.activated",
                            Component.translatable(lookupResult.ritual().getTranslationKey())).withStyle(ChatFormatting.GREEN), true);
            return InteractionResult.SUCCESS;
        } else {
            Component errorMsg = result.getErrorMessage();
            if (errorMsg != null) {
                player.displayClientMessage(errorMsg.copy().withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.imperfectRitualStone.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.imperfectRitualStone.hint").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
