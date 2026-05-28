package com.breakinblocks.neovitae.api.spiritus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Handler interface for interacting with the spiritus aura system.
 *
 * <p>Spiritus is stored per-chunk and comes in five types:</p>
 * <ul>
 *   <li>{@code DEFAULT} - Raw spiritus</li>
 *   <li>{@code CORROSIVE} - Corrosive spiritus</li>
 *   <li>{@code DESTRUCTIVE} - Destructive spiritus</li>
 *   <li>{@code VENGEFUL} - Vengeful spiritus</li>
 *   <li>{@code STEADFAST} - Steadfast spiritus</li>
 * </ul>
 *
 * <p>Each chunk has a maximum will capacity determined by:</p>
 * <ol>
 *   <li>Base maximum from server config (default 100 per type)</li>
 *   <li>Per-chunk bonuses from rituals or other effects</li>
 * </ol>
 *
 * <p>Access this handler via {@code NeoVitaeAPI.getInstance().getSpiritusHandler()}.</p>
 *
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * ISpiritusHandler handler = NeoVitaeAPI.getInstance().getSpiritusHandler();
 *
 * // For rituals: query all will types at once with a threshold
 * SpiritusState will = handler.querySpiritus(level, pos, 0.5);
 * if (will.hasDefault()) {
 *     double scaling = will.getDefault() / 100.0;
 *     will.use(SpiritusType.RAW, 0.1);
 * }
 * will.drain(handler, level, pos);
 *
 * // For simple operations: individual will access
 * double raw = handler.getCurrentSpiritus(level, pos, SpiritusType.RAW);
 * double added = handler.addSpiritus(level, pos, SpiritusType.RUINA, 50.0);
 * }</pre>
 */
public interface ISpiritusHandler {

    /**
     * Gets the current amount of spiritus of a specific type in the chunk at the given position.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The current will amount
     */
    double getCurrentSpiritus(Level level, BlockPos pos, SpiritusType type);

    /**
     * Gets the total will of all types in the chunk at the given position.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return The total will amount across all types
     */
    double getTotalSpiritus(Level level, BlockPos pos);

    /**
     * Gets the maximum will capacity for a specific type in the chunk.
     * This includes both the base config value and any per-chunk bonuses.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The maximum will capacity
     */
    double getMaxSpiritus(Level level, BlockPos pos, SpiritusType type);

    /**
     * Gets the base maximum will capacity from server config for a specific type.
     * This does not include per-chunk bonuses.
     *
     * @param type The will type
     * @return The base maximum will capacity from config
     */
    double getBaseMaxSpiritus(SpiritusType type);

    /**
     * Gets the per-chunk bonus to maximum will capacity for a specific type.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The bonus capacity (0 if none)
     */
    double getMaxBonus(Level level, BlockPos pos, SpiritusType type);

    /**
     * Sets the per-chunk bonus to maximum will capacity for a specific type.
     * This is used by rituals to expand chunk capacity.
     *
     * <p>Server-side only. Does nothing on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param bonus  The new bonus value (must be >= 0)
     */
    void setMaxBonus(Level level, BlockPos pos, SpiritusType type, double bonus);

    /**
     * Adds to the per-chunk bonus to maximum will capacity for a specific type.
     * This is used by rituals to expand chunk capacity.
     *
     * <p>Server-side only. Does nothing on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param amount The amount to add (can be negative to reduce)
     * @return The new bonus value
     */
    double addMaxBonus(Level level, BlockPos pos, SpiritusType type, double amount);

    /**
     * Adds will to the chunk at the given position.
     *
     * <p>Server-side only. Returns 0 on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param amount The amount to add
     * @return The amount actually added (may be less if at cap)
     */
    double addSpiritus(Level level, BlockPos pos, SpiritusType type, double amount);

    /**
     * Drains will from the chunk at the given position.
     *
     * <p>Server-side only. Returns 0 on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param amount The amount to drain
     * @return The amount actually drained (may be less if not enough)
     */
    double drainSpiritus(Level level, BlockPos pos, SpiritusType type, double amount);

    /**
     * Fills will in the chunk up to the specified amount.
     *
     * <p>Server-side only. Returns 0 on client.</p>
     *
     * @param level        The level
     * @param pos          The position (chunk is determined from this)
     * @param type         The will type
     * @param targetAmount The target amount to fill to
     * @return The amount actually added
     */
    double fillSpiritusToAmount(Level level, BlockPos pos, SpiritusType type, double targetAmount);

    /**
     * Gets the dominant will type in the chunk (highest amount).
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return The dominant will type
     */
    SpiritusType getDominantSpiritusType(Level level, BlockPos pos);

    /**
     * Checks if the chunk has any will.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return true if the chunk has any will of any type
     */
    boolean hasSpiritus(Level level, BlockPos pos);

    /**
     * Gets the fill ratio (current/max) for a specific will type in the chunk.
     * Useful for display purposes.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return Ratio from 0.0 to 1.0
     */
    double getFillRatio(Level level, BlockPos pos, SpiritusType type);

    /**
     * Queries all will types for a chunk and returns a {@link SpiritusState} snapshot
     * with threshold-based active flags and usage tracking.
     *
     * <p>This is the recommended way for rituals to interact with spiritus.</p>
     *
     * @param level     The level
     * @param pos       The position (chunk is determined from this)
     * @param threshold Minimum will required for each type to be considered "active"
     * @return A snapshot of current will amounts with usage tracking
     */
    default SpiritusState querySpiritus(Level level, BlockPos pos, double threshold) {
        return new SpiritusState(this, level, pos, threshold);
    }

    /**
     * Transfers will from one chunk to an adjacent chunk.
     * Used by demon pylons.
     *
     * <p>Server-side only. Returns 0 on client.</p>
     *
     * @param level       The level
     * @param fromChunk   The source chunk position
     * @param toChunk     The destination chunk position
     * @param type        The will type
     * @param maxTransfer The maximum amount to transfer
     * @return The amount actually transferred
     */
    double transferSpiritus(Level level, ChunkPos fromChunk, ChunkPos toChunk, SpiritusType type, double maxTransfer);
}
