package com.breakinblocks.neovitae.common.routing;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * Interface for fluid filters used in the routing system.
 * Mirrors IItemFilter but for fluid transfer between tanks.
 */
public interface IFluidFilter extends IRoutingFilter {

    /**
     * Initializes the filter with the given filter list and tank context.
     * @param filteredFluids The list of fluid stacks to filter
     * @param tile The block entity this filter is attached to
     * @param fluidHandler The fluid handler to filter
     * @param isFilterOutput True if this is an output filter
     */
    void initializeFilter(List<FluidStack> filteredFluids, BlockEntity tile, IFluidHandler fluidHandler, boolean isFilterOutput);

    /**
     * Initializes the filter with just the filter list (no tank context).
     */
    void initializeFilter(List<FluidStack> filteredFluids);

    /**
     * Transfers a fluid stack through the output filter.
     * Called when the output tank receives fluid.
     * @param inputFluid The fluid to filter
     * @return The remainder after absorption into the tank
     */
    FluidStack transferFluidThroughOutputFilter(FluidStack inputFluid);

    /**
     * Transfers fluid from the input tank to the output filter.
     * @param outputFilter The output filter to transfer to
     * @param maxTransfer Maximum amount of fluid to transfer (in mB)
     * @return The amount of fluid actually transferred
     */
    int transferThroughInputFilter(IFluidFilter outputFilter, int maxTransfer);

    /**
     * Checks if the given fluid passes through this filter.
     */
    boolean doesFluidPassFilter(FluidStack testFluid);

    /**
     * Checks if the filter fluid matches the test fluid.
     */
    boolean doFluidsMatch(FluidStack filterFluid, FluidStack testFluid);

    /**
     * Gets the filter list. The returned list should not be modified.
     */
    List<FluidStack> getFilterList();
}
