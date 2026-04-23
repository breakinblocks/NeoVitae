package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class BloodBatteryBlockEntity extends BaseBlockEntity {

    public static final int CAPACITY = 10_000_000;
    private static final int MAX_TRANSFER = 100_000;

    private final SimpleEnergyHandler energy = new SimpleEnergyHandler(CAPACITY, MAX_TRANSFER, MAX_TRANSFER) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

    public BloodBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.BLOOD_BATTERY_TYPE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        int stored = energy.getAmountAsInt();
        int max = energy.getCapacityAsInt();
        if (level.hasNeighborSignal(worldPosition) && stored < max) {
            try (Transaction tx = Transaction.openRoot()) {
                energy.insert(max - stored, tx);
                tx.commit();
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        energy.deserialize(tag);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        energy.serialize(tag);
    }

    public static @Nullable EnergyHandler getEnergyHandler(BloodBatteryBlockEntity tile, @Nullable Direction direction) {
        return tile.energy;
    }

    public int getEnergyStored() {
        return energy.getAmountAsInt();
    }

    public int getMaxEnergyStored() {
        return energy.getCapacityAsInt();
    }
}
