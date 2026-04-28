package com.breakinblocks.neovitae.api.routing;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Position of the routing node this filter is attached to, or {@code null}
     * if the filter is unbound. Used by the master node to drive per-transfer
     * visual effects.
     */
    @Nullable
    default BlockPos getNodePos() {
        return null;
    }
}
