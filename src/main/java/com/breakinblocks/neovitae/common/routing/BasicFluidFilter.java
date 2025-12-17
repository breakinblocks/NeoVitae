package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

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
    protected IFluidHandler fluidHandler;

    @Override
    public void initializeFilter(List<FluidStack> filteredFluids, BlockEntity tile, IFluidHandler fluidHandler, boolean isFilterOutput) {
        this.accessedTile = tile;
        this.fluidHandler = fluidHandler;
        this.requestList = new ArrayList<>();

        // Deep copy the filtered fluids
        for (FluidStack fluid : filteredFluids) {
            if (!fluid.isEmpty()) {
                requestList.add(fluid.copy());
            }
        }

        if (isFilterOutput) {
            // Adjust counts based on what's already in the tanks
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                FluidStack checkedFluid = fluidHandler.getFluidInTank(tank);
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
            // Input filter: invert counts
            for (FluidStack filterFluid : requestList) {
                filterFluid.setAmount(filterFluid.getAmount() * -1);
            }

            // Add back what's in the tanks
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                FluidStack checkedFluid = fluidHandler.getFluidInTank(tank);
                if (checkedFluid.isEmpty()) continue;

                int fluidAmount = checkedFluid.getAmount();

                for (FluidStack filterFluid : requestList) {
                    if (doFluidsMatch(filterFluid, checkedFluid)) {
                        filterFluid.grow(fluidAmount);
                    }
                }
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

        FluidStack testFluid = inputFluid.copy();
        testFluid.setAmount(allowedAmount);

        int filled = fluidHandler.fill(testFluid, IFluidHandler.FluidAction.EXECUTE);
        int changeAmount = filled;

        FluidStack remainderFluid = inputFluid.copy();
        remainderFluid.shrink(changeAmount);

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

        return remainderFluid.isEmpty() ? FluidStack.EMPTY : remainderFluid;
    }

    @Override
    public int transferThroughInputFilter(IFluidFilter outputFilter, int maxTransfer) {
        int totalChange = 0;

        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            FluidStack inputFluid = fluidHandler.getFluidInTank(tank);
            if (inputFluid.isEmpty()) {
                continue;
            }

            // Check if we can extract
            FluidStack drainTest = fluidHandler.drain(inputFluid.copy(), IFluidHandler.FluidAction.SIMULATE);
            if (drainTest.isEmpty()) {
                continue;
            }

            int allowedAmount = 0;
            for (FluidStack filterFluid : requestList) {
                if (doFluidsMatch(filterFluid, inputFluid)) {
                    allowedAmount = Math.min(maxTransfer, Math.min(filterFluid.getAmount(), drainTest.getAmount()));
                    break;
                }
            }

            if (allowedAmount <= 0) {
                continue;
            }

            FluidStack testFluid = inputFluid.copy();
            testFluid.setAmount(allowedAmount);
            FluidStack remainderFluid = outputFilter.transferFluidThroughOutputFilter(testFluid);
            int changeAmount = allowedAmount - (remainderFluid.isEmpty() ? 0 : remainderFluid.getAmount());

            if (changeAmount <= 0) {
                continue;
            }

            // Actually drain the fluid
            FluidStack toDrain = inputFluid.copy();
            toDrain.setAmount(changeAmount);
            fluidHandler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);

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
