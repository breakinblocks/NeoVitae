// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.routing;

import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class BlacklistFluidFilter implements IFluidFilter {

    protected List<FluidStack> requestList;
    protected BlockEntity accessedTile;
    protected IFluidHandler fluidHandler;

    @Override
    public void initializeFilter(List<FluidStack> filteredFluids, BlockEntity tile, IFluidHandler fluidHandler, boolean isFilterOutput) {
        this.accessedTile = tile;
        this.fluidHandler = fluidHandler;
        this.requestList = new ArrayList<>();
        for (FluidStack fluid : filteredFluids) {
            if (!fluid.isEmpty()) {
                requestList.add(fluid.copy());
            }
        }
    }

    @Override
    public void initializeFilter(List<FluidStack> filteredFluids) {
        this.requestList = new ArrayList<>();
        for (FluidStack fluid : filteredFluids) {
            if (!fluid.isEmpty()) {
                requestList.add(fluid.copy());
            }
        }
    }

    private boolean isBlacklisted(FluidStack fluid) {
        for (FluidStack blocked : requestList) {
            if (doFluidsMatch(blocked, fluid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FluidStack transferFluidThroughOutputFilter(FluidStack inputFluid) {
        if (isBlacklisted(inputFluid)) {
            return inputFluid;
        }

        int filled = fluidHandler.fill(inputFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
        FluidStack remainder = inputFluid.copy();
        remainder.shrink(filled);

        if (filled > 0 && accessedTile != null) {
            Level level = accessedTile.getLevel();
            BlockPos pos = accessedTile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        return remainder.isEmpty() ? FluidStack.EMPTY : remainder;
    }

    @Override
    public int transferThroughInputFilter(IFluidFilter outputFilter, int maxTransfer) {
        int totalChange = 0;

        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            FluidStack inputFluid = fluidHandler.getFluidInTank(tank);
            if (inputFluid.isEmpty()) continue;
            if (isBlacklisted(inputFluid)) continue;

            FluidStack drainTest = fluidHandler.drain(inputFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
            if (drainTest.isEmpty()) continue;

            int allowedAmount = Math.min(maxTransfer, drainTest.getAmount());
            if (allowedAmount <= 0) continue;

            FluidStack testFluid = inputFluid.copy();
            testFluid.setAmount(allowedAmount);
            FluidStack remainderFluid = outputFilter.transferFluidThroughOutputFilter(testFluid);
            int changeAmount = allowedAmount - (remainderFluid.isEmpty() ? 0 : remainderFluid.getAmount());

            if (changeAmount <= 0) continue;

            FluidStack toDrain = inputFluid.copy();
            toDrain.setAmount(changeAmount);
            fluidHandler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);

            if (accessedTile != null) {
                Level level = accessedTile.getLevel();
                BlockPos pos = accessedTile.getBlockPos();
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            }

            maxTransfer -= changeAmount;
            totalChange += changeAmount;
            if (maxTransfer <= 0) return totalChange;
        }

        return totalChange;
    }

    @Override
    public boolean doesFluidPassFilter(FluidStack testFluid) {
        return !isBlacklisted(testFluid);
    }

    @Override
    public boolean doFluidsMatch(FluidStack filterFluid, FluidStack testFluid) {
        return FluidStack.isSameFluidSameComponents(filterFluid, testFluid);
    }

    @Override
    public List<FluidStack> getFilterList() {
        return this.requestList;
    }
}
