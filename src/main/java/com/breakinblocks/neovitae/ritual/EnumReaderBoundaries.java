package com.breakinblocks.neovitae.ritual;

/**
 * Result codes for ritual area modification attempts.
 */
public enum EnumReaderBoundaries {
    /**
     * Area modification was successful
     */
    SUCCESS,
    /**
     * Area exceeds the maximum volume
     */
    VOLUME_TOO_LARGE,
    /**
     * Area extends beyond the allowed boundaries
     */
    NOT_WITHIN_BOUNDARIES
}
