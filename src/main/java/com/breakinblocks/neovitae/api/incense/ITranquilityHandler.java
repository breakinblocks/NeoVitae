package com.breakinblocks.neovitae.api.incense;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;

import javax.annotation.Nullable;

/**
 * API interface for accessing block tranquility values.
 *
 * <p>Tranquility is a mechanic used by the Incense Altar to determine the
 * bonus multiplier for LP gains during self-sacrifice. Blocks around the altar
 * contribute different types and amounts of tranquility based on their nature.</p>
 *
 * <h2>Tranquility Types</h2>
 * <ul>
 *   <li>{@code PLANT} - Flowers, grass, ferns, vines</li>
 *   <li>{@code CROP} - Wheat, carrots, potatoes, etc.</li>
 *   <li>{@code TREE} - Logs, leaves, saplings</li>
 *   <li>{@code EARTHEN} - Dirt, sand, gravel, clay</li>
 *   <li>{@code WATER} - Water source blocks</li>
 *   <li>{@code FIRE} - Fire, campfires</li>
 *   <li>{@code LAVA} - Lava source blocks</li>
 * </ul>
 *
 * <h2>Datapack Customization</h2>
 * <p>Tranquility values can be customized via datapacks at:
 * {@code data/<namespace>/data_maps/block/tranquility.json}</p>
 *
 * <pre>{@code
 * {
 *   "values": {
 *     "#minecraft:logs": { "type": "tree", "value": 1.0 },
 *     "mymod:magic_flower": { "type": "plant", "value": 2.0 }
 *   }
 * }
 * }</pre>
 *
 * <h2>Multiple Tag Matching</h2>
 * <p>When a block matches multiple tags with different tranquility values,
 * the entry with the <b>highest value</b> is used.</p>
 *
 * @see com.breakinblocks.neovitae.api.NeoVitaeAPI#getTranquilityHandler()
 */
public interface ITranquilityHandler {

    /**
     * Gets the tranquility type for a block.
     *
     * @param block The block to check
     * @return The tranquility type, or null if the block has no tranquility
     */
    @Nullable
    EnumTranquilityType getTranquilityType(Block block);

    /**
     * Gets the tranquility type for a block state.
     *
     * @param state The block state to check
     * @return The tranquility type, or null if the block has no tranquility
     */
    @Nullable
    EnumTranquilityType getTranquilityType(BlockState state);

    /**
     * Gets the tranquility value for a block.
     *
     * @param block The block to check
     * @return The tranquility value (typically 0.5-2.0), or 0 if the block has no tranquility
     */
    double getTranquilityValue(Block block);

    /**
     * Gets the tranquility value for a block state.
     *
     * @param state The block state to check
     * @return The tranquility value (typically 0.5-2.0), or 0 if the block has no tranquility
     */
    double getTranquilityValue(BlockState state);

    /**
     * Checks if a block provides tranquility.
     *
     * @param block The block to check
     * @return True if the block has tranquility defined
     */
    boolean hasTranquility(Block block);

    /**
     * Checks if a block state provides tranquility.
     *
     * @param state The block state to check
     * @return True if the block has tranquility defined
     */
    boolean hasTranquility(BlockState state);
}
