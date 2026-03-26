package com.breakinblocks.neovitae.api.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;
import com.breakinblocks.neovitae.api.soul.SoulTicket;

import java.util.UUID;

/**
 * Events fired when LP is added or syphoned from a Soul Network.
 * Listen to these events to track or modify LP transactions.
 */
public abstract class SoulNetworkEvent extends Event {
    private final ISoulNetwork network;
    private final SoulTicket ticket;

    protected SoulNetworkEvent(ISoulNetwork network, SoulTicket ticket) {
        this.network = network;
        this.ticket = ticket;
    }

    /**
     * Gets the soul network involved in this event.
     */
    public ISoulNetwork getNetwork() {
        return network;
    }

    /**
     * Gets the UUID of the network owner.
     */
    public UUID getOwnerId() {
        return network.getPlayerId();
    }

    /**
     * Gets the soul ticket for this transaction.
     */
    public SoulTicket getTicket() {
        return ticket;
    }

    /**
     * Gets the LP amount being transacted.
     */
    public int getAmount() {
        return ticket.getAmount();
    }

    // ==================== Syphon Events ====================

    /**
     * Base class for syphon (LP removal) events.
     */
    public abstract static class Syphon extends SoulNetworkEvent {
        protected Syphon(ISoulNetwork network, SoulTicket ticket) {
            super(network, ticket);
        }
    }

    /**
     * Fired before LP is syphoned from a network.
     * Cancel to prevent the syphon entirely.
     */
    public static class PreSyphon extends Syphon implements ICancellableEvent {
        private int modifiedAmount;

        public PreSyphon(ISoulNetwork network, SoulTicket ticket) {
            super(network, ticket);
            this.modifiedAmount = ticket.getAmount();
        }

        /**
         * Gets the modified syphon amount.
         */
        public int getModifiedAmount() {
            return modifiedAmount;
        }

        /**
         * Sets the amount to syphon. Cannot exceed original amount.
         */
        public void setModifiedAmount(int amount) {
            this.modifiedAmount = Math.max(0, amount);
        }
    }

    /**
     * Fired after LP has been syphoned from a network.
     * Not cancellable - use for notification/tracking only.
     */
    public static class PostSyphon extends Syphon {
        private final int actualAmount;

        public PostSyphon(ISoulNetwork network, SoulTicket ticket, int actualAmount) {
            super(network, ticket);
            this.actualAmount = actualAmount;
        }

        /**
         * Gets the actual amount of LP that was syphoned.
         * May be less than requested if the network didn't have enough.
         */
        public int getActualAmount() {
            return actualAmount;
        }
    }

    // ==================== Add Events ====================

    /**
     * Base class for add (LP gain) events.
     */
    public abstract static class Add extends SoulNetworkEvent {
        private final int maximum;

        protected Add(ISoulNetwork network, SoulTicket ticket, int maximum) {
            super(network, ticket);
            this.maximum = maximum;
        }

        /**
         * Gets the maximum LP the network can hold.
         */
        public int getMaximum() {
            return maximum;
        }
    }

    /**
     * Fired before LP is added to a network.
     * Cancel to prevent the addition entirely.
     */
    public static class PreAdd extends Add implements ICancellableEvent {
        private int modifiedAmount;

        public PreAdd(ISoulNetwork network, SoulTicket ticket, int maximum) {
            super(network, ticket, maximum);
            this.modifiedAmount = ticket.getAmount();
        }

        /**
         * Gets the modified add amount.
         */
        public int getModifiedAmount() {
            return modifiedAmount;
        }

        /**
         * Sets the amount to add.
         */
        public void setModifiedAmount(int amount) {
            this.modifiedAmount = Math.max(0, amount);
        }
    }

    /**
     * Fired after LP has been added to a network.
     * Not cancellable - use for notification/tracking only.
     */
    public static class PostAdd extends Add {
        private final int actualAmount;

        public PostAdd(ISoulNetwork network, SoulTicket ticket, int maximum, int actualAmount) {
            super(network, ticket, maximum);
            this.actualAmount = actualAmount;
        }

        /**
         * Gets the actual amount of LP that was added.
         * May be less than requested if the network was near capacity.
         */
        public int getActualAmount() {
            return actualAmount;
        }
    }
}
