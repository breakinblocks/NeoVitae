package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.*;
import java.util.function.Consumer;

/**
 * Ritual that cuts down trees in the configured area.
 */
public class RitualFelling extends Ritual {

    public static final String FELL_RANGE = "fellRange";
    private static final int MAX_BLOCKS_PER_OPERATION = 128;

    public RitualFelling() {
        super("felling", 0, 2000, "ritual." + NeoVitae.MODID + ".felling");
        addBlockRange(FELL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, 0, -10), 21, 30, 21));
        setMaximumVolumeAndDistanceOfRange(FELL_RANGE, 15000, 15, 40);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel)) return;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, FELL_RANGE, ctx.masterPos());
        UUID owner = ctx.master().getOwner();
        int blocksBroken = 0;
        int maxBlocks = Math.min(ctx.maxOperations(getRefreshCost()), MAX_BLOCKS_PER_OPERATION);

        // Find and break logs first, then leaves
        for (BlockPos pos : positions) {
            if (blocksBroken >= maxBlocks) break;

            BlockState state = ctx.level().getBlockState(pos);
            if (state.is(BlockTags.LOGS)) {
                // Check protection before breaking
                if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                    Block.dropResources(state, ctx.level(), pos);
                    ctx.level().destroyBlock(pos, false);
                    blocksBroken++;
                }
            }
        }

        // If we still have capacity, break leaves
        if (blocksBroken < maxBlocks) {
            for (BlockPos pos : positions) {
                if (blocksBroken >= maxBlocks) break;

                BlockState state = ctx.level().getBlockState(pos);
                if (state.is(BlockTags.LEAVES)) {
                    // Check protection before breaking
                    if (BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                        Block.dropResources(state, ctx.level(), pos);
                        ctx.level().destroyBlock(pos, false);
                        blocksBroken++;
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * blocksBroken);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 10;
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
