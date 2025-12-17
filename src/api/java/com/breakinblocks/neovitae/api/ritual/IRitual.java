package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Interface representing a ritual in Neo Vitae.
 * Rituals are multiblock structures built from ritual stones that provide ongoing effects.
 *
 * <p>To create a custom ritual, extend the abstract Ritual class in the main package
 * rather than implementing this interface directly.</p>
 */
public interface IRitual {

    /**
     * Performs the ritual's effect. Called every {@link #getRefreshTime()} ticks while active.
     *
     * @param masterRitualStone The master ritual stone running this ritual
     */
    void performRitual(IMasterRitualStone masterRitualStone);

    /**
     * Gets the LP cost per refresh (tick).
     *
     * @return LP drained each refresh
     */
    int getRefreshCost();

    /**
     * Gathers all rune components that make up this ritual's structure.
     *
     * @param components Consumer to add components to
     */
    void gatherComponents(Consumer<RitualComponent> components);

    /**
     * Creates a fresh copy of this ritual for a new master ritual stone.
     *
     * @return New ritual instance
     */
    IRitual getNewCopy();

    /**
     * Called when a player attempts to activate this ritual.
     *
     * @param masterRitualStone The master ritual stone
     * @param player            The activating player
     * @param owner             UUID of the ritual owner
     * @return true if activation should proceed
     */
    boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner);

    /**
     * Called when the ritual is stopped.
     *
     * @param masterRitualStone The master ritual stone
     * @param breakType         Reason for stopping
     */
    void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType);

    /**
     * Gets how often this ritual performs its effect.
     *
     * @return Ticks between each performRitual call
     */
    int getRefreshTime();

    /**
     * Gets the unique name of this ritual.
     */
    String getName();

    /**
     * Gets the required activation crystal tier.
     * 1 = weak activation crystal, 2 = awakened activation crystal
     */
    int getCrystalLevel();

    /**
     * Gets the LP cost to activate this ritual.
     */
    int getActivationCost();

    /**
     * Gets the translation key for this ritual.
     */
    String getTranslationKey();

    /**
     * Gets the area descriptor for a specific range key.
     */
    AreaDescriptor getBlockRange(String key);

    /**
     * Gets all modifiable range keys for this ritual.
     */
    List<String> getListOfRanges();

    /**
     * Reads ritual state from NBT.
     */
    void readFromNBT(CompoundTag tag);

    /**
     * Writes ritual state to NBT.
     */
    void writeToNBT(CompoundTag tag);

    /**
     * Provides information about this ritual to the player.
     */
    Component[] provideInformationOfRitualToPlayer(Player player);

    /**
     * Gets all modifiable area ranges.
     */
    Map<String, AreaDescriptor> getModifiableRanges();

    /**
     * Reasons a ritual can be stopped.
     */
    enum BreakType {
        /** Ritual deactivated by player */
        DEACTIVATE,
        /** Master ritual stone was broken */
        BREAK_MRS,
        /** A ritual stone component was broken */
        BREAK_STONE,
        /** Another ritual was activated */
        ACTIVATE,
        /** Redstone signal stopped the ritual */
        REDSTONE,
        /** Ritual was destroyed by explosion */
        EXPLOSION
    }
}
