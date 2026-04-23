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
import java.util.Iterator;
import java.util.List;

/**
 * Whitelist fluid filter implementation.
 * As an output filter, it fills until the requested amount.
 * As an input filter, it only pulls until the requested amount.
 */
public class BasicFluidFilter implements IFluidFilter {

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

        if (isFilterOutput) {
            for (int tank = 0; tank < fluidHandler.size(); tank++) {
                FluidStack checkedFluid = fluidAt(tank);
                if (checkedFluid.isEmpty()) continue;

                int fluidAmount = checkedFluid.getAmount();

                for (FluidStack filterFluid : requestList) {
                    if (filterFluid.getAmount() == 0) continue;

                    if (doFluidsMatch(filterFluid, checkedFluid)) {
                        filterFluid.setAmount(Math.max(filterFluid.getAmount() - fluidAmount, 0));
                    }
                }
            }
        } else {
            for (FluidStack filterFluid : requestList) {
                int maxPull = filterFluid.getAmount();
                int available = 0;

                for (int tank = 0; tank < fluidHandler.size(); tank++) {
                    FluidStack checkedFluid = fluidAt(tank);
                    if (!checkedFluid.isEmpty() && doFluidsMatch(filterFluid, checkedFluid)) {
                        available += checkedFluid.getAmount();
                    }
                }

                filterFluid.setAmount(Math.min(maxPull, available));
            }
        }

        requestList.removeIf(FluidStack::isEmpty);
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

    @Override
    public FluidStack transferFluidThroughOutputFilter(FluidStack inputFluid) {
        int allowedAmount = 0;
        for (FluidStack filterFluid : requestList) {
            if (doFluidsMatch(filterFluid, inputFluid)) {
                allowedAmount = Math.min(filterFluid.getAmount(), inputFluid.getAmount());
                break;
            }
        }

        if (allowedAmount <= 0) {
            return inputFluid;
        }

        int filled;
        try (Transaction tx = Transaction.openRoot()) {
            filled = fluidHandler.insert(FluidResource.of(inputFluid), allowedAmount, tx);
            tx.commit();
        }

        FluidStack remainderFluid = inputFluid.copy();
        remainderFluid.shrink(filled);

        Iterator<FluidStack> itr = requestList.iterator();
        while (itr.hasNext()) {
            FluidStack filterFluid = itr.next();
            if (doFluidsMatch(filterFluid, inputFluid)) {
                filterFluid.shrink(filled);
                if (filterFluid.isEmpty()) {
                    itr.remove();
                }
            }
        }

        if (accessedTile != null) {
            Level level = accessedTile.getLevel();
            BlockPos pos = accessedTile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        return remainderFluid.isEmpty() ? FluidStack.EMPTY : remainderFluid;
    }

    @Override
    public int transferThroughInputFilter(IFluidFilter outputFilter, int maxTransfer) {
        int totalChange = 0;

        for (int tank = 0; tank < fluidHandler.size(); tank++) {
            FluidStack inputFluid = fluidAt(tank);
            if (inputFluid.isEmpty()) continue;

            int simulated = simulateExtract(tank, inputFluid);
            if (simulated <= 0) continue;

            int allowedAmount = 0;
            for (FluidStack filterFluid : requestList) {
                if (doFluidsMatch(filterFluid, inputFluid)) {
                    allowedAmount = Math.min(maxTransfer, Math.min(filterFluid.getAmount(), simulated));
                    break;
                }
            }

            if (allowedAmount <= 0) continue;

            FluidStack testFluid = inputFluid.copy();
            testFluid.setAmount(allowedAmount);
            FluidStack remainderFluid = outputFilter.transferFluidThroughOutputFilter(testFluid);
            int changeAmount = allowedAmount - (remainderFluid.isEmpty() ? 0 : remainderFluid.getAmount());

            if (changeAmount <= 0) continue;

            extractCommitted(tank, inputFluid, changeAmount);

            Iterator<FluidStack> itr = requestList.iterator();
            while (itr.hasNext()) {
                FluidStack filterFluid = itr.next();
                if (doFluidsMatch(filterFluid, inputFluid)) {
                    filterFluid.shrink(changeAmount);
                    if (filterFluid.isEmpty()) {
                        itr.remove();
                    }
                }
            }

            if (accessedTile != null) {
                Level level = accessedTile.getLevel();
                BlockPos pos = accessedTile.getBlockPos();
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            }

            maxTransfer -= changeAmount;
            totalChange += changeAmount;
            if (maxTransfer <= 0) {
                return totalChange;
            }
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

    private void extractCommitted(int tank, FluidStack input, int amount) {
        FluidResource r = fluidHandler.getResource(tank);
        if (r.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot()) {
            fluidHandler.extract(tank, r, amount, tx);
            tx.commit();
        }
    }

    @Override
    public boolean doesFluidPassFilter(FluidStack testFluid) {
        for (FluidStack filterFluid : requestList) {
            if (doFluidsMatch(filterFluid, testFluid)) {
                return true;
            }
        }
        return false;
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
