// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.item.ItemActivationCrystal;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Master Ritual Stone block - the central block for ritual multiblock structures.
 * Place rune blocks in the correct pattern and activate with an activation crystal.
 */
public class BlockMasterRitualStone extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public final boolean isInverted;

    public BlockMasterRitualStone(BlockBehaviour.Properties props, boolean isInverted) {
        super(props
                .sound(SoundType.STONE)
                .strength(2.0F, 5.0F)
                .requiresCorrectToolForDrops());
        this.isInverted = isInverted;
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        MasterRitualStoneBlockEntity tile = new MasterRitualStoneBlockEntity(pos, state);
        tile.setInverted(isInverted);
        return tile;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == NVTiles.MASTER_RITUAL_STONE_TYPE.get()
                ? (lvl, pos, st, be) -> MasterRitualStoneBlockEntity.tick(lvl, pos, st, (MasterRitualStoneBlockEntity) be)
                : null;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneBlockEntity tile)) {
            return InteractionResult.PASS;
        }

        if (stack.getItem() instanceof ItemRitualDiviner diviner) {
            if (level.isClientSide()) {
                ItemRitualDiviner.spawnParticles(level, pos.relative(hitResult.getDirection()), 15);
                return InteractionResult.SUCCESS;
            }

            String ritualId = diviner.getCurrentRitualId(stack);
            if (ritualId.isEmpty()) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.diviner.noRitualSelected").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            if (diviner.addRuneToRitual(stack, level, pos, player)) {
                diviner.setStoredPos(stack, pos);
                diviner.setActivated(stack, true);
                return InteractionResult.SUCCESS;
            }

            Ritual ritual = diviner.getCurrentRitual(stack);
            if (ritual != null && tile.checkStructure(ritual)) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.diviner.ritualComplete").withStyle(ChatFormatting.GREEN));
            }
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof ItemActivationCrystal crystal) {
            Binding binding = stack.get(NVDataComponents.BINDING.get());
            if (binding == null || binding.uuid() == null) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.crystal.notBound").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            int crystalLevel = crystal.getCrystalLevel(stack);

            // Find the BEST matching ritual (most components = most specific match)
            // This prevents smaller rituals from matching when a larger ritual structure exists
            Ritual bestMatch = null;
            int bestMatchSize = 0;

            for (Ritual ritual : RitualRegistry.getAllRituals()) {
                if (tile.checkStructure(ritual)) {
                    int componentCount = countRitualComponents(ritual, level);
                    if (componentCount > bestMatchSize) {
                        bestMatchSize = componentCount;
                        bestMatch = ritual;
                    }
                }
            }

            if (bestMatch != null) {
                if (tile.activateRitual(bestMatch, player, crystalLevel)) {
                    player.sendOverlayMessage(
                            Component.translatable("chat.neovitae.ritual.activated",
                                    Component.translatable(bestMatch.getTranslationKey())).withStyle(ChatFormatting.GREEN));
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }

            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.ritual.noMatch").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneBlockEntity tile)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && tile.isActive()) {
            tile.stopRitual(Ritual.BreakType.DEACTIVATE);
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.ritual.deactivated").withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS;
        }

        if (tile.isActive() && tile.getCurrentRitual() != null) {
            tile.provideInformationOfRitualToPlayer(player);
        } else {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.ritual.notActive").withStyle(ChatFormatting.GRAY));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (true) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MasterRitualStoneBlockEntity tile) {
                if (tile.isActive()) {
                    tile.stopRitual(Ritual.BreakType.BREAK_MRS);
    }
            }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    // @Override (removed: not an override in 26.1)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        if (isInverted) {
            tooltip.accept(Component.translatable("tooltip.neovitae.masterRitualStone.inverted").withStyle(ChatFormatting.DARK_PURPLE));
        }}

    private int countRitualComponents(Ritual ritual, Level level) {
        return RitualLayouts.get(level, ritual).size();
    }
}
