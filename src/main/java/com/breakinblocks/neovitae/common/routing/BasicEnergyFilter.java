// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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
    private final int rateLimit;

    public BasicEnergyFilter(BlockEntity tile, EnergyHandler storage, boolean isOutput, int rateLimit) {
        this.tile = tile;
        this.storage = storage;
        this.isOutput = isOutput;
        this.rateLimit = Math.max(0, rateLimit);
    }

    @Override
    public BlockPos getNodePos() {
        return tile != null ? tile.getBlockPos() : null;
    }

    @Override
    public int transferEnergyThroughOutputFilter(int amount) {
        int capped = Math.min(amount, rateLimit);
        if (capped <= 0) return 0;

        int received;
        try (Transaction tx = Transaction.openRoot()) {
            received = storage.insert(capped, tx);
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
        int capped = Math.min(maxTransfer, rateLimit);
        if (capped <= 0) return 0;

        int available;
        try (Transaction tx = Transaction.openRoot()) {
            available = storage.extract(capped, tx);
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
