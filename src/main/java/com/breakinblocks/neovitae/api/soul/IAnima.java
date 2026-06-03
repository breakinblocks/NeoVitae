package com.breakinblocks.neovitae.api.soul;

import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.api.INeoVitaeAPI;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;

import java.util.UUID;

/**
 * Represents a player's Anima - the storage for Essentia Vitae (EV).
 *
 * <p>Each player has their own Anima that stores EV, which is generated
 * through self-sacrifice or the sacrifice of other entities. EV is consumed
 * by sigils, rituals, and other NeoVitae items.</p>
 *
 * <p>Access a player's anima via {@link NeoVitaeAPI#getInstance()}
 * and then {@link INeoVitaeAPI#getAnima(UUID)}.</p>
 */
public interface IAnima {

    /**
     * Gets the UUID of the player who owns this anima.
     *
     * @return The owner's UUID
     */
    UUID getPlayerId();

    /**
     * Gets the current EV stored in this anima.
     *
     * @return The current EV amount
     */
    int getCurrentEV();

    /**
     * Adds EV to this anima from the given ticket.
     *
     * @param ticket The ticket describing the source
     * @param maximum The maximum EV the anima can hold
     * @return The actual amount added
     */
    int add(AnimaTicket ticket, int maximum);

    /**
     * Sets the EV in this anima.
     *
     * @param ticket The ticket describing the amount to set
     * @param maximum The maximum EV the anima can hold
     * @return The actual amount set
     */
    int set(AnimaTicket ticket, int maximum);

    /**
     * Drains EV from this anima.
     *
     * @param ticket The ticket describing the amount to drain
     * @return The actual amount drained
     */
    int syphon(AnimaTicket ticket);

    /**
     * Damages the player based on EV syphoned that couldn't be provided.
     *
     * @param user The player to damage
     * @param syphon The amount that couldn't be syphoned
     */
    void hurtPlayer(Player user, float syphon);

    /**
     * Syphons EV and damages the player if not enough is available.
     *
     * @param user The player using the anima
     * @param ticket The ticket describing the amount to syphon
     * @return The result of the syphon operation
     */
    SyphonResult syphonAndDamage(Player user, AnimaTicket ticket);
}
