package com.breakinblocks.neovitae.common.routing;

/**
 * AUTO_MATCH is fluid-only (mirrors whatever is in the neighbor tank); item sides
 * never produce it and clamp it to WHITELIST defensively.
 */
public enum FilterMode {
    WHITELIST,
    BLACKLIST,
    AUTO_MATCH;

    public FilterMode toggle() {
        return this == WHITELIST ? BLACKLIST : WHITELIST;
    }

    public FilterMode nextFluidMode() {
        return switch (this) {
            case WHITELIST -> BLACKLIST;
            case BLACKLIST -> AUTO_MATCH;
            case AUTO_MATCH -> WHITELIST;
        };
    }
}
