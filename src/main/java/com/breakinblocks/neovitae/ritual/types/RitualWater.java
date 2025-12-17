package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that generates water in a configurable area.
 * With demon will, can also fill fluid tanks.
 */
public class RitualWater extends Ritual {

    public static final String WATER_RANGE = "waterRange";
    public static final String TANK_RANGE = "tankRange";

    public RitualWater() {
        super("water", 0, 500, "ritual." + NeoVitae.MODID + ".water");
        addBlockRange(WATER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(TANK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(WATER_RANGE, 9, 3, 3);
        setMaximumVolumeAndDistanceOfRange(TANK_RANGE, 1, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        UUID owner = ctx.master().getOwner();
        int maxEffects = ctx.maxOperations(getRefreshCost());
        int totalEffects = 0;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, WATER_RANGE, ctx.masterPos());
        for (BlockPos pos : positions) {
            if (totalEffects >= maxEffects) break;

            if (ctx.level().isEmptyBlock(pos)) {
                if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), pos, Blocks.WATER.defaultBlockState(), owner)) {
                    totalEffects++;
                }
            }
        }

        ctx.syphon(getRefreshCost() * totalEffects);
    }

    @Override
    public int getRefreshTime() {
        return 1;
    }

    @Override
    public int getRefreshCost() {
        return 25;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualWater();
    }
}
