// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaCursor;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;
import java.util.function.Consumer;

import com.breakinblocks.neovitae.util.Utils;

public class RitualPlacer extends Ritual {

    public static final String PLACER_RANGE = "placerRange";

    public static final double TIER1_ENTER = 20.0;
    public static final double TIER1_HOLD = 18.0;
    public static final double TIER2_ENTER = 50.0;
    public static final double TIER2_HOLD = 48.0;

    private static final double RAW_PER_BLOCK = 0.01;
    private static final int AURA_REQUERY_TICKS = 20;
    private static final int LOOKUPS_PER_BLOCK = 128;
    private static final int STEPS_PER_LOOKUP = 8;

    private static final int[] TIER_VOLUME = {5000, 20000, 80000};
    private static final int[] TIER_RADIUS = {15, 24, 40};

    private long cursorIndex;
    private long cursorVolume;
    private int tier;
    private double cachedRaw;
    private int auraCooldown;

    public RitualPlacer() {
        super("placer", 0, 5000, "ritual." + NeoVitae.MODID + ".placer");
        addBlockRange(PLACER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 1, -5), 11, 10, 11));
        setMaximumVolumeAndDistanceOfRange(PLACER_RANGE, TIER_VOLUME[0], TIER_RADIUS[0], TIER_RADIUS[0]);
    }

    @Override
    public boolean usesFillMode() {
        return true;
    }

    public static int tierFor(double raw) {
        if (raw >= TIER2_ENTER) return 2;
        if (raw >= TIER1_ENTER) return 1;
        return 0;
    }

    public static int blocksPerRefresh(double raw) {
        if (raw >= TIER2_ENTER) return 8;
        if (raw >= TIER1_ENTER) return Math.min(8, 4 + (int) ((raw - TIER1_ENTER) * 4.0 / (TIER2_ENTER - TIER1_ENTER)));
        return Math.max(1, Math.min(4, 1 + (int) (raw * 3.0 / TIER1_ENTER)));
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        Level level = ctx.level();
        BlockPos masterPos = ctx.masterPos();

        SpiritusState will = null;
        if (--auraCooldown <= 0) {
            auraCooldown = AURA_REQUERY_TICKS / Math.max(1, getRefreshTime());
            will = RitualHelper.querySpiritus(level, masterPos, RAW_PER_BLOCK);
            cachedRaw = will.getRaw();
            tier = tierWithHysteresis(cachedRaw, tier);
        }

        int budget = Math.min(blocksPerRefresh(cachedRaw), ctx.maxOperations(getRefreshCost()));
        if (budget <= 0) return;

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, PLACER_RANGE);
        AreaCursor cursor = AreaCursor.of(range, masterPos);
        if (cursor.volume() <= 0) return;
        if (cursorVolume != cursor.volume() || cursorIndex >= cursor.volume()) {
            cursorVolume = cursor.volume();
            cursorIndex = 0;
        }

        ResourceHandler<ItemResource> inventory = findAdjacentInventory(level, masterPos);
        if (inventory == null) return;

        int slotIndex = findBlockItemSlot(inventory, -1);
        if (slotIndex < 0) return;

        EnumFillMode mode = ctx.master().getFillMode();
        UUID owner = ctx.master().getOwner();

        int lookupBudget = LOOKUPS_PER_BLOCK * budget;
        int stepBudget = STEPS_PER_LOOKUP * lookupBudget;
        int placed = 0;
        int lookups = 0;
        int steps = 0;
        BlockPos lastPlaced = null;

        while (placed < budget && lookups < lookupBudget && steps < stepBudget) {
            if (cursorIndex >= cursor.volume()) cursorIndex = 0;

            if (!cursor.accepts(cursorIndex, mode)) {
                cursorIndex = cursor.skipTo(cursorIndex + 1, mode);
                steps++;
                continue;
            }

            BlockPos pos = cursor.at(cursorIndex);
            cursorIndex++;
            steps++;

            if (!level.isLoaded(pos)) continue;
            lookups++;

            if (!level.isEmptyBlock(pos)) continue;
            if (!BlockProtectionHelper.canPlaceBlock(level, pos, level.getBlockState(pos), owner)) continue;

            ItemStack toPlace = Utils.stackAt(inventory, slotIndex);
            if (toPlace.isEmpty() || !(toPlace.getItem() instanceof BlockItem blockItem)) {
                slotIndex = findBlockItemSlot(inventory, -1);
                if (slotIndex < 0) break;
                continue;
            }

            BlockState stateToPlace = blockItem.getBlock().defaultBlockState();
            if (!stateToPlace.canSurvive(level, pos)) continue;

            if (BlockProtectionHelper.tryPlaceBlock(level, pos, stateToPlace, owner)) {
                Utils.extractItem(inventory, slotIndex, 1, false);
                placed++;
                lastPlaced = pos;
                if (Utils.stackAt(inventory, slotIndex).isEmpty()) {
                    slotIndex = findBlockItemSlot(inventory, -1);
                    if (slotIndex < 0) break;
                }
            }
        }

        if (placed <= 0) return;

        ctx.syphon(getRefreshCost() * placed);

        if (tier > 0) {
            if (will == null) {
                will = RitualHelper.querySpiritus(level, masterPos, RAW_PER_BLOCK);
            }
            will.use(SpiritusType.RAW, RAW_PER_BLOCK * placed);
            will.drain(level, masterPos);
        }

        final BlockPos placedAt = lastPlaced;
        RitualHelper.chanceStream(level, 25, () ->
                StreamPresets.arcaneBolt(masterPos, placedAt).build()
                        .sendToNearby(ctx.serverLevel(), masterPos, 64));
    }

    private static int tierWithHysteresis(double raw, int current) {
        if (current >= 2) return raw >= TIER2_HOLD ? 2 : tierFor(raw);
        if (current == 1) return raw >= TIER2_ENTER ? 2 : (raw >= TIER1_HOLD ? 1 : 0);
        return tierFor(raw);
    }

    private int auraTier(@Nullable IMasterRitualStone master) {
        if (master == null || master.getLevel() == null) return 0;
        return tierFor(RitualHelper.querySpiritus(master.getLevel(), master.getBlockPos(), 0).getRaw());
    }

    @Override
    public int getMaxVolumeForRange(String key, @Nullable IMasterRitualStone master) {
        if (!PLACER_RANGE.equals(key) || getRangeLimit(key) != null) {
            return super.getMaxVolumeForRange(key, master);
        }
        return TIER_VOLUME[auraTier(master)];
    }

    @Override
    public int getMaxHorizontalRadiusForRange(String key, @Nullable IMasterRitualStone master) {
        if (!PLACER_RANGE.equals(key) || getRangeLimit(key) != null) {
            return super.getMaxHorizontalRadiusForRange(key, master);
        }
        return TIER_RADIUS[auraTier(master)];
    }

    @Override
    public int getMaxVerticalRadiusForRange(String key, @Nullable IMasterRitualStone master) {
        if (!PLACER_RANGE.equals(key) || getRangeLimit(key) != null) {
            return super.getMaxVerticalRadiusForRange(key, master);
        }
        return TIER_RADIUS[auraTier(master)];
    }

    private int findBlockItemSlot(ResourceHandler<ItemResource> inventory, int skip) {
        for (int i = 0; i < inventory.size(); i++) {
            if (i == skip) continue;
            ItemStack stack = Utils.stackAt(inventory, i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }

    private ResourceHandler<ItemResource> findAdjacentInventory(Level level, BlockPos pos) {
        for (BlockPos offset : new BlockPos[]{
            pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        }) {
            var rh = level.getCapability(Capabilities.Item.BLOCK, offset, null);
            if (rh != null) {
                return rh;
            }
        }
        return null;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putLong("fillCursor", cursorIndex);
        tag.putLong("fillCursorVolume", cursorVolume);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        cursorIndex = tag.getLongOr("fillCursor", 0L);
        cursorVolume = tag.getLongOr("fillCursorVolume", 0L);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPlacer();
    }
}
