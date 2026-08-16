package com.breakinblocks.neovitae.api.routing;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public interface ISpiritusFilter extends IRoutingFilter {

    int receiveSpiritus(SpiritusType type, int amount);

    int transferThroughInputFilter(ISpiritusFilter outputFilter, int maxTransfer);
}
