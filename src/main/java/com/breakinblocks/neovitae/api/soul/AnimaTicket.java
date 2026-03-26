package com.breakinblocks.neovitae.api.soul;

/**
 * Represents a request to add or remove EV from an Anima.
 *
 * <p>Anima tickets track the amount of EV being transferred and can be
 * used for logging or auditing purposes in the future.</p>
 */
public class AnimaTicket {

    private final int amount;

    private AnimaTicket(int amount) {
        this.amount = amount;
    }

    /**
     * Creates a new anima ticket with the specified amount.
     *
     * @param amount The amount of EV
     * @return A new anima ticket
     */
    public static AnimaTicket create(int amount) {
        return new AnimaTicket(amount);
    }

    /**
     * Gets the amount of EV in this ticket.
     *
     * @return The EV amount
     */
    public int getAmount() {
        return amount;
    }
}
