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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.DungeonSealBlockEntity;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;

public class BlockDungeonSeal extends Block implements EntityBlock {

    public static final BooleanProperty SPECIAL = BooleanProperty.create("special");

    public BlockDungeonSeal() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .lightLevel(state -> state.getValue(SPECIAL) ? 11 : 7));
        this.registerDefaultState(this.stateDefinition.any().setValue(SPECIAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SPECIAL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonSealBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DungeonSealBlockEntity seal)) {
            return InteractionResult.PASS;
        }

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

        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        if (stack.getItem() instanceof ItemDungeonKey dungeonKey) {
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof DungeonSealBlockEntity seal)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!dungeonKey.canOpenDoor(seal.getPotentialRoomTypes())) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.dungeon.seal.wrongKey")
                                .withStyle(ChatFormatting.YELLOW), true);
                return ItemInteractionResult.FAIL;
            }

            boolean success = seal.requestRoomFromControllerWithKey(player, dungeonKey);

            if (success) {
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
