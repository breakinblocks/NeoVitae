package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class SlotSideConfig {

    private static final int DIRECTION_COUNT = 6;
    private static final String KEY_PREFIX = "allowedDirections";

    private final int slotCount;
    private final boolean[][] mask;

    public SlotSideConfig(int slotCount, boolean[][] defaults) {
        if (slotCount <= 0) throw new IllegalArgumentException("slotCount must be > 0");
        if (defaults.length != slotCount) {
            throw new IllegalArgumentException("defaults rows (" + defaults.length + ") != slotCount (" + slotCount + ")");
        }
        this.slotCount = slotCount;
        this.mask = new boolean[slotCount][DIRECTION_COUNT];
        for (int s = 0; s < slotCount; s++) {
            boolean[] row = defaults[s];
            if (row.length != DIRECTION_COUNT) {
                throw new IllegalArgumentException("defaults row " + s + " length " + row.length + " != " + DIRECTION_COUNT);
            }
            System.arraycopy(row, 0, this.mask[s], 0, DIRECTION_COUNT);
        }
    }

    public int slotCount() {
        return slotCount;
    }

    public boolean isAllowed(int slot, Direction dir) {
        if (slot < 0 || slot >= slotCount || dir == null) return false;
        return mask[slot][dir.ordinal()];
    }

    public void setAllowed(int slot, Direction dir, boolean enabled) {
        if (slot < 0 || slot >= slotCount || dir == null) return;
        mask[slot][dir.ordinal()] = enabled;
    }

    public void save(CompoundTag tag) {
        for (int s = 0; s < slotCount; s++) {
            byte[] bytes = new byte[DIRECTION_COUNT];
            for (int d = 0; d < DIRECTION_COUNT; d++) {
                bytes[d] = (byte) (mask[s][d] ? 1 : 0);
            }
            tag.putByteArray(KEY_PREFIX + s, bytes);
        }
    }

    public void load(CompoundTag tag) {
        for (int s = 0; s < slotCount; s++) {
            byte[] bytes = tag.getByteArray(KEY_PREFIX + s);
            int limit = Math.min(bytes.length, DIRECTION_COUNT);
            for (int d = 0; d < limit; d++) {
                mask[s][d] = bytes[d] != 0;
            }
        }
    }
}
