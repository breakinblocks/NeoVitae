package com.breakinblocks.neovitae.api.will;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

import java.util.EnumMap;

/**
 * Snapshot of demon will amounts in a chunk, with threshold-based active flags
 * and accumulated usage tracking for batch draining.
 *
 * <p>Created via {@link IDemonWillHandler#queryWill(Level, BlockPos, double)}.
 * This is the recommended way for rituals and other systems to interact with
 * demon will, replacing individual {@code getCurrentWill}/{@code drainWill} calls.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * IDemonWillHandler handler = NeoVitaeAPI.getInstance().getDemonWillHandler();
 * WillState will = handler.queryWill(level, pos, 0.5);
 *
 * if (will.hasDefault()) {
 *     double scaling = will.getDefault() / 100.0;
 *     // ... use scaling ...
 *     will.use(EnumWillType.DEFAULT, 0.1);
 * }
 *
 * if (will.hasSteadfast()) {
 *     // ... steadfast behavior ...
 *     will.use(EnumWillType.STEADFAST, 0.2);
 * }
 *
 * will.drain(handler, level, pos);
 * }</pre>
 */
public final class WillState {
    private final EnumMap<EnumWillType, Double> amounts = new EnumMap<>(EnumWillType.class);
    private final EnumMap<EnumWillType, Double> used = new EnumMap<>(EnumWillType.class);
    private final double threshold;

    /**
     * Creates a new WillState by querying all will types from the handler.
     * Typically called via {@link IDemonWillHandler#queryWill(Level, BlockPos, double)}.
     */
    public WillState(IDemonWillHandler handler, Level level, BlockPos pos, double threshold) {
        this.threshold = threshold;
        for (EnumWillType type : EnumWillType.values()) {
            amounts.put(type, handler.getCurrentWill(level, pos, type));
            used.put(type, 0.0);
        }
    }

    /** Gets the current amount of the specified will type. */
    public double get(EnumWillType type) { return amounts.getOrDefault(type, 0.0); }

    /** Returns true if the specified will type meets or exceeds the threshold. */
    public boolean has(EnumWillType type) { return get(type) >= threshold; }

    public double getDefault() { return get(EnumWillType.DEFAULT); }
    public double getCorrosive() { return get(EnumWillType.CORROSIVE); }
    public double getDestructive() { return get(EnumWillType.DESTRUCTIVE); }
    public double getSteadfast() { return get(EnumWillType.STEADFAST); }
    public double getVengeful() { return get(EnumWillType.VENGEFUL); }

    public boolean hasDefault() { return has(EnumWillType.DEFAULT); }
    public boolean hasCorrosive() { return has(EnumWillType.CORROSIVE); }
    public boolean hasDestructive() { return has(EnumWillType.DESTRUCTIVE); }
    public boolean hasSteadfast() { return has(EnumWillType.STEADFAST); }
    public boolean hasVengeful() { return has(EnumWillType.VENGEFUL); }

    /**
     * Records will usage of the specified type. Does not drain immediately —
     * call {@link #drain(IDemonWillHandler, Level, BlockPos)} at the end to apply.
     */
    public void use(EnumWillType type, double amount) {
        used.merge(type, amount, Double::sum);
    }

    /**
     * Drains all accumulated usage from the chunk in a single batch.
     * Call this once at the end of your ritual tick after all {@link #use} calls.
     */
    public void drain(IDemonWillHandler handler, Level level, BlockPos pos) {
        used.forEach((type, amount) -> {
            if (amount > 0) {
                handler.drainWill(level, pos, type, amount);
            }
        });
    }

    /**
     * Convenience overload that drains using the default {@link DemonWillHandler}.
     */
    public void drain(Level level, BlockPos pos) {
        drain(DemonWillHandler.INSTANCE, level, pos);
    }
}
