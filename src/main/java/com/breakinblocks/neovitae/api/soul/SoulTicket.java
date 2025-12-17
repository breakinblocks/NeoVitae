package com.breakinblocks.neovitae.api.soul;

/**
 * Represents a request to add or remove LP from a Soul Network.
 *
 * <p>Soul tickets track the amount of LP being transferred and can be
 * used for logging or auditing purposes in the future.</p>
 */
public class SoulTicket {

    private final int amount;

    private SoulTicket(int amount) {
        this.amount = amount;
    }

    /**
     * Creates a new soul ticket with the specified amount.
     *
     * @param amount The amount of LP
     * @return A new soul ticket
     */
    public static SoulTicket create(int amount) {
        return new SoulTicket(amount);
    }

    /**
     * Gets the amount of LP in this ticket.
     *
     * @return The LP amount
     */
    public int getAmount() {
        return amount;
    }
}
