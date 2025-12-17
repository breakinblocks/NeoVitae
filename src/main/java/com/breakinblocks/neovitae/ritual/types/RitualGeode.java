package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of the Geode's Bounty - Accelerates amethyst crystal growth.
 * Also generates budding amethyst blocks over time.
 */
public class RitualGeode extends Ritual {

    public static final String GEODE_RANGE = "geodeRange";

    public RitualGeode() {
        super("geode", 0, 10000, "ritual." + NeoVitae.MODID + ".geode");
        addBlockRange(GEODE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(GEODE_RANGE, 3000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, GEODE_RANGE, ctx.masterPos());
        int totalCost = 0;
        UUID owner = ctx.master().getOwner();

        // Find budding amethyst and grow crystals
        List<BlockPos> buddingBlocks = new ArrayList<>();
        List<BlockPos> growableBlocks = new ArrayList<>();

        for (BlockPos pos : positions) {
            BlockState state = ctx.level().getBlockState(pos);

            if (state.is(Blocks.BUDDING_AMETHYST)) {
                buddingBlocks.add(pos);
            } else if (state.getBlock() instanceof AmethystClusterBlock) {
                growableBlocks.add(pos);
            }
        }

        // Grow existing crystals
        for (BlockPos pos : growableBlocks) {
            if (totalCost + getRefreshCost() > ctx.currentEssence()) break;

            BlockState state = ctx.level().getBlockState(pos);
            Block block = state.getBlock();

            Block nextStage = getNextGrowthStage(block);
            if (nextStage != null && ctx.level().random.nextFloat() < 0.3f) {
                BlockState newState = nextStage.defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING, state.getValue(AmethystClusterBlock.FACING));
                // Replace with protection check
                if (BlockProtectionHelper.tryReplaceBlock(ctx.level(), pos, newState, owner)) {
                    totalCost += getRefreshCost();
                }
            }
        }

        // Occasionally spawn new buds on budding amethyst
        for (BlockPos pos : buddingBlocks) {
            if (totalCost + getRefreshCost() * 2 > ctx.currentEssence()) break;

            if (ctx.level().random.nextFloat() < 0.1f) {
                // Try to place a small bud on an adjacent air block
                for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                    BlockPos adjacent = pos.relative(dir);
                    if (ctx.level().isEmptyBlock(adjacent)) {
                        BlockState budState = Blocks.SMALL_AMETHYST_BUD.defaultBlockState()
                                .setValue(AmethystClusterBlock.FACING, dir);
                        // Place with protection check
                        if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), adjacent, budState, owner)) {
                            totalCost += getRefreshCost() * 2;
                            break;
                        }
                    }
                }
            }
        }

        ctx.syphon(totalCost);
    }

    private Block getNextGrowthStage(Block current) {
        if (current == Blocks.SMALL_AMETHYST_BUD) return Blocks.MEDIUM_AMETHYST_BUD;
        if (current == Blocks.MEDIUM_AMETHYST_BUD) return Blocks.LARGE_AMETHYST_BUD;
        if (current == Blocks.LARGE_AMETHYST_BUD) return Blocks.AMETHYST_CLUSTER;
        return null;
    }

    @Override
    public int getRefreshTime() {
        return 100; // Every 5 seconds
    }

    @Override
    public int getRefreshCost() {
        return 50;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGeode();
    }
}
