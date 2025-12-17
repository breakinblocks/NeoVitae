package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.TileDungeonSeal;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;

/**
 * Dungeon Seal block - represents a sealed door in a procedural dungeon.
 * When activated, requests a new room to be generated from the dungeon controller.
 */
public class BlockDungeonSeal extends Block implements EntityBlock {

    public BlockDungeonSeal() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1.0F, 3600000.0F)  // Unbreakable in survival
                .noLootTable()
                .lightLevel(state -> 7));  // Faint glow to make it visible
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileDungeonSeal(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TileDungeonSeal seal)) {
            return InteractionResult.PASS;
        }

        // Request room generation
        boolean success = seal.requestRoomFromController(player);

        if (success) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.dungeon.seal.opened")
                            .withStyle(ChatFormatting.GREEN), true);
        } else {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.dungeon.seal.failed")
                            .withStyle(ChatFormatting.RED), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        // Handle dungeon key items
        if (stack.getItem() instanceof ItemDungeonKey dungeonKey) {
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof TileDungeonSeal seal)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // Check if this key can open this door
            if (!dungeonKey.canOpenDoor(seal.getPotentialRoomTypes())) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.dungeon.seal.wrongKey")
                                .withStyle(ChatFormatting.YELLOW), true);
                return ItemInteractionResult.FAIL;
            }

            // Request room generation with key
            boolean success = seal.requestRoomFromControllerWithKey(player, dungeonKey);

            if (success) {
                // Consume one key
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.dungeon.seal.opened")
                                .withStyle(ChatFormatting.GREEN), true);
            } else {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.dungeon.seal.failed")
                                .withStyle(ChatFormatting.RED), true);
            }

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
