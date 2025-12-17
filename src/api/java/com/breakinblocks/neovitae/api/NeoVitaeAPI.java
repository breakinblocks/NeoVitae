package com.breakinblocks.neovitae.api;

import javax.annotation.Nullable;

/**
 * Static accessor for the Neo Vitae API.
 *
 * <p>The API instance is set by Neo Vitae during mod initialization.
 * Always check for null before using, or ensure Neo Vitae is loaded.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.get();
 * if (api != null) {
 *     ISoulNetwork network = api.getSoulNetwork(player);
 *     // Use the network...
 * }
 * }</pre>
 */
public final class NeoVitaeAPI {
    private static INeoVitaeAPI instance;

    private NeoVitaeAPI() {
        // No instantiation
    }

    /**
     * Gets the Neo Vitae API instance.
     *
     * @return The API instance, or null if Neo Vitae is not loaded
     */
    @Nullable
    public static INeoVitaeAPI get() {
        return instance;
    }

    /**
     * Sets the API instance. Called internally by Neo Vitae during initialization.
     * <b>Do not call this from addon mods.</b>
     *
     * @param api The API implementation
     */
    public static void setInstance(INeoVitaeAPI api) {
        if (instance != null) {
            throw new IllegalStateException("Neo Vitae API instance already set!");
        }
        instance = api;
    }

    /**
     * Checks if the Neo Vitae API is available.
     *
     * @return True if the API has been initialized
     */
    public static boolean isAvailable() {
        return instance != null;
    }
}
