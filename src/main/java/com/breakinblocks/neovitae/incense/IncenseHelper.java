package com.breakinblocks.neovitae.incense;

import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.common.dataattachment.BMDataAttachments;

/**
 * Helper class for managing player incense levels.
 * Incense accumulates when a player is near an active Incense Altar
 * and is consumed when using a Sacrificial Knife for self-sacrifice.
 *
 * Uses NeoForge data attachments for persistence.
 */
public class IncenseHelper {

    /**
     * Gets the current incense level for a player.
     */
    public static double getCurrentIncense(Player player) {
        return player.getData(BMDataAttachments.INCENSE);
    }

    /**
     * Sets the current incense level for a player.
     */
    public static void setCurrentIncense(Player player, double amount) {
        player.setData(BMDataAttachments.INCENSE, amount);
    }

    /**
     * Attempts to increment the player's incense level.
     *
     * @param player          The player to increment incense for
     * @param min             Minimum incense level required
     * @param incenseAddition Maximum incense level that can be reached
     * @param increment       Amount to increment by
     * @return true if incense was incremented
     */
    public static boolean incrementIncense(Player player, double min, double incenseAddition, double increment) {
        double amount = getCurrentIncense(player);
        if (amount < min || amount >= incenseAddition) {
            return false;
        }

        amount = amount + Math.min(increment, incenseAddition - amount);
        setCurrentIncense(player, amount);

        return true;
    }

    /**
     * Clears the player's incense level (called after self-sacrifice).
     */
    public static void clearIncense(Player player) {
        setCurrentIncense(player, 0);
    }

    /**
     * Gets the self-sacrifice modifier based on incense level.
     * Returns (1 + incenseBonus), where incenseBonus is the incense level.
     */
    public static double getSelfSacrificeModifier(Player player) {
        return 1.0 + getCurrentIncense(player);
    }
}
