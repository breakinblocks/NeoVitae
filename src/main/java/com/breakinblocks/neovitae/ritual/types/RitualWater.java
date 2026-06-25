// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that generates water in a configurable area.
 * With spiritus, can also fill fluid tanks.
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

        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), ctx.masterPos(), 1.0);
        if (will.hasRaw()) {
            List<BlockPos> tankPositions = RitualHelper.getRangePositions(ctx.master(), this, TANK_RANGE, ctx.masterPos());

            for (BlockPos tankPos : tankPositions) {
                IFluidHandler fluidHandler = ctx.level().getCapability(Capabilities.FluidHandler.BLOCK, tankPos, null);
                if (fluidHandler != null) {
                    FluidStack waterStack = new FluidStack(Fluids.WATER, 1000);
                    int filled = fluidHandler.fill(waterStack, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        totalEffects++;
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * totalEffects);
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
