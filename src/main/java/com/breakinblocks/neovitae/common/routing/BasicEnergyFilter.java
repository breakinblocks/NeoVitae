package com.breakinblocks.neovitae.common.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BasicEnergyFilter implements IEnergyFilter {

    private final BlockEntity tile;
    private final IEnergyStorage storage;
    private final boolean isOutput;

    public BasicEnergyFilter(BlockEntity tile, IEnergyStorage storage, boolean isOutput) {
        this.tile = tile;
        this.storage = storage;
        this.isOutput = isOutput;
    }

    @Override
    public BlockPos getNodePos() {
        return tile != null ? tile.getBlockPos() : null;
    }

    @Override
    public int transferEnergyThroughOutputFilter(int amount) {
        if (!storage.canReceive()) return 0;

        int received = storage.receiveEnergy(amount, false);
        if (received > 0 && tile != null) {
            Level level = tile.getLevel();
            BlockPos pos = tile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }
        return received;
    }

    @Override
    public int transferThroughInputFilter(IEnergyFilter outputFilter, int maxTransfer) {
        if (!storage.canExtract()) return 0;

        int available = storage.extractEnergy(maxTransfer, true);
        if (available <= 0) return 0;

        int accepted = outputFilter.transferEnergyThroughOutputFilter(available);
        if (accepted <= 0) return 0;

        storage.extractEnergy(accepted, false);

        if (tile != null) {
            Level level = tile.getLevel();
            BlockPos pos = tile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        return accepted;
    }
}
