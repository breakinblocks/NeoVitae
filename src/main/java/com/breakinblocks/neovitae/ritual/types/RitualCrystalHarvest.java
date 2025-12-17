package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that harvests demon crystals in the area.
 */
public class RitualCrystalHarvest extends Ritual {

    public static final String HARVEST_RANGE = "harvestRange";

    public RitualCrystalHarvest() {
        super("crystal_harvest", 1, 10000, "ritual." + NeoVitae.MODID + ".crystal_harvest");
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel)) return;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, ctx.masterPos());
        int crystalsHarvested = 0;
        int maxHarvests = ctx.maxOperations(getRefreshCost());
        UUID owner = ctx.master().getOwner();

        for (BlockPos pos : positions) {
            if (crystalsHarvested >= maxHarvests) break;

            BlockState state = ctx.level().getBlockState(pos);
            Block block = state.getBlock();

            // Check if it's a demon crystal block
            if (isDemonCrystal(block)) {
                // Check protection before breaking
                if (BlockProtectionHelper.tryBreakBlock(ctx.level(), pos, owner)) {
                    crystalsHarvested++;
                }
            }
        }

        ctx.syphon(getRefreshCost() * crystalsHarvested);
    }

    private boolean isDemonCrystal(Block block) {
        return block == BMBlocks.RAW_DEMON_CRYSTAL.block().get() ||
               block == BMBlocks.CORROSIVE_DEMON_CRYSTAL.block().get() ||
               block == BMBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block().get() ||
               block == BMBlocks.VENGEFUL_DEMON_CRYSTAL.block().get() ||
               block == BMBlocks.STEADFAST_DEMON_CRYSTAL.block().get();
    }

    @Override
    public int getRefreshTime() {
        return 100;
    }

    @Override
    public int getRefreshCost() {
        return 500;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrystalHarvest();
    }
}
