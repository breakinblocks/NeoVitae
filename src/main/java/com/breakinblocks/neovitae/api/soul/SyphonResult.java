package com.breakinblocks.neovitae.api.soul;

/**
 * Result of a syphon operation on a Soul Network.
 *
 * @param success Whether the syphon succeeded
 * @param amount The amount that was syphoned
 */
public record SyphonResult(boolean success, int amount) {

    /**
     * Creates a successful result with the given amount.
     *
     * @param success Whether the operation succeeded
     * @param amount The amount syphoned
     * @return A new result
     */
    public static SyphonResult of(boolean success, int amount) {
        return new SyphonResult(success, amount);
    }

    /**
     * Creates a failure result.
     *
     * @return A failure result with 0 amount
     */
    public static SyphonResult failure() {
        return new SyphonResult(false, 0);
    }
}
