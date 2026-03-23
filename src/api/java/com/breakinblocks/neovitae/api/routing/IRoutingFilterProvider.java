package com.breakinblocks.neovitae.api.routing;

/**
 * Marker interface for items that provide routing filters.
 * <p>
 * Items implementing this interface can be placed in routing node filter slots
 * to enable transfer on that side. The presence of an {@code IRoutingFilterProvider}
 * item in a filter slot activates item, fluid, and energy routing for that direction.
 * <p>
 * Implement {@link IItemFilterProvider} for item-specific filtering with whitelist/blacklist support.
 */
public interface IRoutingFilterProvider {
}
