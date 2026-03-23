package com.breakinblocks.neovitae.api.routing;

/**
 * Base marker interface for all routing filters.
 * <p>
 * Routing filters handle the actual transfer logic between inventories, tanks,
 * or energy storage. Each {@link RoutingChannel} is parameterized on a specific
 * filter type extending this interface.
 *
 * @see IItemFilter
 * @see IFluidFilter
 * @see IEnergyFilter
 */
public interface IRoutingFilter {
}
