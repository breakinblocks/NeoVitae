package com.breakinblocks.neovitae.api;

import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.incense.ITranquilityHandler;
import com.breakinblocks.neovitae.api.living.ILivingArmorManager;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;
import com.breakinblocks.neovitae.api.will.IDemonWillHandler;

import java.util.UUID;

/**
 * Main interface for the Blood Magic API.
 *
 * <p>This interface provides access to Blood Magic's core systems for addon mods.
 * Access the implementation via {@link NeoVitaeAPI#getInstance()}.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.getInstance();
 *
 * // Get a player's soul network
 * ISoulNetwork network = api.getSoulNetwork(playerUUID);
 * if (network != null) {
 *     int lp = network.getCurrentEssence();
 * }
 *
 * // Register a custom altar rune type
 * api.getRuneRegistry().registerRuneType(myCustomRuneType);
 * api.getRuneRegistry().registerRuneBlock(myRuneBlock, myCustomRuneType, 1);
 *
 * // Interact with demon will in a chunk
 * IDemonWillHandler willHandler = api.getDemonWillHandler();
 * double rawWill = willHandler.getCurrentWill(level, pos, EnumWillType.DEFAULT);
 * }</pre>
 */
public interface INeoVitaeAPI {

    /**
     * Gets the Soul Network for a player by their UUID.
     *
     * @param uuid The player's UUID
     * @return The soul network, or null if none exists for this player
     */
    @Nullable
    ISoulNetwork getSoulNetwork(UUID uuid);

    /**
     * Gets the Living Armor upgrade manager.
     *
     * @return The living armor manager
     */
    ILivingArmorManager getLivingArmorManager();

    /**
     * Gets the Altar Rune registry for registering custom runes.
     *
     * <p>Use this to:</p>
     * <ul>
     *   <li>Register custom rune types that provide new altar bonuses</li>
     *   <li>Associate blocks with rune types so they're recognized in altar structures</li>
     * </ul>
     *
     * @return The altar rune registry
     */
    IAltarRuneRegistry getRuneRegistry();

    /**
     * Gets the Tranquility handler for looking up block tranquility values.
     *
     * <p>Use this to:</p>
     * <ul>
     *   <li>Check what type of tranquility a block provides (plant, tree, earthen, etc.)</li>
     *   <li>Get the tranquility value contribution for custom Incense Altar-like systems</li>
     * </ul>
     *
     * <p>Tranquility values are data-driven and can be customized via datapacks at:
     * {@code data/<namespace>/data_maps/block/tranquility.json}</p>
     *
     * @return The tranquility handler
     */
    ITranquilityHandler getTranquilityHandler();

    /**
     * Gets the Demon Will handler for interacting with chunk-based demon will aura.
     *
     * <p>Use this to:</p>
     * <ul>
     *   <li>Query current demon will amounts in chunks</li>
     *   <li>Add or drain demon will from chunks</li>
     *   <li>Check or modify maximum will capacity (including per-chunk bonuses)</li>
     *   <li>Transfer will between chunks</li>
     * </ul>
     *
     * <p>Maximum will capacity per chunk is configurable in the server config,
     * and can be increased per-chunk via rituals or other effects.</p>
     *
     * @return The demon will handler
     */
    IDemonWillHandler getDemonWillHandler();

    /**
     * Gets the current API version string.
     *
     * @return The API version (e.g., "1.0.0")
     */
    String getApiVersion();
}
