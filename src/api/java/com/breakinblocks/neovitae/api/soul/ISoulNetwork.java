package com.breakinblocks.neovitae.api.soul;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Interface representing a player's Soul Network.
 * The Soul Network stores Life Points (LP) that power Neo Vitae items and rituals.
 *
 * <p>Obtain an instance via {@link com.breakinblocks.neovitae.api.NeoVitaeAPI#getSoulNetwork(UUID)}</p>
 */
public interface ISoulNetwork {

    /**
     * Gets the UUID of the player who owns this network.
     *
     * @return The owner's UUID
     */
    UUID getPlayerId();

    /**
     * Gets the current amount of Life Points stored in this network.
     *
     * @return Current LP amount
     */
    int getCurrentEssence();

    /**
     * Adds LP to this network up to the specified maximum.
     *
     * @param ticket  The soul ticket describing this transaction
     * @param maximum The maximum LP this network can hold
     * @return The amount of LP actually added
     */
    int add(SoulTicket ticket, int maximum);

    /**
     * Sets the LP in this network to a specific value.
     *
     * @param ticket  The soul ticket containing the amount to set
     * @param maximum The maximum LP this network can hold
     * @return The new LP value
     */
    int set(SoulTicket ticket, int maximum);

    /**
     * Removes LP from this network.
     *
     * @param ticket The soul ticket containing the amount to syphon
     * @return The amount of LP actually removed
     */
    int syphon(SoulTicket ticket);

    /**
     * Damages the player based on LP debt.
     * Called when an item needs more LP than available.
     *
     * @param user   The player to damage
     * @param amount The amount of LP debt to convert to damage
     */
    void hurtPlayer(Player user, float amount);

    /**
     * Attempts to syphon LP from the network, damaging the player if insufficient.
     * This is the recommended method for items that consume LP.
     *
     * @param user   The player using the item
     * @param ticket The soul ticket containing the amount to syphon
     * @return Result containing success state and amount processed
     */
    SyphonResult syphonAndDamage(Player user, SoulTicket ticket);

    /**
     * Result of a syphon operation.
     *
     * @param success Whether the operation completed (even if player was damaged)
     * @param amount  The amount of LP processed
     */
    record SyphonResult(boolean success, int amount) {
        public static SyphonResult failure() {
            return new SyphonResult(false, 0);
        }

        public static SyphonResult of(boolean success, int amount) {
            return new SyphonResult(success, amount);
        }
    }
}
