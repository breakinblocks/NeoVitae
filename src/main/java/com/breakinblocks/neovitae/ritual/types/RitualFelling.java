// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.*;
import java.util.function.Consumer;

/**
 * Ritual that cuts down trees in the configured area.
 * Drops are inserted into a chest inventory if available, otherwise spawned in the world.
 */
public class RitualFelling extends Ritual {

    public static final String FELL_RANGE = "fellRange";
    public static final String CHEST_RANGE = "chestRange";
    private static final int MAX_BLOCKS_PER_OPERATION = 128;

    public RitualFelling() {
        super("felling", 0, 2000, "ritual." + NeoVitae.MODID + ".felling");
        addBlockRange(FELL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, 0, -10), 21, 30, 21));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(FELL_RANGE, 15000, 15, 40);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        ServerLevel serverLevel = ctx.serverLevel();

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, FELL_RANGE, ctx.masterPos());
        UUID owner = ctx.master().getOwner();
        int blocksBroken = 0;
        int maxBlocks = Math.min(ctx.maxOperations(getRefreshCost()), MAX_BLOCKS_PER_OPERATION);

        ItemStack toolStack = new ItemStack(Items.NETHERITE_AXE);

        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(serverLevel, owner, "NeoVitae");

        RitualHelper.ChestOutput chest = RitualHelper.resolveChestOutput(ctx, this, CHEST_RANGE);
        BlockEntity inv = chest.tile();
        boolean hasInv = chest.hasFreeSlot();

        List<ItemStack> allDrops = new ArrayList<>();
        List<BlockPos> replantSpots = new ArrayList<>();

        BlockPos masterPos = ctx.masterPos();
        for (BlockPos pos : positions) {
            if (blocksBroken >= maxBlocks) break;

            BlockState state = ctx.level().getBlockState(pos);
            if (state.is(BlockTags.LOGS)) {
                if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                    if (!ctx.level().getBlockState(pos.below()).is(BlockTags.LOGS)) {
                        replantSpots.add(pos);
                    }
                    int broken = breakAndCollect(ctx, serverLevel, pos, state, toolStack, fakePlayer, allDrops);
                    blocksBroken += broken;
                    if (broken > 0) {
                        RitualHelper.chanceStream(ctx.level(), 8, () ->
                                StreamPresets.lifePulse(pos, masterPos).color(0x44CC33).build()
                                        .sendToNearby(ctx.serverLevel(), masterPos, 64));
                    }
                }
            }
        }

        // If we still have capacity, break leaves
        if (blocksBroken < maxBlocks) {
            for (BlockPos pos : positions) {
                if (blocksBroken >= maxBlocks) break;

                BlockState state = ctx.level().getBlockState(pos);
                if (state.is(BlockTags.LEAVES)) {
                    if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                        blocksBroken += breakAndCollect(ctx, serverLevel, pos, state, toolStack, fakePlayer, allDrops);
                    }
                }
            }
        }

        for (BlockPos spot : replantSpots) {
            tryReplant(serverLevel, spot, allDrops);
        }

        RitualHelper.distributeDrops(allDrops, hasInv ? inv : null,
                stack -> Block.popResource(ctx.level(), masterPos, stack));

        ctx.syphon(getRefreshCost() * blocksBroken);
    }

    /**
     * Breaks a block and collects its drops into the supplied sink.
     * @return 1 if the block was broken, 0 otherwise
     */
    private int breakAndCollect(RitualContext ctx, ServerLevel serverLevel, BlockPos pos,
                                BlockState state, ItemStack toolStack, FakePlayer fakePlayer,
                                List<ItemStack> dropSink) {
        List<ItemStack> drops = RitualHelper.getBlockDrops(serverLevel, state, pos, toolStack, fakePlayer);

        ctx.level().destroyBlock(pos, false);

        dropSink.addAll(drops);

        return 1;
    }

    private boolean tryReplant(ServerLevel level, BlockPos pos, List<ItemStack> drops) {
        if (!level.getBlockState(pos).isAir()) return false;

        for (ItemStack stack : drops) {
            if (stack.isEmpty() || !stack.is(ItemTags.SAPLINGS)) continue;
            if (!(stack.getItem() instanceof BlockItem blockItem)) continue;

            BlockState plant = blockItem.getBlock().defaultBlockState();
            if (!plant.canSurvive(level, pos)) continue;

            level.setBlock(pos, plant, Block.UPDATE_ALL);
            stack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addRune(components, 3, 0, 0, EnumRuneType.EARTH);
        addRune(components, -3, 0, 0, EnumRuneType.EARTH);
        addRune(components, 0, 0, 3, EnumRuneType.EARTH);
        addRune(components, 0, 0, -3, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFelling();
    }
}
