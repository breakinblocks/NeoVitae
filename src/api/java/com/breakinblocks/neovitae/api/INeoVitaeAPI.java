package com.breakinblocks.neovitae.api;

import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.living.ILivingArmorManager;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * The main entry point for the Neo Vitae API.
 *
 * <p>Access via {@link NeoVitaeAPI#get()} after Neo Vitae has initialized.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.get();
 * ISoulNetwork network = api.getSoulNetwork(player);
 * int currentLP = network.getCurrentEssence();
 * }</pre>
 */
public interface INeoVitaeAPI {

    // Soul Network

    /**
     * Gets the soul network for a player by UUID.
     *
     * @param uuid The player's UUID
     * @return The soul network, or null if the server is not available
     */
    @Nullable
    ISoulNetwork getSoulNetwork(UUID uuid);

    /**
     * Gets the soul network for a player.
     *
     * @param player The player
     * @return The soul network, or null if the server is not available
     */
    @Nullable
    default ISoulNetwork getSoulNetwork(Player player) {
        return getSoulNetwork(player.getUUID());
    }

    /**
     * Gets the soul network for a player by UUID string.
     *
     * @param uuid The player's UUID as a string
     * @return The soul network, or null if the server is not available or UUID is invalid
     */
    @Nullable
    default ISoulNetwork getSoulNetwork(String uuid) {
        try {
            return getSoulNetwork(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Living Armor

    /**
     * Gets the Living Armor manager for querying and modifying upgrades.
     *
     * @return The living armor manager
     */
    ILivingArmorManager getLivingArmorManager();

    // Altar Runes

    /**
     * Gets the Altar Rune registry for registering custom rune types.
     *
     * <p>Use this to register custom rune types that can affect Blood Altar behavior.
     * Custom runes should be registered during mod initialization (e.g., in a
     * {@code FMLCommonSetupEvent} handler).</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * IAltarRuneRegistry registry = NeoVitaeAPI.get().getRuneRegistry();
     * registry.registerRuneType(new MyCustomRuneType());
     * }</pre>
     *
     * @return The altar rune registry
     * @see IAltarRuneRegistry
     * @see com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType
     * @see com.breakinblocks.neovitae.api.event.AltarRuneEvent
     */
    IAltarRuneRegistry getRuneRegistry();

    // Version Info

    /**
     * Gets the version of the Neo Vitae API.
     *
     * @return API version string
     */
    String getApiVersion();
}
