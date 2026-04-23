package com.breakinblocks.neovitae.common.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BasicEnergyFilter implements IEnergyFilter {

    private final BlockEntity tile;
    private final EnergyHandler storage;
    private final boolean isOutput;

    public BasicEnergyFilter(BlockEntity tile, EnergyHandler storage, boolean isOutput) {
        this.tile = tile;
        this.storage = storage;
        this.isOutput = isOutput;
    }

    @Override
    public int transferEnergyThroughOutputFilter(int amount) {
        int received;
        try (Transaction tx = Transaction.openRoot()) {
            received = storage.insert(amount, tx);
            tx.commit();
        }
        if (received > 0 && tile != null) {
            Level level = tile.getLevel();
            BlockPos pos = tile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
        return received;
    }

    @Override
    public int transferThroughInputFilter(IEnergyFilter outputFilter, int maxTransfer) {
        int available;
        try (Transaction tx = Transaction.openRoot()) {
            available = storage.extract(maxTransfer, tx);
        }
        if (available <= 0) return 0;

        int accepted = outputFilter.transferEnergyThroughOutputFilter(available);
        if (accepted <= 0) return 0;

        try (Transaction tx = Transaction.openRoot()) {
            storage.extract(accepted, tx);
            tx.commit();
        }

        if (tile != null) {
            Level level = tile.getLevel();
            BlockPos pos = tile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        return accepted;
    }
}
