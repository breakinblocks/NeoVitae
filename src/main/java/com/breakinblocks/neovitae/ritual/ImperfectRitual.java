// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitual;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Abstract base class for imperfect rituals.
 * Imperfect rituals are simpler, one-time effects triggered by
 * placing a specific block above an imperfect ritual stone.
 *
 * Block requirements and costs are data-driven via ImperfectRitualStats DataMap.
 */
public abstract class ImperfectRitual implements IImperfectRitual {

    private final String name;
    private final Predicate<BlockState> blockRequirement;
    private final int activationCost;
    private final boolean lightShow;
    private final String translationKey;

    public ImperfectRitual(String name, Predicate<BlockState> blockRequirement, int activationCost,
                          boolean lightShow, String translationKey) {
        this.name = name;
        this.blockRequirement = blockRequirement;
        this.activationCost = activationCost;
        this.lightShow = lightShow;
        this.translationKey = translationKey;
    }

    public ImperfectRitual(String name, Predicate<BlockState> blockRequirement, int activationCost, String translationKey) {
        this(name, blockRequirement, activationCost, false, translationKey);
    }

    @Override
    public abstract boolean onActivate(com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone imperfectRitualStone, Player player);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Predicate<BlockState> getBlockRequirement() {
        return blockRequirement;
    }

    @Override
    public int getActivationCost() {
        return activationCost;
    }

    @Override
    public boolean isLightShow() {
        return lightShow;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImperfectRitual that)) return false;
        return activationCost == that.activationCost &&
               Objects.equals(name, that.name) &&
               Objects.equals(translationKey, that.translationKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, activationCost, translationKey);
    }

    @Override
    public String toString() {
        return "ImperfectRitual{name='%s', cost=%d}".formatted(name, activationCost);
    }
}
