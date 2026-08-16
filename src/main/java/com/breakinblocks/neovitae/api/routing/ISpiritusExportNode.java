package com.breakinblocks.neovitae.api.routing;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import org.jetbrains.annotations.Nullable;

/**
 * A routing node that stocks the Spiritus Aura of its own chunk from the network.
 */
public interface ISpiritusExportNode {

    @Nullable
    SpiritusType getSpiritusExportType();

    int getSpiritusStockTarget();

    void setSpiritusExport(@Nullable SpiritusType type, int stockTarget);

    void cycleSpiritusType(int direction);

    void adjustSpiritusStock(int delta);
}
