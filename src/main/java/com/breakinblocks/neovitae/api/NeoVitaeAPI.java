package com.breakinblocks.neovitae.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Main entry point for the Blood Magic API.
 * The instance is populated by Blood Magic at the right time.
 * Do NOT instantiate this class yourself.
 */
public final class NeoVitaeAPI {
    private static INeoVitaeAPI INSTANCE;

    /**
     * @return The instance of the Blood Magic API.
     */
    public static INeoVitaeAPI getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Blood Magic API not available yet!");
        }
        return INSTANCE;
    }

    @ApiStatus.Internal
    public static void setInstance(INeoVitaeAPI instance) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Blood Magic API already initialized!");
        }
        INSTANCE = instance;
    }
}
