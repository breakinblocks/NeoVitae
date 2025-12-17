package com.breakinblocks.neovitae.api.will;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Handler interface for interacting with the demon will aura system.
 *
 * <p>Demon will is stored per-chunk and comes in five types:</p>
 * <ul>
 *   <li>{@code DEFAULT} - Raw demon will</li>
 *   <li>{@code CORROSIVE} - Corrosive demon will</li>
 *   <li>{@code DESTRUCTIVE} - Destructive demon will</li>
 *   <li>{@code VENGEFUL} - Vengeful demon will</li>
 *   <li>{@code STEADFAST} - Steadfast demon will</li>
 * </ul>
 *
 * <p>Each chunk has a maximum will capacity determined by:</p>
 * <ol>
 *   <li>Base maximum from server config (default 100 per type)</li>
 *   <li>Per-chunk bonuses from rituals or other effects</li>
 * </ol>
 *
 * <p>Access this handler via {@code NeoVitaeAPI.getInstance().getDemonWillHandler()}.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * IDemonWillHandler handler = NeoVitaeAPI.getInstance().getDemonWillHandler();
 *
 * // Get current will amount
 * double raw = handler.getCurrentWill(level, pos, EnumWillType.DEFAULT);
 *
 * // Add will to chunk
 * double added = handler.addWill(level, pos, EnumWillType.CORROSIVE, 50.0);
 *
 * // Check maximum capacity
 * double max = handler.getMaxWill(level, pos, EnumWillType.DEFAULT);
 *
 * // Add a bonus to max capacity (for rituals)
 * handler.addMaxBonus(level, pos, EnumWillType.DEFAULT, 50.0);
 * }</pre>
 */
public interface IDemonWillHandler {

    /**
     * Gets the current amount of will of a specific type in the chunk at the given position.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The current will amount
     */
    double getCurrentWill(Level level, BlockPos pos, EnumWillType type);

    /**
     * Gets the total will of all types in the chunk at the given position.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return The total will amount across all types
     */
    double getTotalWill(Level level, BlockPos pos);

    /**
     * Gets the maximum will capacity for a specific type in the chunk.
     * This includes both the base config value and any per-chunk bonuses.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The maximum will capacity
     */
    double getMaxWill(Level level, BlockPos pos, EnumWillType type);

    /**
     * Gets the base maximum will capacity from server config for a specific type.
     * This does not include per-chunk bonuses.
     *
     * @param type The will type
     * @return The base maximum will capacity from config
     */
    double getBaseMaxWill(EnumWillType type);

    /**
     * Gets the per-chunk bonus to maximum will capacity for a specific type.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The bonus capacity (0 if none)
     */
    double getMaxBonus(Level level, BlockPos pos, EnumWillType type);

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
    void setMaxBonus(Level level, BlockPos pos, EnumWillType type, double bonus);

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
    double addMaxBonus(Level level, BlockPos pos, EnumWillType type, double amount);

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
    double addWill(Level level, BlockPos pos, EnumWillType type, double amount);

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
    double drainWill(Level level, BlockPos pos, EnumWillType type, double amount);

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
    double fillWillToAmount(Level level, BlockPos pos, EnumWillType type, double targetAmount);

    /**
     * Gets the dominant will type in the chunk (highest amount).
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return The dominant will type
     */
    EnumWillType getDominantWillType(Level level, BlockPos pos);

    /**
     * Checks if the chunk has any will.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @return true if the chunk has any will of any type
     */
    boolean hasWill(Level level, BlockPos pos);

    /**
     * Gets the fill ratio (current/max) for a specific will type in the chunk.
     * Useful for display purposes.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return Ratio from 0.0 to 1.0
     */
    double getFillRatio(Level level, BlockPos pos, EnumWillType type);

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
    double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, EnumWillType type, double maxTransfer);
}
