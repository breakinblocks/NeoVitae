package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.api.routing.ISpiritusFilter;
import com.breakinblocks.neovitae.api.spiritus.ISpiritusStorage;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public class AccumulatorSpiritusFilter implements ISpiritusFilter {

    private final BlockEntity tile;
    private final ISpiritusStorage storage;

    public AccumulatorSpiritusFilter(BlockEntity tile, ISpiritusStorage storage) {
        this.tile = tile;
        this.storage = storage;
    }

    @Override
    public BlockPos getNodePos() {
        return tile != null ? tile.getBlockPos() : null;
    }

    @Override
    public int receiveSpiritus(SpiritusType type, int amount) {
        return (int) Math.floor(storage.insert(type, amount, false));
    }

    @Override
    public int transferThroughInputFilter(ISpiritusFilter outputFilter, int maxTransfer) {
        SpiritusType type = storage.getStoredType();
        if (type == null) return 0;

        int available = (int) Math.floor(storage.extract(type, maxTransfer, true));
        if (available <= 0) return 0;

        int accepted = outputFilter.receiveSpiritus(type, available);
        if (accepted <= 0) return 0;

        storage.extract(type, accepted, false);
        return accepted;
    }
}
