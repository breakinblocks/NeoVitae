package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.api.routing.ISpiritusFilter;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public class ChunkStockSpiritusFilter implements ISpiritusFilter {

    private final BlockEntity tile;
    private final SpiritusType exportType;
    private final double stockTarget;

    public ChunkStockSpiritusFilter(BlockEntity tile, SpiritusType exportType, double stockTarget) {
        this.tile = tile;
        this.exportType = exportType;
        this.stockTarget = stockTarget;
    }

    @Override
    public BlockPos getNodePos() {
        return tile != null ? tile.getBlockPos() : null;
    }

    @Override
    public int receiveSpiritus(SpiritusType type, int amount) {
        if (type != exportType || tile == null) return 0;
        Level level = tile.getLevel();
        BlockPos pos = tile.getBlockPos();

        double current = WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);
        double cap = Math.min(stockTarget, WorldSpiritusHandler.getMaxSpiritus(level, pos, type));
        int toAdd = (int) Math.floor(Math.min(amount, cap - current));
        if (toAdd <= 0) return 0;

        return (int) Math.floor(WorldSpiritusHandler.addSpiritusToChunk(level, pos, type, toAdd));
    }

    @Override
    public int transferThroughInputFilter(ISpiritusFilter outputFilter, int maxTransfer) {
        return 0;
    }
}
