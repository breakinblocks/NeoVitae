package com.breakinblocks.neovitae.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Main entry point for accessing the NeoVitae API.
 *
 * <p>Addon mods should call {@link #getInstance()} to obtain the {@link INeoVitaeAPI}
 * implementation, which provides access to all NeoVitae subsystems (soul networks,
 * altar runes, demon will, tranquility, etc.).</p>
 *
 * <p>The API instance is set by NeoVitae during mod loading. It is safe to call
 * {@link #getInstance()} from {@code FMLCommonSetupEvent} or later. Calling it
 * before NeoVitae has initialized will throw {@link IllegalStateException}.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.getInstance();
 * ISoulNetwork network = api.getSoulNetwork(playerUUID);
 * }</pre>
 */
public final class NeoVitaeAPI {
    private static INeoVitaeAPI INSTANCE;

    /**
     * Returns the NeoVitae API instance.
     *
     * @return The active {@link INeoVitaeAPI} implementation
     * @throws IllegalStateException if called before NeoVitae has finished initializing
     */
    public static INeoVitaeAPI getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NeoVitae API not available yet!");
        }
        return INSTANCE;
    }

    @ApiStatus.Internal
    public static void setInstance(INeoVitaeAPI instance) {
        if (INSTANCE != null) {
            throw new IllegalStateException("NeoVitae API already initialized!");
        }
        INSTANCE = instance;
    }
}
