package com.breakinblocks.neovitae.api;

import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.incense.ITranquilityHandler;
import com.breakinblocks.neovitae.api.sentient.ISentientArmorManager;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.will.ISpiritusHandler;
import com.breakinblocks.neovitae.api.will.IPlayerSpiritusHandler;

import java.util.UUID;

/**
 * Main interface for the NeoVitae API.
 *
 * <p>This interface provides access to NeoVitae's core systems for addon mods.
 * Access the implementation via {@link NeoVitaeAPI#getInstance()}.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.getInstance();
 *
 * // Get a player's anima
 * IAnima anima = api.getAnima(playerUUID);
 * if (anima != null) {
 *     int ev = anima.getCurrentEV();
 * }
 *
 * // Register a custom altar rune type
 * api.getRuneRegistry().registerRuneType(myCustomRuneType);
 * api.getRuneRegistry().registerRuneBlock(myRuneBlock, myCustomRuneType, 1);
 *
 * // Interact with spiritus in a chunk
 * ISpiritusHandler willHandler = api.getSpiritusHandler();
 * double rawSpiritus = willHandler.getCurrentWill(level, pos, SpiritusType.RAW);
 * }</pre>
 */
public interface INeoVitaeAPI {

    /**
     * Gets the Anima for a player by their UUID.
     *
     * @param uuid The player's UUID
     * @return The anima, or null if none exists for this player
     */
    @Nullable
    IAnima getAnima(UUID uuid);

    /**
     * Gets the Sentient Armor upgrade manager.
     *
     * @return The living armor manager
     */
    ISentientArmorManager getLivingArmorManager();

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
     * Gets the Spiritus handler for interacting with chunk-based spiritus aura.
     *
     * <p>Use this to:</p>
     * <ul>
     *   <li>Query current spiritus amounts in chunks</li>
     *   <li>Add or drain spiritus from chunks</li>
     *   <li>Check or modify maximum will capacity (including per-chunk bonuses)</li>
     *   <li>Transfer will between chunks</li>
     * </ul>
     *
     * <p>Maximum will capacity per chunk is configurable in the server config,
     * and can be increased per-chunk via rituals or other effects.</p>
     *
     * @return The spiritus handler
     */
    ISpiritusHandler getSpiritusHandler();

    /**
     * Gets the handler for managing spiritus items in player inventories.
     *
     * @return The player spiritus handler
     */
    IPlayerSpiritusHandler getPlayerWillHandler();

    /**
     * Gets the current API version string.
     *
     * @return The API version (e.g., "1.0.0")
     */
    String getApiVersion();
}
