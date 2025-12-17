package com.breakinblocks.neovitae.ritual;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Result of attempting to perform a ritual.
 * Contains success/failure status and an optional failure reason for proper error messaging.
 */
public record RitualResult(boolean successful, @Nullable FailureReason failureReason, int contextValue) {

    /**
     * Creates a successful result.
     */
    public static RitualResult success() {
        return new RitualResult(true, null, 0);
    }

    /**
     * Creates a failure result with a reason.
     */
    public static RitualResult failure(FailureReason reason) {
        return new RitualResult(false, reason, 0);
    }

    /**
     * Creates a failure result with a reason and context value (e.g., required LP amount).
     */
    public static RitualResult failure(FailureReason reason, int contextValue) {
        return new RitualResult(false, reason, contextValue);
    }

    /**
     * Gets the localized error message for this result.
     *
     * @return The error component, or null if successful
     */
    @Nullable
    public Component getErrorMessage() {
        if (successful || failureReason == null) {
            return null;
        }
        return failureReason.getErrorMessage(contextValue);
    }

    /**
     * Reasons a ritual can fail.
     */
    public enum FailureReason {
        /**
         * Player doesn't have enough LP in their soul network.
         */
        NOT_ENOUGH_LP("chat.neovitae.ritual.notEnoughLP"),

        /**
         * Player has no soul network (not bound to an orb).
         */
        NO_SOUL_NETWORK("chat.neovitae.ritual.noSoulNetwork"),

        /**
         * The ritual's activation was cancelled by an event.
         */
        EVENT_CANCELLED("chat.neovitae.ritual.eventCancelled"),

        /**
         * The ritual's onActivate method returned false (generic failure).
         */
        ACTIVATION_FAILED("chat.neovitae.ritual.activationFailed"),

        /**
         * Ritual requires a specific item to be held or present.
         */
        MISSING_ITEM("chat.neovitae.ritual.missingItem"),

        /**
         * Ritual requires a specific effect/condition that wasn't found.
         */
        MISSING_CONDITION("chat.neovitae.ritual.missingCondition"),

        /**
         * The world is client-side (shouldn't happen in normal use).
         */
        CLIENT_SIDE("chat.neovitae.ritual.clientSide"),

        /**
         * The ritual has been disabled via datapack.
         */
        RITUAL_DISABLED("chat.neovitae.ritual.disabled"),

        /**
         * Generic unknown failure.
         */
        UNKNOWN("chat.neovitae.ritual.unknownFailure");

        private final String translationKey;

        FailureReason(String translationKey) {
            this.translationKey = translationKey;
        }

        /**
         * Gets the error message component.
         *
         * @param contextValue Optional context value (e.g., LP amount for NOT_ENOUGH_LP)
         * @return The translated error message
         */
        public Component getErrorMessage(int contextValue) {
            if (this == NOT_ENOUGH_LP && contextValue > 0) {
                return Component.translatable(translationKey, contextValue);
            }
            return Component.translatable(translationKey);
        }
    }
}
