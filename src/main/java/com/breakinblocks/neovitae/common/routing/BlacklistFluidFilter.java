package com.breakinblocks.neovitae.common.routing;

import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BlacklistFluidFilter implements IFluidFilter {

    protected List<FluidStack> requestList;
    protected BlockEntity accessedTile;
    protected ResourceHandler<FluidResource> fluidHandler;

    @Override
    public void initializeFilter(List<FluidStack> filteredFluids, BlockEntity tile, ResourceHandler<FluidResource> fluidHandler, boolean isFilterOutput) {
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

        int filled;
        try (Transaction tx = Transaction.openRoot()) {
            filled = fluidHandler.insert(FluidResource.of(inputFluid), inputFluid.getAmount(), tx);
            tx.commit();
        }

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

        for (int tank = 0; tank < fluidHandler.size(); tank++) {
            FluidStack inputFluid = fluidAt(tank);
            if (inputFluid.isEmpty()) continue;
            if (isBlacklisted(inputFluid)) continue;

            int simulated = simulateExtract(tank, inputFluid);
            if (simulated <= 0) continue;

            int allowedAmount = Math.min(maxTransfer, simulated);
            if (allowedAmount <= 0) continue;

            FluidStack testFluid = inputFluid.copy();
            testFluid.setAmount(allowedAmount);
            FluidStack remainderFluid = outputFilter.transferFluidThroughOutputFilter(testFluid);
            int changeAmount = allowedAmount - (remainderFluid.isEmpty() ? 0 : remainderFluid.getAmount());

            if (changeAmount <= 0) continue;

            extractCommitted(tank, changeAmount);

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

    private FluidStack fluidAt(int tank) {
        FluidResource r = fluidHandler.getResource(tank);
        return r.isEmpty() ? FluidStack.EMPTY : r.toStack(fluidHandler.getAmountAsInt(tank));
    }

    private int simulateExtract(int tank, FluidStack input) {
        FluidResource r = fluidHandler.getResource(tank);
        if (r.isEmpty()) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            return fluidHandler.extract(tank, r, input.getAmount(), tx);
        }
    }

    private void extractCommitted(int tank, int amount) {
        FluidResource r = fluidHandler.getResource(tank);
        if (r.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot()) {
            fluidHandler.extract(tank, r, amount, tx);
            tx.commit();
        }
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
