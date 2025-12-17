package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.item.ItemActivationCrystal;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.List;

/**
 * Master Ritual Stone block - the central block for ritual multiblock structures.
 * Place rune blocks in the correct pattern and activate with an activation crystal.
 */
public class BlockMasterRitualStone extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public final boolean isInverted;

    public BlockMasterRitualStone(boolean isInverted) {
        super(BlockBehaviour.Properties.of()
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
        MasterRitualStoneTile tile = new MasterRitualStoneTile(pos, state);
        tile.setInverted(isInverted);
        return tile;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == BMTiles.MASTER_RITUAL_STONE_TYPE.get()
                ? (lvl, pos, st, be) -> MasterRitualStoneTile.tick(lvl, pos, st, (MasterRitualStoneTile) be)
                : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneTile tile)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Handle ritual diviner - let the item handle placement
        if (stack.getItem() instanceof com.breakinblocks.neovitae.common.item.ItemRitualDiviner diviner) {
            // Client-side: spawn particles and return success
            if (level.isClientSide()) {
                com.breakinblocks.neovitae.common.item.ItemRitualDiviner.spawnParticles(level, pos.relative(hitResult.getDirection()), 15);
                return ItemInteractionResult.SUCCESS;
            }

            // Server-side: delegate to the diviner's logic
            String ritualId = diviner.getCurrentRitualId(stack);
            if (ritualId.isEmpty()) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.diviner.noRitualSelected").withStyle(ChatFormatting.RED), true);
                return ItemInteractionResult.FAIL;
            }

            // Try to build ritual
            if (diviner.addRuneToRitual(stack, level, pos, player)) {
                diviner.setStoredPos(stack, pos);
                diviner.setActivated(stack, true);
                return ItemInteractionResult.SUCCESS;
            }

            // Ritual is complete or cannot progress
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.diviner.ritualComplete").withStyle(ChatFormatting.GREEN), true);
            return ItemInteractionResult.SUCCESS;
        }

        // Client-side early return for other items
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        // Handle activation crystal
        if (stack.getItem() instanceof ItemActivationCrystal crystal) {
            Binding binding = stack.get(BMDataComponents.BINDING.get());
            if (binding == null || binding.uuid() == null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.crystal.notBound").withStyle(ChatFormatting.RED), true);
                return ItemInteractionResult.FAIL;
            }

            int crystalLevel = crystal.getCrystalLevel(stack);

            // Find the BEST matching ritual (most components = most specific match)
            // This prevents smaller rituals from matching when a larger ritual structure exists
            Ritual bestMatch = null;
            int bestMatchSize = 0;

            for (Ritual ritual : RitualRegistry.getAllRituals()) {
                if (tile.checkStructure(ritual)) {
                    int componentCount = countRitualComponents(ritual);
                    if (componentCount > bestMatchSize) {
                        bestMatchSize = componentCount;
                        bestMatch = ritual;
                    }
                }
            }

            if (bestMatch != null) {
                if (tile.activateRitual(bestMatch, player, crystalLevel)) {
                    player.displayClientMessage(
                            Component.translatable("chat.neovitae.ritual.activated",
                                    Component.translatable(bestMatch.getTranslationKey())).withStyle(ChatFormatting.GREEN), true);
                    return ItemInteractionResult.SUCCESS;
                }
            }

            player.displayClientMessage(
                    Component.translatable("chat.neovitae.ritual.noMatch").withStyle(ChatFormatting.RED), true);
            return ItemInteractionResult.FAIL;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneTile tile)) {
            return InteractionResult.PASS;
        }

        // Sneak-click to deactivate
        if (player.isShiftKeyDown() && tile.isActive()) {
            tile.stopRitual(Ritual.BreakType.DEACTIVATE);
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.ritual.deactivated").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }

        // Show ritual info
        if (tile.isActive() && tile.getCurrentRitual() != null) {
            tile.provideInformationOfRitualToPlayer(player);
        } else {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.ritual.notActive").withStyle(ChatFormatting.GRAY), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MasterRitualStoneTile tile) {
                if (tile.isActive()) {
                    tile.stopRitual(Ritual.BreakType.BREAK_MRS);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.decoration.safe").withStyle(ChatFormatting.GRAY));
        if (isInverted) {
            tooltip.add(Component.translatable("tooltip.neovitae.masterRitualStone.inverted").withStyle(ChatFormatting.DARK_PURPLE));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    /**
     * Counts the number of rune components in a ritual.
     * Used for determining the most specific ritual match.
     */
    private int countRitualComponents(Ritual ritual) {
        java.util.List<com.breakinblocks.neovitae.ritual.RitualComponent> components = new java.util.ArrayList<>();
        ritual.gatherComponents(components::add);
        return components.size();
    }
}
