package com.breakinblocks.neovitae.common.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;
import com.breakinblocks.neovitae.incense.TranquilityStack;

import javax.annotation.Nullable;

/**
 * Helper class for looking up block tranquility values from the datamap.
 *
 * <p>This provides a simple API for the Incense Altar system to determine
 * tranquility contributions from blocks in the area.</p>
 *
 * <h2>Priority System</h2>
 * <p>When a block matches multiple tags, the entry with the highest value wins.
 * This allows modpack developers to define broad tag-based defaults while
 * supporting specific block overrides.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Get tranquility for a block
 * TranquilityValue value = TranquilityHelper.getTranquilityValue(block);
 * if (value != null) {
 *     EnumTranquilityType type = value.type();
 *     double contribution = value.value();
 * }
 *
 * // Or get as a TranquilityStack for backwards compatibility
 * TranquilityStack stack = TranquilityHelper.getTranquilityStack(state);
 * }</pre>
 */
public final class TranquilityHelper {

    private TranquilityHelper() {
        // Utility class
    }

    /**
     * Gets the tranquility value for a block from the datamap.
     *
     * @param block The block to look up
     * @return The tranquility value, or null if the block has no tranquility
     */
    @Nullable
    public static TranquilityValue getTranquilityValue(Block block) {
        return BuiltInRegistries.BLOCK
                .wrapAsHolder(block)
                .getData(BMDataMaps.TRANQUILITY);
    }

    /**
     * Gets the tranquility value for a block state from the datamap.
     *
     * @param state The block state to look up
     * @return The tranquility value, or null if the block has no tranquility
     */
    @Nullable
    public static TranquilityValue getTranquilityValue(BlockState state) {
        return getTranquilityValue(state.getBlock());
    }

    /**
     * Gets the tranquility as a TranquilityStack for backwards compatibility.
     *
     * @param block The block to look up
     * @return A TranquilityStack, or null if the block has no tranquility
     */
    @Nullable
    public static TranquilityStack getTranquilityStack(Block block) {
        TranquilityValue value = getTranquilityValue(block);
        if (value == null) {
            return null;
        }
        return new TranquilityStack(value.type(), value.value());
    }

    /**
     * Gets the tranquility as a TranquilityStack for backwards compatibility.
     *
     * @param state The block state to look up
     * @return A TranquilityStack, or null if the block has no tranquility
     */
    @Nullable
    public static TranquilityStack getTranquilityStack(BlockState state) {
        return getTranquilityStack(state.getBlock());
    }

    /**
     * Checks if a block has tranquility defined in the datamap.
     *
     * @param block The block to check
     * @return True if the block has tranquility defined
     */
    public static boolean hasTranquility(Block block) {
        return getTranquilityValue(block) != null;
    }

    /**
     * Checks if a block has tranquility defined in the datamap.
     *
     * @param state The block state to check
     * @return True if the block has tranquility defined
     */
    public static boolean hasTranquility(BlockState state) {
        return hasTranquility(state.getBlock());
    }

    /**
     * Gets the tranquility type for a block.
     *
     * @param block The block to look up
     * @return The tranquility type, or null if the block has no tranquility
     */
    @Nullable
    public static EnumTranquilityType getTranquilityType(Block block) {
        TranquilityValue value = getTranquilityValue(block);
        return value != null ? value.type() : null;
    }

    /**
     * Gets the tranquility value amount for a block.
     *
     * @param block The block to look up
     * @return The tranquility value, or 0 if the block has no tranquility
     */
    public static double getTranquilityAmount(Block block) {
        TranquilityValue value = getTranquilityValue(block);
        return value != null ? value.value() : 0.0;
    }
}
