package com.breakinblocks.neovitae.api.altar.rune;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a single rune instance found during altar structure scanning.
 *
 * <p>This record provides addon mods with direct access to the scanned rune data,
 * eliminating the need to re-scan the altar structure. This is especially useful
 * for dynamic runes whose bonuses depend on their internal state (e.g., a rune
 * that provides different bonuses based on stored power).</p>
 *
 * <h2>Usage in Event Handlers</h2>
 * <pre>{@code
 * @SubscribeEvent
 * public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
 *     // Find all instances of our custom rune
 *     for (RuneInstance instance : event.getRuneInstances()) {
 *         if (instance.blockEntity() instanceof MyCustomRuneBlockEntity myRune) {
 *             // Apply dynamic bonus based on rune state
 *             if (myRune.isPowered()) {
 *                 event.getModifiers().addConsumptionMod(0.15f);
 *             }
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h2>Helper Methods</h2>
 * <p>The record provides convenience methods for filtering and type checking:</p>
 * <ul>
 *   <li>{@link #hasBlockEntity()} - Check if this rune has a block entity</li>
 *   <li>{@link #isBlockEntityType(Class)} - Check if the block entity is a specific type</li>
 *   <li>{@link #getBlockEntityAs(Class)} - Cast the block entity to a specific type</li>
 * </ul>
 *
 * @param pos The position of the rune block in the world
 * @param block The block at this position
 * @param blockEntity The block entity at this position, or null if none exists
 */
public record RuneInstance(
        BlockPos pos,
        Block block,
        @Nullable BlockEntity blockEntity
) {
    /**
     * Checks if this rune instance has an associated block entity.
     *
     * @return True if a block entity exists at this position
     */
    public boolean hasBlockEntity() {
        return blockEntity != null;
    }

    /**
     * Checks if this rune's block entity is of the specified type.
     *
     * @param type The block entity class to check
     * @return True if the block entity is an instance of the given type
     */
    public boolean isBlockEntityType(Class<? extends BlockEntity> type) {
        return blockEntity != null && type.isInstance(blockEntity);
    }

    /**
     * Gets the block entity cast to the specified type.
     *
     * @param type The expected block entity class
     * @param <T> The block entity type
     * @return The block entity cast to the specified type, or null if it's not that type
     */
    @Nullable
    public <T extends BlockEntity> T getBlockEntityAs(Class<T> type) {
        if (blockEntity != null && type.isInstance(blockEntity)) {
            return type.cast(blockEntity);
        }
        return null;
    }

    /**
     * Checks if this rune is of the specified block type.
     *
     * @param blockClass The block class to check
     * @return True if the block is an instance of the given type
     */
    public boolean isBlockType(Class<? extends Block> blockClass) {
        return blockClass.isInstance(block);
    }
}
