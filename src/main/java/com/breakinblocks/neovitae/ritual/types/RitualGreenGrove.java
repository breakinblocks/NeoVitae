package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that accelerates plant growth in a configurable area.
 */
public class RitualGreenGrove extends Ritual {

    public static final String GROWTH_RANGE = "growthRange";

    public RitualGreenGrove() {
        super("green_grove", 0, 1000, "ritual." + NeoVitae.MODID + ".green_grove");
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-3, 1, -3), 7, 5, 7));
        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 1000, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel serverLevel)) return;

        int maxGrowths = ctx.maxOperations(getRefreshCost());
        int totalGrowths = 0;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, GROWTH_RANGE, ctx.masterPos());
        for (BlockPos pos : positions) {
            if (totalGrowths >= maxGrowths) break;

            BlockState state = ctx.level().getBlockState(pos);
            if (state.getBlock() instanceof BonemealableBlock growable) {
                if (growable.isValidBonemealTarget(ctx.level(), pos, state)) {
                    if (growable.isBonemealSuccess(ctx.level(), ctx.level().random, pos, state)) {
                        growable.performBonemeal(serverLevel, ctx.level().random, pos, state);
                        totalGrowths++;
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * totalGrowths);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGreenGrove();
    }
}
