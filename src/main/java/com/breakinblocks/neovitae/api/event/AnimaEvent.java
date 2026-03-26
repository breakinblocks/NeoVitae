package com.breakinblocks.neovitae.api.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;

import java.util.UUID;

/**
 * Events fired when EV is added or syphoned from an Anima.
 * Listen to these events to track or modify EV transactions.
 */
public abstract class AnimaEvent extends Event {
    private final IAnima network;
    private final AnimaTicket ticket;

    protected AnimaEvent(IAnima network, AnimaTicket ticket) {
        this.network = network;
        this.ticket = ticket;
    }

    /**
     * Gets the anima involved in this event.
     */
    public IAnima getNetwork() {
        return network;
    }

    /**
     * Gets the UUID of the anima owner.
     */
    public UUID getOwnerId() {
        return network.getPlayerId();
    }

    /**
     * Gets the anima ticket for this transaction.
     */
    public AnimaTicket getTicket() {
        return ticket;
    }

    /**
     * Gets the EV amount being transacted.
     */
    public int getAmount() {
        return ticket.getAmount();
    }

    // ==================== Syphon Events ====================

    /**
     * Base class for syphon (EV removal) events.
     */
    public abstract static class Syphon extends AnimaEvent {
        protected Syphon(IAnima network, AnimaTicket ticket) {
            super(network, ticket);
        }
    }

    /**
     * Fired before EV is syphoned from an anima.
     * Cancel to prevent the syphon entirely.
     */
    public static class PreSyphon extends Syphon implements ICancellableEvent {
        private int modifiedAmount;

        public PreSyphon(IAnima network, AnimaTicket ticket) {
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
     * Fired after EV has been syphoned from an anima.
     * Not cancellable - use for notification/tracking only.
     */
    public static class PostSyphon extends Syphon {
        private final int actualAmount;

        public PostSyphon(IAnima network, AnimaTicket ticket, int actualAmount) {
            super(network, ticket);
            this.actualAmount = actualAmount;
        }

        /**
         * Gets the actual amount of EV that was syphoned.
         * May be less than requested if the anima didn't have enough.
         */
        public int getActualAmount() {
            return actualAmount;
        }
    }

    // ==================== Add Events ====================

    /**
     * Base class for add (EV gain) events.
     */
    public abstract static class Add extends AnimaEvent {
        private final int maximum;

        protected Add(IAnima network, AnimaTicket ticket, int maximum) {
            super(network, ticket);
            this.maximum = maximum;
        }

        /**
         * Gets the maximum EV the anima can hold.
         */
        public int getMaximum() {
            return maximum;
        }
    }

    /**
     * Fired before EV is added to an anima.
     * Cancel to prevent the addition entirely.
     */
    public static class PreAdd extends Add implements ICancellableEvent {
        private int modifiedAmount;

        public PreAdd(IAnima network, AnimaTicket ticket, int maximum) {
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
     * Fired after EV has been added to an anima.
     * Not cancellable - use for notification/tracking only.
     */
    public static class PostAdd extends Add {
        private final int actualAmount;

        public PostAdd(IAnima network, AnimaTicket ticket, int maximum, int actualAmount) {
            super(network, ticket, maximum);
            this.actualAmount = actualAmount;
        }

        /**
         * Gets the actual amount of EV that was added.
         * May be less than requested if the anima was near capacity.
         */
        public int getActualAmount() {
            return actualAmount;
        }
    }
}
