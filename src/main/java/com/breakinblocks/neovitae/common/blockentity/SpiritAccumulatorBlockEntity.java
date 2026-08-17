package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.spiritus.ISpiritusStorage;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.AccumulatorContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public class SpiritAccumulatorBlockEntity extends RoutingNodeBlockEntity implements ISpiritusStorage {
    public static final double CAPACITY = 1000.0;
    public static final double SATURATION_FLOOR = 30.0;
    public static final double FILL_RATE = 25.0;
    public static final double TIP_HEIGHT = 0.85;

    @Nullable
    private SpiritusType attunedType;
    private boolean locked;
    private double stored;
    private int syncTimer;

    public SpiritAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPIRIT_ACCUMULATOR_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiritAccumulatorBlockEntity tile) {
        tile.tick(level, pos, state);
        if (level.isClientSide()) return;
        tile.serverTick();
    }

    private void serverTick() {
        if (!locked || attunedType == null || stored >= CAPACITY || level == null) return;

        double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(level, worldPosition, attunedType);
        double available = chunkAmount - SATURATION_FLOOR;
        if (available <= 0) return;

        double request = Math.min(FILL_RATE, Math.min(available, CAPACITY - stored));
        double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, worldPosition, attunedType, request);
        if (drained <= 0) return;

        stored += drained;
        if (++syncTimer >= 20 || stored >= CAPACITY) {
            syncTimer = 0;
            syncToClients();
        }
        setChanged();
    }

    public void syncToClients() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static float bobOffset(BlockPos pos) {
        float phase = ((pos.getX() * 31 + pos.getY() * 13 + pos.getZ() * 17) & 0xFF) / 255f * Mth.TWO_PI;
        float angle = (System.currentTimeMillis() % 4000L) / 4000f * Mth.TWO_PI;
        return Mth.sin(angle + phase) * 0.05f;
    }

    @Override
    public Vec3 getBeamAnchor() {
        BlockPos pos = getBlockPos();
        return new Vec3(pos.getX() + 0.5, pos.getY() + TIP_HEIGHT + bobOffset(pos), pos.getZ() + 0.5);
    }

    @Override
    @Nullable
    public SpiritusType getStoredType() {
        return attunedType;
    }

    @Nullable
    public SpiritusType getAttunedType() {
        return attunedType;
    }

    public boolean isLocked() {
        return locked;
    }

    @Nullable
    public SpiritusType cycleAttunement() {
        if (locked) return attunedType;
        SpiritusType[] types = SpiritusType.values();
        if (attunedType == null) {
            attunedType = types[0];
        } else {
            int next = attunedType.ordinal() + 1;
            attunedType = next >= types.length ? null : types[next];
        }
        setChanged();
        syncToClients();
        return attunedType;
    }

    public boolean lock() {
        if (locked || attunedType == null) return false;
        locked = true;
        setChanged();
        syncToClients();
        return true;
    }

    public boolean unlock() {
        if (!locked || stored > 0) return false;
        locked = false;
        setChanged();
        syncToClients();
        return true;
    }

    public boolean attuneTo(SpiritusType type) {
        if (attunedType == type && locked) return false;
        if (attunedType != type && stored > 0) return false;
        attunedType = type;
        locked = true;
        setChanged();
        syncToClients();
        return true;
    }

    @Override
    public double getStored() {
        return stored;
    }

    @Override
    public double getCapacity() {
        return CAPACITY;
    }

    @Override
    public double insert(SpiritusType type, double amount, boolean simulate) {
        if (amount <= 0 || !canAccept(type)) return 0;
        double inserted = Math.min(amount, CAPACITY - stored);
        if (inserted <= 0) return 0;
        if (!simulate) {
            stored += inserted;
            setChanged();
            syncToClients();
        }
        return inserted;
    }

    @Override
    public double extract(SpiritusType type, double amount, boolean simulate) {
        if (amount <= 0 || attunedType != type) return 0;
        double extracted = Math.min(amount, stored);
        if (extracted <= 0) return 0;
        if (!simulate) {
            stored -= extracted;
            setChanged();
            syncToClients();
        }
        return extracted;
    }

    public float getFillFraction() {
        return (float) Math.min(1.0, stored / CAPACITY);
    }

    public boolean canAccept(SpiritusType type) {
        return locked && attunedType == type && stored < CAPACITY;
    }

    public boolean insertSpiritus(SpiritusType type, double amount) {
        return insert(type, amount, false) > 0;
    }

    public double vent(double amount) {
        if (attunedType == null || stored <= 0 || level == null) return 0;
        double toVent = Math.min(amount, stored);
        WorldSpiritusHandler.addSpiritusToChunk(level, worldPosition, attunedType, toVent);
        stored -= toVent;
        if (stored <= 0.0001) {
            stored = 0;
        }
        setChanged();
        syncToClients();
        return toVent;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        AccumulatorContent content = componentInput.getOrDefault(NVDataComponents.ACCUMULATOR_CONTENT.get(), AccumulatorContent.EMPTY);
        attunedType = content.typeOrNull();
        stored = Math.min(content.stored(), CAPACITY);
        locked = content.locked() && attunedType != null;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (attunedType == null && stored <= 0 && !locked) return;
        components.set(NVDataComponents.ACCUMULATOR_CONTENT.get(), AccumulatorContent.of(attunedType, stored, locked));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("attunedType", attunedType == null ? "" : attunedType.getSerializedName());
        tag.putBoolean("locked", locked);
        tag.putDouble("stored", stored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String typeStr = tag.getString("attunedType");
        attunedType = null;
        if (!typeStr.isEmpty()) {
            for (SpiritusType type : SpiritusType.values()) {
                if (type.getSerializedName().equals(typeStr)) {
                    attunedType = type;
                    break;
                }
            }
        }
        locked = tag.contains("locked") ? tag.getBoolean("locked") : attunedType != null;
        stored = tag.getDouble("stored");
    }
}
