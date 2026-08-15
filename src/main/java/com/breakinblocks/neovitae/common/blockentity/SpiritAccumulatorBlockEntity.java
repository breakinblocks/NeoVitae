package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public class SpiritAccumulatorBlockEntity extends BaseBlockEntity {
    public static final double CAPACITY = 1000.0;
    public static final double SATURATION_FLOOR = 75.0;
    public static final double FILL_RATE = 25.0;

    @Nullable
    private SpiritusType attunedType;
    private double stored;
    private int syncTimer;

    public SpiritAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPIRIT_ACCUMULATOR_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiritAccumulatorBlockEntity tile) {
        if (level.isClientSide()) return;
        tile.serverTick();
    }

    private void serverTick() {
        if (attunedType == null || stored >= CAPACITY || level == null) return;

        double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(level, worldPosition, attunedType);
        double available = chunkAmount - SATURATION_FLOOR;
        if (available <= 0) return;

        double request = Math.min(FILL_RATE, Math.min(available, CAPACITY - stored));
        double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, worldPosition, attunedType, request);
        if (drained <= 0) return;

        stored += drained;
        if (++syncTimer >= 20 || stored >= CAPACITY) {
            syncTimer = 0;
            setChanged();
        } else {
            setChangedNoSync();
        }
    }

    @Nullable
    public SpiritusType getAttunedType() {
        return attunedType;
    }

    public double getStored() {
        return stored;
    }

    public float getFillFraction() {
        return (float) Math.min(1.0, stored / CAPACITY);
    }

    public boolean canAccept(SpiritusType type) {
        return attunedType == null || (attunedType == type && stored < CAPACITY);
    }

    public boolean insertSpiritus(SpiritusType type, double amount) {
        if (!canAccept(type)) return false;
        attunedType = type;
        stored = Math.min(CAPACITY, stored + amount);
        setChanged();
        return true;
    }

    public double vent(double amount) {
        if (attunedType == null || stored <= 0 || level == null) return 0;
        double toVent = Math.min(amount, stored);
        WorldSpiritusHandler.addSpiritusToChunk(level, worldPosition, attunedType, toVent);
        stored -= toVent;
        if (stored <= 0.0001) {
            stored = 0;
            attunedType = null;
        }
        setChanged();
        return toVent;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putString("attunedType", attunedType == null ? "" : attunedType.getSerializedName());
        tag.putDouble("stored", stored);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        String typeStr = tag.getStringOr("attunedType", "");
        attunedType = null;
        if (!typeStr.isEmpty()) {
            for (SpiritusType type : SpiritusType.values()) {
                if (type.getSerializedName().equals(typeStr)) {
                    attunedType = type;
                    break;
                }
            }
        }
        stored = tag.getDoubleOr("stored", 0d);
    }
}
