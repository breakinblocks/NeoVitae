package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.*;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * The Ritual Reader is used to configure ritual effect areas and settings.
 * - Right-click on Master Ritual Stone to get ritual information
 * - Sneak + right-click on MRS to cycle through reader modes
 * - Right-click on blocks while in SET_AREA mode to define corners
 * - Sneak + right-click in air to cycle through range keys
 */
public class ItemRitualReader extends Item {

    public static final String TOOLTIP_BASE = "tooltip.neovitae.reader.";

    public ItemRitualReader(Item.Properties props) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.READER_STATE.get(), 0)
                .component(NVDataComponents.READER_RANGE_KEY.get(), "")
                .component(NVDataComponents.READER_CORNER1.get(), BlockPos.ZERO));
    }


    public EnumRitualReaderState getState(ItemStack stack) {
        Integer stateOrdinal = stack.get(NVDataComponents.READER_STATE.get());
        if (stateOrdinal == null || stateOrdinal < 0 || stateOrdinal >= EnumRitualReaderState.values().length) {
            return EnumRitualReaderState.INFORMATION;
        }
        return EnumRitualReaderState.values()[stateOrdinal];
    }

    public void setState(ItemStack stack, EnumRitualReaderState state) {
        stack.set(NVDataComponents.READER_STATE.get(), state.ordinal());
    }

    public String getRangeKey(ItemStack stack) {
        String key = stack.get(NVDataComponents.READER_RANGE_KEY.get());
        return key != null ? key : "";
    }

    public void setRangeKey(ItemStack stack, String key) {
        stack.set(NVDataComponents.READER_RANGE_KEY.get(), key);
    }

    public BlockPos getCorner1(ItemStack stack) {
        BlockPos pos = stack.get(NVDataComponents.READER_CORNER1.get());
        return pos != null ? pos : BlockPos.ZERO;
    }

    public void setCorner1(ItemStack stack, BlockPos pos) {
        stack.set(NVDataComponents.READER_CORNER1.get(), pos);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(clickedPos);

        if (blockEntity instanceof MasterRitualStoneBlockEntity mrsT) {
            return handleMasterRitualStoneClick(stack, level, mrsT, player);
        }

        EnumRitualReaderState state = getState(stack);
        if (state == EnumRitualReaderState.SET_AREA_CORNER_1 ||
            state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            return handleAreaCornerClick(stack, level, clickedPos, player, state);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult handleMasterRitualStoneClick(ItemStack stack, Level level,
                                                            MasterRitualStoneBlockEntity mrs, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            cycleReaderState(stack, player);
            return InteractionResult.SUCCESS;
        }

        Ritual ritual = mrs.getCurrentRitual();
        if (ritual == null) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED));
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
                    List<String> ranges = ritual.getListOfRanges();
                    if (!ranges.isEmpty()) {
                        rangeKey = ranges.get(0);
                        setRangeKey(stack, rangeKey);
                    }
                }
                mrs.provideInformationOfRangeToPlayer(player, rangeKey);
            }
            case SET_WILL_CONFIG -> {
                SpiritusType currentType = mrs.getActiveWillConfig();
                SpiritusType nextType = switch (currentType) {
                    case DEFAULT -> SpiritusType.CORROSIVE;
                    case CORROSIVE -> SpiritusType.DESTRUCTIVE;
                    case DESTRUCTIVE -> SpiritusType.VENGEFUL;
                    case VENGEFUL -> SpiritusType.STEADFAST;
                    case STEADFAST -> SpiritusType.DEFAULT;
                };
                mrs.setActiveWillConfig(nextType);
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.reader.willType",
                                Component.translatable("will.neovitae." + nextType.getSerializedName())));
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
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.reader.corner1Set",
                            clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()));
            return InteractionResult.SUCCESS;
        }

        if (state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            BlockPos corner1 = getCorner1(stack);
            BlockPos corner2 = clickedPos;
            String rangeKey = getRangeKey(stack);

            MasterRitualStoneBlockEntity mrs = findNearbyMasterRitualStone(level, corner1, corner2, player);
            if (mrs == null) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.reader.noMRS").withStyle(ChatFormatting.RED));
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            Ritual ritual = mrs.getCurrentRitual();
            if (ritual == null) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED));
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            BlockPos mrsPos = mrs.getBlockPos();
            BlockPos offset1 = corner1.subtract(mrsPos);
            BlockPos offset2 = corner2.subtract(mrsPos);

            AreaDescriptor descriptor = ritual.getBlockRange(rangeKey);
            if (descriptor == null) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.reader.invalidRange").withStyle(ChatFormatting.RED));
                setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
                return InteractionResult.SUCCESS;
            }

            EnumReaderBoundaries result = ritual.canBlockRangeBeModified(rangeKey, descriptor, mrs, offset1, offset2);
            if (result == EnumReaderBoundaries.SUCCESS) {
                descriptor.modifyAreaByBlockPositions(offset1, offset2);
                mrs.setBlockRange(rangeKey, descriptor);
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.reader.areaSet", rangeKey));
            } else {
                Component errorMsg = ritual.getErrorForBlockRangeOnFail(player, rangeKey, mrs, offset1, offset2);
                player.sendOverlayMessage(errorMsg.copy().withStyle(ChatFormatting.RED));
            }

            setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player.isShiftKeyDown()) {
            cycleRangeKey(stack, player);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }


    private void cycleReaderState(ItemStack stack, Player player) {
        EnumRitualReaderState current = getState(stack);
        EnumRitualReaderState next = current.next();
        setState(stack, next);
        player.sendOverlayMessage(
                Component.translatable(TOOLTIP_BASE + "state." + next.getSerializedName()));
    }

    private void cycleRangeKey(ItemStack stack, Player player) {
        String currentKey = getRangeKey(stack);
        if (currentKey.isEmpty()) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.reader.noRangeSelected").withStyle(ChatFormatting.YELLOW));
        } else {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.reader.currentRange", currentKey));
        }
    }

    public void cycleRangeKey(ItemStack stack, Player player, Ritual ritual) {
        if (ritual == null) return;

        String currentKey = getRangeKey(stack);
        String nextKey = ritual.getNextBlockRange(currentKey);
        setRangeKey(stack, nextKey);

        if (!nextKey.isEmpty()) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.reader.rangeSelected", nextKey));
        }
    }


    private MasterRitualStoneBlockEntity findNearbyMasterRitualStone(Level level, BlockPos corner1, BlockPos corner2, Player player) {
        int searchRadius = 32;
        BlockPos center = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-searchRadius, -searchRadius, -searchRadius),
                center.offset(searchRadius, searchRadius, searchRadius))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MasterRitualStoneBlockEntity mrs) {
                if (mrs.isActive()) {
                    return mrs;
                }
            }
        }

        return null;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        EnumRitualReaderState state = getState(stack);
        tooltip.accept(Component.translatable(TOOLTIP_BASE + "currentState",
                Component.translatable(TOOLTIP_BASE + "state." + state.getSerializedName()))
                .withStyle(ChatFormatting.GRAY));

        String rangeKey = getRangeKey(stack);
        if (!rangeKey.isEmpty()) {
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "currentRange", rangeKey)
                    .withStyle(ChatFormatting.GRAY));
        }

        tooltip.accept(Component.empty());
        tooltip.accept(Component.translatable(TOOLTIP_BASE + "help.1").withStyle(ChatFormatting.BLUE));
        tooltip.accept(Component.translatable(TOOLTIP_BASE + "help.2").withStyle(ChatFormatting.BLUE));
        tooltip.accept(Component.translatable(TOOLTIP_BASE + "help.3").withStyle(ChatFormatting.BLUE));}
}
