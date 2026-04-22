package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public class BloodBatteryBlockEntity extends BaseBlockEntity {

    public static final int CAPACITY = 10_000_000;
    private static final int MAX_TRANSFER = 100_000;

    private final EnergyStorage energy = new EnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) setChanged();
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) setChanged();
            return extracted;
        }
    };

    public BloodBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.BLOOD_BATTERY_TYPE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (level.hasNeighborSignal(worldPosition) && energy.getEnergyStored() < energy.getMaxEnergyStored()) {
            energy.receiveEnergy(energy.getMaxEnergyStored() - energy.getEnergyStored(), false);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.child("energy").ifPresent(energy::deserialize);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        energy.serialize(tag.child("energy"));
    }

    public static @Nullable EnergyStorage getEnergyHandler(BloodBatteryBlockEntity tile, @Nullable Direction direction) {
        return tile.energy;
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energy.getMaxEnergyStored();
    }
}
