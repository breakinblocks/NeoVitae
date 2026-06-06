// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public record RitualResult(boolean successful, @Nullable FailureReason failureReason, int contextValue) {

    public static RitualResult success() {
        return new RitualResult(true, null, 0);
    }

    public static RitualResult failure(FailureReason reason) {
        return new RitualResult(false, reason, 0);
    }

    public static RitualResult failure(FailureReason reason, int contextValue) {
        return new RitualResult(false, reason, contextValue);
    }

    @Nullable
    public Component getErrorMessage() {
        if (successful || failureReason == null) {
            return null;
        }
        return failureReason.getErrorMessage(contextValue);
    }

    public enum FailureReason {
        NOT_ENOUGH_LP("chat.neovitae.ritual.notEnoughLP"),
        NO_ANIMA("chat.neovitae.ritual.noAnima"),
        EVENT_CANCELLED("chat.neovitae.ritual.eventCancelled"),
        ACTIVATION_FAILED("chat.neovitae.ritual.activationFailed"),
        MISSING_ITEM("chat.neovitae.ritual.missingItem"),
        MISSING_CONDITION("chat.neovitae.ritual.missingCondition"),
        CLIENT_SIDE("chat.neovitae.ritual.clientSide"),
        RITUAL_DISABLED("chat.neovitae.ritual.disabled"),
        UNKNOWN("chat.neovitae.ritual.unknownFailure");

        private final String translationKey;

        FailureReason(String translationKey) {
            this.translationKey = translationKey;
        }

        public Component getErrorMessage(int contextValue) {
            if (this == NOT_ENOUGH_LP && contextValue > 0) {
                return Component.translatable(translationKey, contextValue);
            }
            return Component.translatable(translationKey);
        }
    }
}
