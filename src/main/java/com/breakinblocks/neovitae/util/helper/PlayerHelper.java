package com.breakinblocks.neovitae.util.helper;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerHelper {
    /**
     * A list of all known fake players that do not extend FakePlayer.
     * Will be added to as needed.
     * Thread-safe: Uses CopyOnWriteArrayList for concurrent read access.
     */
    private static final List<String> knownFakePlayers = new CopyOnWriteArrayList<>();

    public static Player getPlayerFromId(UUID uuid) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return null;
        }
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    public static Player getPlayerFromUUID(UUID uuid) {
        return getPlayerFromId(uuid);
    }

    public static UUID getUUIDFromPlayer(Player player) {
        return player.getGameProfile().getId();
    }

    /**
     * Checks whether or not the given player is an "actual" player
     *
     * @param player - The player in question
     * @return If the player is fake or not
     */
    public static boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer ||
               (player != null && knownFakePlayers.contains(player.getClass().getCanonicalName()));
    }
}
