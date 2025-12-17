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

    /**
     * Creates a new imperfect ritual.
     *
     * @param name             Unique identifier
     * @param blockRequirement Predicate for the block that must be above the ritual stone (fallback if no DataMap)
     * @param activationCost   LP cost to activate (fallback if no DataMap)
     * @param lightShow        Whether to show lightning effects on activation (fallback if no DataMap)
     * @param translationKey   Translation key prefix
     */
    public ImperfectRitual(String name, Predicate<BlockState> blockRequirement, int activationCost,
                          boolean lightShow, String translationKey) {
        this.name = name;
        this.blockRequirement = blockRequirement;
        this.activationCost = activationCost;
        this.lightShow = lightShow;
        this.translationKey = translationKey;
    }

    /**
     * Creates a new imperfect ritual without lightning effects.
     */
    public ImperfectRitual(String name, Predicate<BlockState> blockRequirement, int activationCost, String translationKey) {
        this(name, blockRequirement, activationCost, false, translationKey);
    }

    /**
     * Called when a player activates this ritual.
     *
     * @param imperfectRitualStone The imperfect ritual stone
     * @param player               The activating player
     * @return true if activation was successful
     */
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
