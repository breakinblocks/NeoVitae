package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;
import com.breakinblocks.neovitae.api.soul.SoulTicket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Interface for the Master Ritual Stone block entity.
 * Handles ritual activation, execution, and state management.
 *
 * <p>The Master Ritual Stone is the central block of any ritual structure.
 * When right-clicked with an activation crystal, it validates the ritual
 * structure and begins running the ritual.</p>
 */
public interface IMasterRitualStone {

    /**
     * Gets the world this ritual stone is in.
     */
    Level getLevel();

    /**
     * Alias for getLevel() for compatibility.
     */
    default Level getWorldObj() {
        return getLevel();
    }

    /**
     * Gets the position of this master ritual stone.
     */
    BlockPos getBlockPos();

    /**
     * Alias for getBlockPos() for compatibility.
     */
    default BlockPos getMasterBlockPos() {
        return getBlockPos();
    }

    /**
     * Gets the UUID of the player who owns this ritual stone.
     */
    @Nullable
    UUID getOwner();

    /**
     * Sets the owner of this ritual stone.
     */
    void setOwner(UUID owner);

    /**
     * Gets the currently active ritual, or null if none.
     */
    @Nullable
    IRitual getCurrentRitual();

    /**
     * Checks if a ritual is currently active.
     */
    boolean isActive();

    /**
     * Gets the direction this ritual stone is facing.
     */
    Direction getDirection();

    /**
     * Checks if this is an inverted master ritual stone.
     */
    boolean isInverted();

    /**
     * Gets the current cooldown timer in ticks.
     */
    int getCooldown();

    /**
     * Sets the cooldown timer.
     */
    void setCooldown(int cooldown);

    /**
     * Gets the total running time of the current ritual in ticks.
     */
    long getRunningTime();

    /**
     * Activates a ritual with the given activation crystal.
     *
     * @param ritual       The ritual to activate
     * @param player       The player activating the ritual
     * @param crystalLevel The level of the activation crystal used
     * @return True if activation was successful
     */
    boolean activateRitual(IRitual ritual, Player player, int crystalLevel);

    /**
     * Performs the current ritual's effect.
     */
    void performRitual();

    /**
     * Stops the current ritual.
     *
     * @param breakType The reason for stopping
     */
    void stopRitual(IRitual.BreakType breakType);

    /**
     * Checks if the ritual structure is valid for the given ritual.
     */
    boolean checkStructure(IRitual ritual);

    /**
     * Gets the current area range configuration for the given key.
     */
    AreaDescriptor getBlockRange(String key);

    /**
     * Gets all modifiable block ranges.
     */
    Map<String, AreaDescriptor> getBlockRanges();

    /**
     * Sets the block range for the given key.
     */
    void setBlockRange(String key, AreaDescriptor descriptor);

    /**
     * Sets all block ranges at once.
     */
    void setBlockRanges(Map<String, AreaDescriptor> ranges);

    /**
     * Provides information about the ritual to the player.
     */
    void provideInformationOfRitualToPlayer(Player player);

    /**
     * Provides information about the currently selected range to the player.
     */
    void provideInformationOfRangeToPlayer(Player player, String key);

    /**
     * Creates a soul ticket for consuming LP from the owner's network.
     */
    default SoulTicket ticket() {
        return SoulTicket.block(getLevel(), getBlockPos());
    }

    /**
     * Creates a soul ticket with a specific LP amount.
     */
    default SoulTicket ticket(int amount) {
        return SoulTicket.block(getLevel(), getBlockPos(), amount);
    }

    /**
     * Gets the owner's soul network for LP operations.
     */
    @Nullable
    ISoulNetwork getOwnerNetwork();

    /**
     * Sends a message to the owner if they are online.
     */
    void notifyOwner(Component message);
}
