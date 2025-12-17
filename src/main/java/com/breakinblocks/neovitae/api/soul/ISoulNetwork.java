package com.breakinblocks.neovitae.api.soul;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Represents a player's Soul Network - the storage for Life Points (LP).
 *
 * <p>Each player has their own Soul Network that stores LP, which is generated
 * through self-sacrifice or the sacrifice of other entities. LP is consumed
 * by sigils, rituals, and other Blood Magic items.</p>
 *
 * <p>Access a player's network via {@link com.breakinblocks.neovitae.api.NeoVitaeAPI#getInstance()}
 * and then {@link com.breakinblocks.neovitae.api.INeoVitaeAPI#getSoulNetwork(UUID)}.</p>
 */
public interface ISoulNetwork {

    /**
     * Gets the UUID of the player who owns this network.
     *
     * @return The owner's UUID
     */
    UUID getPlayerId();

    /**
     * Gets the current LP stored in this network.
     *
     * @return The current LP amount
     */
    int getCurrentEssence();

    /**
     * Adds LP to this network from the given ticket.
     *
     * @param ticket The ticket describing the source
     * @param maximum The maximum LP the network can hold
     * @return The actual amount added
     */
    int add(SoulTicket ticket, int maximum);

    /**
     * Sets the LP in this network.
     *
     * @param ticket The ticket describing the amount to set
     * @param maximum The maximum LP the network can hold
     * @return The actual amount set
     */
    int set(SoulTicket ticket, int maximum);

    /**
     * Drains LP from this network.
     *
     * @param ticket The ticket describing the amount to drain
     * @return The actual amount drained
     */
    int syphon(SoulTicket ticket);

    /**
     * Damages the player based on LP syphoned that couldn't be provided.
     *
     * @param user The player to damage
     * @param syphon The amount that couldn't be syphoned
     */
    void hurtPlayer(Player user, float syphon);

    /**
     * Syphons LP and damages the player if not enough is available.
     *
     * @param user The player using the network
     * @param ticket The ticket describing the amount to syphon
     * @return The result of the syphon operation
     */
    SyphonResult syphonAndDamage(Player user, SoulTicket ticket);
}
