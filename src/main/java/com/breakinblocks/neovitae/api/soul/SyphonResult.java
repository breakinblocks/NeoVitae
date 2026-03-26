package com.breakinblocks.neovitae.api.soul;

/**
 * Result of a syphon operation on an Anima.
 *
 * <p>A successful result indicates the full requested amount was available in the
 * anima. A failed result means the anima did not have enough EV; the player
 * may have been damaged to compensate (see {@link IAnima#syphonAndDamage}).</p>
 *
 * @param success {@code true} if the network had enough LP to cover the full request
 * @param amount  the amount of LP actually syphoned from the network
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
