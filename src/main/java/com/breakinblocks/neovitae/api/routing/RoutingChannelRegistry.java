package com.breakinblocks.neovitae.api.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry for {@link RoutingChannel} implementations.
 * <p>
 * Register new channels during mod construction to add support for additional
 * capability types (e.g., Botania mana, custom energy systems).
 * <p>
 * Example:
 * <pre>{@code
 * RoutingChannelRegistry.register(new ManaRoutingChannel());
 * }</pre>
 */
public final class RoutingChannelRegistry {
    private static final List<RoutingChannel<?>> channels = new ArrayList<>();

    private RoutingChannelRegistry() {}

    public static void register(RoutingChannel<?> channel) {
        channels.add(channel);
    }

    public static List<RoutingChannel<?>> getChannels() {
        return Collections.unmodifiableList(channels);
    }
}
