package com.breakinblocks.neovitae.common.item.routing;

/**
 * Interface for filter providers that can be nested inside composite filters.
 * Extends IItemFilterProvider so nested filters can contribute their filter keys.
 */
public interface INestableItemFilterProvider extends IItemFilterProvider {
}
