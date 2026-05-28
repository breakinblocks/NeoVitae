package com.breakinblocks.neovitae.api.spiritus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.EnumMap;

/**
 * Snapshot of spiritus amounts in a chunk, with threshold-based active flags
 * and accumulated usage tracking for batch draining.
 *
 * <p>Created via {@link ISpiritusHandler#querySpiritus(Level, BlockPos, double)}.
 * This is the recommended way for rituals and other systems to interact with
 * spiritus, replacing individual {@code getCurrentSpiritus}/{@code drainSpiritus} calls.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ISpiritusHandler handler = NeoVitaeAPI.getInstance().getSpiritusHandler();
 * SpiritusState will = handler.querySpiritus(level, pos, 0.5);
 *
 * if (will.hasDefault()) {
 *     double scaling = will.getDefault() / 100.0;
 *     // ... use scaling ...
 *     will.use(SpiritusType.RAW, 0.1);
 * }
 *
 * if (will.hasSteadfast()) {
 *     // ... steadfast behavior ...
 *     will.use(SpiritusType.INVICTUS, 0.2);
 * }
 *
 * will.drain(handler, level, pos);
 * }</pre>
 */
public final class SpiritusState {
    private final EnumMap<SpiritusType, Double> amounts = new EnumMap<>(SpiritusType.class);
    private final EnumMap<SpiritusType, Double> used = new EnumMap<>(SpiritusType.class);
    private final double threshold;

    /**
     * Creates a new SpiritusState by querying all will types from the handler.
     * Typically called via {@link ISpiritusHandler#querySpiritus(Level, BlockPos, double)}.
     */
    public SpiritusState(ISpiritusHandler handler, Level level, BlockPos pos, double threshold) {
        this.threshold = threshold;
        for (SpiritusType type : SpiritusType.values()) {
            amounts.put(type, handler.getCurrentSpiritus(level, pos, type));
            used.put(type, 0.0);
        }
    }

    /** Gets the current amount of the specified will type. */
    public double get(SpiritusType type) { return amounts.getOrDefault(type, 0.0); }

    /** Returns true if the specified will type meets or exceeds the threshold. */
    public boolean has(SpiritusType type) { return get(type) >= threshold; }

    public double getDefault() { return get(SpiritusType.RAW); }
    public double getCorrosive() { return get(SpiritusType.RUINA); }
    public double getDestructive() { return get(SpiritusType.NIHILUM); }
    public double getSteadfast() { return get(SpiritusType.INVICTUS); }
    public double getVengeful() { return get(SpiritusType.VINDICTA); }

    public boolean hasDefault() { return has(SpiritusType.RAW); }
    public boolean hasCorrosive() { return has(SpiritusType.RUINA); }
    public boolean hasDestructive() { return has(SpiritusType.NIHILUM); }
    public boolean hasSteadfast() { return has(SpiritusType.INVICTUS); }
    public boolean hasVengeful() { return has(SpiritusType.VINDICTA); }

    /**
     * Records will usage of the specified type. Does not drain immediately —
     * call {@link #drain(ISpiritusHandler, Level, BlockPos)} at the end to apply.
     */
    public void use(SpiritusType type, double amount) {
        used.merge(type, amount, Double::sum);
    }

    /**
     * Drains all accumulated usage from the chunk in a single batch.
     * Call this once at the end of your ritual tick after all {@link #use} calls.
     */
    public void drain(ISpiritusHandler handler, Level level, BlockPos pos) {
        used.forEach((type, amount) -> {
            if (amount > 0) {
                handler.drainSpiritus(level, pos, type, amount);
            }
        });
    }

    /**
     * Convenience overload that drains using the default {@link SpiritusHandler}.
     */
    public void drain(Level level, BlockPos pos) {
        drain(SpiritusHandler.INSTANCE, level, pos);
    }
}
