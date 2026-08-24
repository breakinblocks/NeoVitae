package com.breakinblocks.neovitae.client;

import java.util.UUID;

public final class ClientAnimaCache {

    private static UUID owner;
    private static int currentEV;

    private ClientAnimaCache() {
    }

    public static void update(UUID owner, int currentEV) {
        ClientAnimaCache.owner = owner;
        ClientAnimaCache.currentEV = currentEV;
    }

    public static void clear() {
        owner = null;
        currentEV = 0;
    }

    public static boolean has(UUID candidate) {
        return owner != null && owner.equals(candidate);
    }

    public static int getCurrentEV() {
        return currentEV;
    }
}
