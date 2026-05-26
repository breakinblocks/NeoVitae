package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SlotSideConfig {

    private static final int DIRECTION_COUNT = 6;
    private static final String KEY = "sideConfig";

    private final int slotCount;
    private final long defaultMask;
    private long mask;

    public SlotSideConfig(int slotCount, boolean[][] defaults) {
        if (slotCount <= 0 || slotCount * DIRECTION_COUNT > 64) {
            throw new IllegalArgumentException("slotCount must be > 0 and <= 10");
        }
        if (defaults.length != slotCount) {
            throw new IllegalArgumentException("defaults rows (" + defaults.length + ") != slotCount (" + slotCount + ")");
        }
        long bits = 0L;
        for (int s = 0; s < slotCount; s++) {
            boolean[] row = defaults[s];
            if (row.length != DIRECTION_COUNT) {
                throw new IllegalArgumentException("defaults row " + s + " length " + row.length + " != " + DIRECTION_COUNT);
            }
            for (int d = 0; d < DIRECTION_COUNT; d++) {
                if (row[d]) bits |= bit(s, d);
            }
        }
        this.slotCount = slotCount;
        this.defaultMask = bits;
        this.mask = bits;
    }

    public int slotCount() {
        return slotCount;
    }

    public boolean isAllowed(int slot, Direction dir) {
        if (slot < 0 || slot >= slotCount || dir == null) return false;
        return (mask & bit(slot, dir.ordinal())) != 0L;
    }

    public void setAllowed(int slot, Direction dir, boolean enabled) {
        if (slot < 0 || slot >= slotCount || dir == null) return;
        long b = bit(slot, dir.ordinal());
        if (enabled) mask |= b;
        else mask &= ~b;
    }

    public void save(ValueOutput out) {
        out.putLong(KEY, mask);
    }

    public void load(ValueInput in) {
        mask = in.getLongOr(KEY, defaultMask);
    }

    private static long bit(int slot, int dir) {
        return 1L << (slot * DIRECTION_COUNT + dir);
    }
}
