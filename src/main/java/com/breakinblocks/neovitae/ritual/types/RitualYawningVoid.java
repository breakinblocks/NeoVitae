// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.common.block.BlockMasterRitualStone;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualYawningVoid extends Ritual {

    public static final String QUARRY_RANGE = "quarryRange";
    public static final String CHEST_RANGE = "chestRange";
    public static final String PLACEMENT_RANGE = "placementRange";

    private static final double RAW_DRAIN = 0.05;
    private static final double STEADFAST_DRAIN = 0.05;
    private static final double CORROSIVE_DRAIN = 0.05;
    private static final int DEFAULT_REFRESH_TIME = 10;
    private static final int MAX_CHECKS_PER_REFRESH = 100;

    private int refreshTime = DEFAULT_REFRESH_TIME;
    private BlockPos lastPos;

    public RitualYawningVoid() {
        super("yawning_void", 0, 5000, "ritual." + NeoVitae.MODID + ".yawning_void");
        addBlockRange(QUARRY_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-1, -3, -1), 3, 3, 3));
        addBlockRange(PLACEMENT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-1, 1, -1), 3, 3, 3));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(QUARRY_RANGE, 0, 64, 32);
        setMaximumVolumeAndDistanceOfRange(PLACEMENT_RANGE, 50, 4, 4);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 3, 3);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        Level world = ctx.level();
        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        SpiritusState will = RitualHelper.querySpiritus(world, masterPos, RAW_DRAIN);

        double rawWill = will.hasDefault() ? will.getDefault() : 0;
        boolean swapMode = will.hasSteadfast() && will.getSteadfast() >= STEADFAST_DRAIN;
        boolean filterMode = will.hasCorrosive() && will.getCorrosive() >= CORROSIVE_DRAIN;

        RitualStats stats = getStats();
        int baseRefresh = stats != null ? stats.refreshTime() : DEFAULT_REFRESH_TIME;
        refreshTime = rawWill >= RAW_DRAIN
                ? Math.max(1, (int) (baseRefresh - rawWill / 10))
                : baseRefresh;

        AreaDescriptor.Rectangle quarryRange = (AreaDescriptor.Rectangle)
                RitualHelper.getEffectiveRange(ctx.master(), this, QUARRY_RANGE);
        BlockPos minOffset = quarryRange.getMinimumOffset();
        BlockPos maxOffset = quarryRange.getMaximumOffset().offset(-1, -1, -1);

        BlockPos swapDestination = swapMode ? findFreePlacementSlot(ctx, masterPos) : null;
        if (swapMode && swapDestination == null) {
            return;
        }

        Set<Item> filter = filterMode ? buildFilter(ctx, masterPos) : null;
        if (filterMode && (filter == null || filter.isEmpty())) {
            return;
        }

        int i = minOffset.getX(), j = maxOffset.getY(), k = minOffset.getZ();
        if (lastPos != null) {
            i = clamp(lastPos.getX(), minOffset.getX(), maxOffset.getX());
            j = clamp(lastPos.getY(), minOffset.getY(), maxOffset.getY());
            k = clamp(lastPos.getZ(), minOffset.getZ(), maxOffset.getZ());
        }

        int checks = 0;
        boolean consumeRaw = false;
        boolean consumeSteadfast = false;
        boolean consumeCorrosive = false;

        while (j >= minOffset.getY()) {
            while (i <= maxOffset.getX()) {
                while (k <= maxOffset.getZ()) {
                    if (checks >= MAX_CHECKS_PER_REFRESH) {
                        lastPos = new BlockPos(i, j, k);
                        return;
                    }
                    checks++;

                    BlockPos targetPos = masterPos.offset(i, j, k);
                    BlockState state = world.getBlockState(targetPos);

                    if (!isCandidate(state, world, targetPos)) {
                        k++;
                        continue;
                    }

                    if (filterMode && !filterMatches(state, filter)) {
                        k++;
                        continue;
                    }

                    if (!BlockProtectionHelper.canBreakBlock(world, targetPos, owner)) {
                        k++;
                        continue;
                    }

                    if (swapMode) {
                        if (!BlockProtectionHelper.tryPlaceBlock(world, swapDestination, state, owner)) {
                            k++;
                            continue;
                        }
                        world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        consumeSteadfast = true;
                    } else {
                        world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }

                    if (filterMode) consumeCorrosive = true;
                    if (rawWill >= RAW_DRAIN) consumeRaw = true;

                    ctx.syphon(getRefreshCost());
                    k++;
                    lastPos = new BlockPos(i, j, k);
                    drainAspects(will, world, masterPos, consumeRaw, consumeSteadfast, consumeCorrosive);
                    return;
                }
                k = minOffset.getZ();
                i++;
            }
            i = minOffset.getX();
            j--;
        }

        lastPos = new BlockPos(minOffset.getX(), maxOffset.getY(), minOffset.getZ());
    }

    private boolean isCandidate(BlockState state, Level world, BlockPos pos) {
        if (state.isAir()) return false;
        if (state.getBlock() instanceof LiquidBlock) return false;
        if (state.getDestroySpeed(world, pos) < 0) return false;
        if (state.getBlock() instanceof BlockRitualStone) return false;
        if (state.getBlock() instanceof BlockMasterRitualStone) return false;
        return true;
    }

    private boolean filterMatches(BlockState state, Set<Item> filter) {
        ItemStack blockAsItem = new ItemStack(state.getBlock().asItem());
        return !blockAsItem.isEmpty() && filter.contains(blockAsItem.getItem());
    }

    private Set<Item> buildFilter(RitualContext ctx, BlockPos masterPos) {
        List<BlockPos> chestPositions = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, masterPos);
        if (chestPositions.isEmpty()) return null;
        BlockEntity tile = ctx.level().getBlockEntity(chestPositions.get(0));
        if (tile == null) return null;
        IItemHandler handler = Utils.getInventory(tile, null);
        if (handler == null) return null;

        Set<Item> filter = new HashSet<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) filter.add(stack.getItem());
        }
        return filter;
    }

    private BlockPos findFreePlacementSlot(RitualContext ctx, BlockPos masterPos) {
        for (BlockPos offset : RitualHelper.getRangePositions(ctx.master(), this, PLACEMENT_RANGE, masterPos)) {
            if (ctx.level().isEmptyBlock(offset)) {
                return offset.immutable();
            }
        }
        return null;
    }

    private void drainAspects(SpiritusState will, Level world, BlockPos masterPos,
                              boolean raw, boolean steadfast, boolean corrosive) {
        if (raw) will.use(SpiritusType.RAW, RAW_DRAIN);
        if (steadfast) will.use(SpiritusType.INVICTUS, STEADFAST_DRAIN);
        if (corrosive) will.use(SpiritusType.RUINA, CORROSIVE_DRAIN);
        will.drain(world, masterPos);
    }

    private static int clamp(int value, int lo, int hi) {
        return Math.min(hi, Math.max(lo, value));
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }


    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        if (tag.contains("lastPosX")) {
            lastPos = new BlockPos(tag.getInt("lastPosX"), tag.getInt("lastPosY"), tag.getInt("lastPosZ"));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (lastPos != null) {
            tag.putInt("lastPosX", lastPos.getX());
            tag.putInt("lastPosY", lastPos.getY());
            tag.putInt("lastPosZ", lastPos.getZ());
        }
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".spiritus.raw"),
                Component.translatable(getTranslationKey() + ".spiritus.invictus"),
                Component.translatable(getTranslationKey() + ".spiritus.ruina")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.BLANK);
        addParallelRunes(components, 4, 0, EnumRuneType.EARTH);
        addOffsetRunes(components, 1, 3, 0, EnumRuneType.WATER);
        addOffsetRunes(components, 3, 1, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 3, 1, EnumRuneType.AIR);
        addParallelRunes(components, 2, 1, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 2, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 3, EnumRuneType.AIR);
        addCornerRunes(components, 2, 3, EnumRuneType.FIRE);
        addOffsetRunes(components, 2, 1, 3, EnumRuneType.FIRE);
        addOffsetRunes(components, 1, 2, 3, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualYawningVoid();
    }
}
