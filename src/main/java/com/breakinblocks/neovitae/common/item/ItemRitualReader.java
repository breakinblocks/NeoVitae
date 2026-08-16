// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.menu.RitualConfiguratorMenu;
import com.breakinblocks.neovitae.common.menu.RitualConfiguratorMenu.RangeInfo;
import com.breakinblocks.neovitae.compat.sable.SableCompat;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The Ritual Configurator opens a panel on the Master Ritual Stone to pick a
 * range and Spiritus aspect, then drops into a two-corner world pick (with a
 * live bounding-box preview) to resize the selected range.
 */
public class ItemRitualReader extends Item {

    public static final String TOOLTIP_BASE = "tooltip.neovitae.reader.";

    public ItemRitualReader() {
        super(new Item.Properties()
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

    @Nullable
    public BlockPos getMasterPos(ItemStack stack) {
        BlockPos pos = stack.get(NVDataComponents.READER_MASTER_POS.get());
        return pos == null || BlockPos.ZERO.equals(pos) ? null : pos;
    }

    public void setMasterPos(ItemStack stack, BlockPos pos) {
        stack.set(NVDataComponents.READER_MASTER_POS.get(), pos);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(context.getHand());
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        EnumRitualReaderState state = getState(stack);

        if (state == EnumRitualReaderState.SET_AREA_CORNER_1 || state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            return handleAreaCornerClick(stack, level, clickedPos, player, state);
        }

        BlockEntity blockEntity = level.getBlockEntity(clickedPos);
        if (blockEntity instanceof MasterRitualStoneBlockEntity mrs) {
            return openConfigurator(level, mrs, player, context.getHand());
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            EnumRitualReaderState state = getState(stack);
            if (state == EnumRitualReaderState.SET_AREA_CORNER_1 || state == EnumRitualReaderState.SET_AREA_CORNER_2) {
                if (!level.isClientSide()) {
                    setState(stack, EnumRitualReaderState.INFORMATION);
                    player.displayClientMessage(
                            Component.translatable("chat.neovitae.reader.cancelled").withStyle(ChatFormatting.YELLOW), true);
                }
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private InteractionResult openConfigurator(Level level, MasterRitualStoneBlockEntity mrs,
                                               Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        Ritual ritual = resolveRitual(mrs);
        if (ritual == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS;

        List<RangeInfo> ranges = new ArrayList<>();
        for (String key : ritual.getListOfRanges()) {
            AreaDescriptor descriptor = mrs.getBlockRange(key);
            if (descriptor == null) descriptor = ritual.getBlockRange(key);
            if (descriptor == null) continue;
            AABB box = descriptor.getAABB(BlockPos.ZERO);
            ranges.add(new RangeInfo(key,
                    (int) Math.round(box.getXsize()),
                    (int) Math.round(box.getYsize()),
                    (int) Math.round(box.getZsize()),
                    ritual.getMaxVolumeForRange(key),
                    ritual.getMaxHorizontalRadiusForRange(key),
                    ritual.getMaxVerticalRadiusForRange(key)));
        }

        ItemStack stack = serverPlayer.getItemInHand(hand);
        String currentKey = getRangeKey(stack);
        boolean valid = ranges.stream().anyMatch(r -> r.key().equals(currentKey));
        if (!valid && !ranges.isEmpty()) {
            setRangeKey(stack, ranges.get(0).key());
        }

        BlockPos masterPos = mrs.getBlockPos();
        String ritualKey = ritual.getTranslationKey();
        int aspect = mrs.getActiveSpiritusAspect().ordinal();
        boolean active = mrs.isActive();
        boolean usesKeepCount = ritual.usesKeepCount();
        int keepCount = mrs.getKeepCount();

        serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new RitualConfiguratorMenu(id, inv, hand, masterPos, ritualKey, ranges, aspect, active, usesKeepCount, keepCount),
                Component.translatable("container.neovitae.ritual_configurator")
        ), buf -> RitualConfiguratorMenu.write(buf, hand, masterPos, ritualKey, ranges, aspect, active, usesKeepCount, keepCount));

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleAreaCornerClick(ItemStack stack, Level level, BlockPos clickedPos,
                                                     Player player, EnumRitualReaderState state) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (state == EnumRitualReaderState.SET_AREA_CORNER_1) {
            MasterRitualStoneBlockEntity mrs = resolveEditingMasterRitualStone(stack, level, player);
            Ritual ritual = mrs != null ? resolveRitual(mrs) : null;
            if (mrs != null && ritual != null && ritual.getMaxVolumeForRange(getRangeKey(stack)) == 1) {
                if (applyArea(stack, mrs, ritual, clickedPos, clickedPos, player)) {
                    setState(stack, EnumRitualReaderState.INFORMATION);
                }
                return InteractionResult.SUCCESS;
            }
            if (mrs != null && ritual != null && !isCornerWithinBounds(stack, mrs, ritual, clickedPos, player)) {
                return InteractionResult.SUCCESS;
            }
            setCorner1(stack, clickedPos);
            setState(stack, EnumRitualReaderState.SET_AREA_CORNER_2);
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.corner1Set",
                            clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), true);
            return InteractionResult.SUCCESS;
        }

        MasterRitualStoneBlockEntity mrs = resolveEditingMasterRitualStone(stack, level, player);
        if (mrs == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.noMRS").withStyle(ChatFormatting.RED), true);
            setState(stack, EnumRitualReaderState.INFORMATION);
            return InteractionResult.SUCCESS;
        }

        Ritual ritual = resolveRitual(mrs);
        if (ritual == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.noRitual").withStyle(ChatFormatting.RED), true);
            setState(stack, EnumRitualReaderState.INFORMATION);
            return InteractionResult.SUCCESS;
        }

        BlockPos corner1 = getCorner1(stack);
        if (applyArea(stack, mrs, ritual, corner1, clickedPos, player)) {
            setState(stack, EnumRitualReaderState.INFORMATION);
        } else if (!isCornerWithinBounds(stack, mrs, ritual, corner1, null)) {
            setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean isCornerWithinBounds(ItemStack stack, MasterRitualStoneBlockEntity mrs, Ritual ritual,
                                         BlockPos corner, @Nullable Player player) {
        String rangeKey = getRangeKey(stack);
        AreaDescriptor current = mrs.getBlockRange(rangeKey);
        if (current == null) current = ritual.getBlockRange(rangeKey);
        if (current == null) return true;

        BlockPos offset = corner.subtract(mrs.getBlockPos());
        if (ritual.canBlockRangeBeModified(rangeKey, current.copy(), mrs, offset, offset)
                == EnumReaderBoundaries.SUCCESS) {
            return true;
        }

        if (player != null) {
            player.displayClientMessage(
                    ritual.getErrorForBlockRangeOnFail(player, rangeKey, mrs, offset, offset).copy()
                            .append(Component.translatable("chat.neovitae.reader.cornerRejected"))
                            .withStyle(ChatFormatting.RED), true);
        }
        return false;
    }

    private boolean applyArea(ItemStack stack, MasterRitualStoneBlockEntity mrs, Ritual ritual,
                              BlockPos corner1, BlockPos corner2, Player player) {
        String rangeKey = getRangeKey(stack);
        BlockPos mrsPos = mrs.getBlockPos();
        BlockPos offset1 = corner1.subtract(mrsPos);
        BlockPos offset2 = corner2.subtract(mrsPos);

        AreaDescriptor current = mrs.getBlockRange(rangeKey);
        if (current == null) current = ritual.getBlockRange(rangeKey);
        if (current == null) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.invalidRange").withStyle(ChatFormatting.RED), true);
            return false;
        }

        AreaDescriptor descriptor = current.copy();
        EnumReaderBoundaries result = ritual.canBlockRangeBeModified(rangeKey, descriptor, mrs, offset1, offset2);
        if (result == EnumReaderBoundaries.SUCCESS) {
            descriptor.modifyAreaByBlockPositions(offset1, offset2);
            mrs.setBlockRange(rangeKey, descriptor);
            ResourceLocation configuredFor = mrs.isActive() ? mrs.getCurrentRitualId() : RitualRegistry.getId(ritual);
            if (configuredFor != null) {
                mrs.markRangesConfiguredFor(configuredFor);
            }
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.reader.areaSet", rangeKey), true);
            return true;
        }

        Component errorMsg = ritual.getErrorForBlockRangeOnFail(player, rangeKey, mrs, offset1, offset2);
        player.displayClientMessage(
                errorMsg.copy().append(Component.translatable("chat.neovitae.reader.areaRetry"))
                        .withStyle(ChatFormatting.RED), true);
        return false;
    }

    private MasterRitualStoneBlockEntity resolveEditingMasterRitualStone(ItemStack stack, Level level, Player player) {
        BlockPos storedPos = getMasterPos(stack);
        if (storedPos != null && level.isLoaded(storedPos)
                && level.getBlockEntity(storedPos) instanceof MasterRitualStoneBlockEntity mrs
                && resolveRitual(mrs) != null) {
            return mrs;
        }
        return findNearbyMasterRitualStone(level, player);
    }

    private MasterRitualStoneBlockEntity findNearbyMasterRitualStone(Level level, Player player) {
        int searchRadius = 32;
        BlockPos center = player.blockPosition();

        MasterRitualStoneBlockEntity found = searchMasterRitualStone(level, center, searchRadius);
        if (found != null) {
            return found;
        }

        SableCompat.ContraptionView view = SableCompat.viewForEntity(player);
        if (view != null) {
            return searchMasterRitualStone(view.subLevel(), view.localCenter(), searchRadius);
        }

        return null;
    }

    private static MasterRitualStoneBlockEntity searchMasterRitualStone(Level level, BlockPos center, int searchRadius) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-searchRadius, -searchRadius, -searchRadius),
                center.offset(searchRadius, searchRadius, searchRadius))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MasterRitualStoneBlockEntity mrs && resolveRitual(mrs) != null) {
                return mrs;
            }
        }
        return null;
    }

    /**
     * Returns the active ritual if one is running, otherwise the best-matching
     * ritual for the stone's current rune layout, so the configurator can be
     * used before activation. Mirrors the activation-time best-match scan.
     */
    private static Ritual resolveRitual(MasterRitualStoneBlockEntity mrs) {
        Ritual active = mrs.getCurrentRitual();
        if (active != null) return active;

        Ritual best = null;
        int bestSize = -1;
        for (Ritual ritual : RitualRegistry.getAllRituals()) {
            if (mrs.checkStructure(ritual)) {
                int size = countComponents(ritual);
                if (size > bestSize) {
                    bestSize = size;
                    best = ritual;
                }
            }
        }
        return best;
    }

    private static int countComponents(Ritual ritual) {
        int[] count = {0};
        ritual.gatherComponents(component -> count[0]++);
        return count[0];
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EnumRitualReaderState state = getState(stack);
        if (state == EnumRitualReaderState.SET_AREA_CORNER_1 || state == EnumRitualReaderState.SET_AREA_CORNER_2) {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "currentState",
                    Component.translatable(TOOLTIP_BASE + "state." + state.getSerializedName()))
                    .withStyle(ChatFormatting.GRAY));
            String rangeKey = getRangeKey(stack);
            if (!rangeKey.isEmpty()) {
                tooltip.add(Component.translatable(TOOLTIP_BASE + "currentRange", rangeKey)
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.3").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable(TOOLTIP_BASE + "help.4").withStyle(ChatFormatting.BLUE));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
