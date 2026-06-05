package com.breakinblocks.neovitae.incense;

import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;

/**
 * Helper class for managing player incense levels.
 * Incense accumulates when a player is near an active Incense Altar
 * and is consumed when bleeding into a Blood Orb for self-sacrifice.
 *
 * Uses NeoForge data attachments for persistence.
 */
public class IncenseHelper {

    public static double getCurrentIncense(Player player) {
        return player.getData(NVDataAttachments.INCENSE);
    }

    public static void setCurrentIncense(Player player, double amount) {
        player.setData(NVDataAttachments.INCENSE, amount);
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

    public static void clearIncense(Player player) {
        setCurrentIncense(player, 0);
    }

    public static double getSelfSacrificeModifier(Player player) {
        return 1.0 + getCurrentIncense(player);
    }
}
