package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.ritual.*;

import java.util.List;

/**
 * The Ritual Reader is used to configure ritual effect areas and settings.
 * - Right-click on Master Ritual Stone to get ritual information
 * - Sneak + right-click on MRS to cycle through reader modes
 * - Right-click on blocks while in SET_AREA mode to define corners
 * - Sneak + right-click in air to cycle through range keys
 */
public class ItemRitualReader extends Item {

    public static final String TOOLTIP_BASE = "tooltip.neovitae.reader.";

    public ItemRitualReader() {
        super(new Item.Properties()
                .stacksTo(1)
                .component(BMDataComponents.READER_STATE.get(), 0)
                .component(BMDataComponents.READER_RANGE_KEY.get(), "")
                .component(BMDataComponents.READER_CORNER1.get(), BlockPos.ZERO));
    }

    // ==================== State Management ====================

    public EnumRitualReaderState getState(ItemStack stack) {
        Integer stateOrdinal = stack.get(BMDataComponents.READER_STATE.get());
        if (stateOrdinal == null || stateOrdinal < 0 || stateOrdinal >= EnumRitualReaderState.values().length) {
            return EnumRitualReaderState.INFORMATION;
        }
        return EnumRitualReaderState.values()[stateOrdinal];
    }

    public void setState(ItemStack stack, EnumRitualReaderState state) {
        stack.set(BMDataComponents.READER_STATE.get(), state.ordinal());
    }

    public String getRangeKey(ItemStack stack) {
        String key = stack.get(BMDataComponents.READER_RANGE_KEY.get());
        return key != null ? key : "";
    }

    public void setRangeKey(ItemStack stack, String key) {
        stack.set(BMDataComponents.READER_RANGE_KEY.get(), key);
    }

    public BlockPos getCorner1(ItemStack stack) {
        BlockPos pos = stack.get(BMDataComponents.READER_CORNER1.get());
        return pos != null ? pos : BlockPos.ZERO;
    }

    public void setCorner1(ItemStack stack, BlockPos pos) {
        stack.set(BMDataComponents.READER_CORNER1.get(), pos);
    }

    // ==================== Interaction ====================

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(clickedPos);

        if (blockEntity instanceof MasterRitualStoneTile mrsT) {
            return handleMasterRitualStoneClick(stack, level, mrsT, player);
        }

        // Handle setting area corners when not clicking on MRS
        EnumRitualReaderState state = getState(stack);
        if (state == EnumRitualReaderState.SET_AREA_CORNER_1 ||
            state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            return handleAreaCornerClick(stack, level, clickedPos, player, state);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult handleMasterRitualStoneClick(ItemStack stack, Level level,
                                                            MasterRitualStoneTile mrs, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            // Sneak + click on MRS cycles reader mode
            cycleReaderState(stack, player);
            return InteractionResult.SUCCESS;
        }

        Ritual ritual = mrs.getCurrentRitual();
        if (ritual == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        }

        EnumRitualReaderState state = getState(stack);
        switch (state) {
            case INFORMATION -> {
                mrs.provideInformationOfRitualToPlayer(player);
            }
            case SET_AREA_CORNER_1, SET_AREA_CORNER_2 -> {
                String rangeKey = getRangeKey(stack);
                if (rangeKey.isEmpty()) {
                    // No range selected - auto-select first range
                    List<String> ranges = ritual.getListOfRanges();
                    if (!ranges.isEmpty()) {
                        rangeKey = ranges.get(0);
                        setRangeKey(stack, rangeKey);
                    }
                }
                mrs.provideInformationOfRangeToPlayer(player, rangeKey);
            }
            case SET_WILL_CONFIG -> {
                // Cycle through will types
                EnumWillType currentType = mrs.getActiveWillConfig();
                EnumWillType nextType = switch (currentType) {
                    case DEFAULT -> EnumWillType.CORROSIVE;
                    case CORROSIVE -> EnumWillType.DESTRUCTIVE;
                    case DESTRUCTIVE -> EnumWillType.VENGEFUL;
                    case VENGEFUL -> EnumWillType.STEADFAST;
                    case STEADFAST -> EnumWillType.DEFAULT;
                };
                mrs.setActiveWillConfig(nextType);
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.reader.willType",
                                Component.translatable("will.neovitae." + nextType.getSerializedName())), true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleAreaCornerClick(ItemStack stack, Level level, BlockPos clickedPos,
                                                     Player player, EnumRitualReaderState state) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (state == EnumRitualReaderState.SET_AREA_CORNER_1) {
            setCorner1(stack, clickedPos);
            setState(stack, EnumRitualReaderState.SET_AREA_CORNER_2);
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.corner1Set",
                            clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), true);
            return InteractionResult.SUCCESS;
        }

        if (state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            BlockPos corner1 = getCorner1(stack);
            BlockPos corner2 = clickedPos;
            String rangeKey = getRangeKey(stack);

            // Find the master ritual stone - we need to look for it
            MasterRitualStoneTile mrs = findNearbyMasterRitualStone(level, corner1, corner2, player);
            if (mrs == null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.reader.noMRS").withStyle(ChatFormatting.RED), true);
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            Ritual ritual = mrs.getCurrentRitual();
            if (ritual == null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED), true);
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            // Convert world positions to offsets relative to MRS
            BlockPos mrsPos = mrs.getBlockPos();
            BlockPos offset1 = corner1.subtract(mrsPos);
            BlockPos offset2 = corner2.subtract(mrsPos);

            // Check if the new area is valid
            AreaDescriptor descriptor = ritual.getBlockRange(rangeKey);
            if (descriptor == null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.reader.invalidRange").withStyle(ChatFormatting.RED), true);
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            EnumReaderBoundaries result = ritual.canBlockRangeBeModified(rangeKey, descriptor, mrs, offset1, offset2);
            if (result == EnumReaderBoundaries.SUCCESS) {
                descriptor.modifyAreaByBlockPositions(offset1, offset2);
                mrs.setBlockRange(rangeKey, descriptor);
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.reader.areaSet", rangeKey), true);
            } else {
                Component errorMsg = ritual.getErrorForBlockRangeOnFail(player, rangeKey, mrs, offset1, offset2);
                player.displayClientMessage(errorMsg.copy().withStyle(ChatFormatting.RED), true);
            }

            setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player.isShiftKeyDown()) {
            // Sneak + right-click in air cycles through range keys
            cycleRangeKey(stack, player);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    // ==================== Cycling Methods ====================

    private void cycleReaderState(ItemStack stack, Player player) {
        EnumRitualReaderState current = getState(stack);
        EnumRitualReaderState next = current.next();
        setState(stack, next);
        player.displayClientMessage(
                Component.translatable(TOOLTIP_BASE + "state." + next.getSerializedName()), true);
    }

    private void cycleRangeKey(ItemStack stack, Player player) {
        // We need context of the current ritual to know valid ranges
        // For now, just display current key
        String currentKey = getRangeKey(stack);
        if (currentKey.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.noRangeSelected").withStyle(ChatFormatting.YELLOW), true);
        } else {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.currentRange", currentKey), true);
        }
    }

    /**
     * Cycles to the next range key for a specific ritual.
     */
    public void cycleRangeKey(ItemStack stack, Player player, Ritual ritual) {
        if (ritual == null) return;

        String currentKey = getRangeKey(stack);
        String nextKey = ritual.getNextBlockRange(currentKey);
        setRangeKey(stack, nextKey);

        if (!nextKey.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.rangeSelected", nextKey), true);
        }
    }

    // ==================== Utilities ====================

    private MasterRitualStoneTile findNearbyMasterRitualStone(Level level, BlockPos corner1, BlockPos corner2, Player player) {
        // Search in a reasonable area around the player and corners
        int searchRadius = 32;
        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-searchRadius, -searchRadius, -searchRadius),
                center.offset(searchRadius, searchRadius, searchRadius))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MasterRitualStoneTile mrs) {
                if (mrs.isActive()) {
                    return mrs;
                }
            }
        }

        return null;
    }

    // ==================== Tooltip ====================

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EnumRitualReaderState state = getState(stack);
        tooltip.add(Component.translatable(TOOLTIP_BASE + "currentState",
                Component.translatable(TOOLTIP_BASE + "state." + state.getSerializedName()))
                .withStyle(ChatFormatting.GRAY));

        String rangeKey = getRangeKey(stack);
        if (!rangeKey.isEmpty()) {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "currentRange", rangeKey)
                    .withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.3").withStyle(ChatFormatting.BLUE));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
