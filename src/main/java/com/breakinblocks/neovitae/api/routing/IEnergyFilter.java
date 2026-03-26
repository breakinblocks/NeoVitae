package com.breakinblocks.neovitae.api.routing;

public interface IEnergyFilter extends IRoutingFilter {

    int transferEnergyThroughOutputFilter(int amount);

    int transferThroughInputFilter(IEnergyFilter outputFilter, int maxTransfer);
}
