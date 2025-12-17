package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for the Master Ritual Stone block entity.
 * Handles ritual activation, execution, and state management.
 */
public interface IMasterRitualStone {

    /**
     * @return The world this ritual stone is in
     */
    Level getLevel();

    /**
     * Alias for getLevel() for compatibility.
     */
    default Level getWorldObj() {
        return getLevel();
    }

    /**
     * @return The position of this master ritual stone
     */
    BlockPos getBlockPos();

    /**
     * Alias for getBlockPos() for compatibility.
     */
    default BlockPos getMasterBlockPos() {
        return getBlockPos();
    }

    /**
     * @return The UUID of the player who owns this ritual stone
     */
    UUID getOwner();

    /**
     * Sets the owner of this ritual stone.
     */
    void setOwner(UUID owner);

    /**
     * @return The currently active ritual, or null if none
     */
    Ritual getCurrentRitual();

    /**
     * @return Whether a ritual is currently active
     */
    boolean isActive();

    /**
     * @return The direction this ritual stone is facing
     */
    Direction getDirection();

    /**
     * @return Whether this is an inverted master ritual stone
     */
    boolean isInverted();

    /**
     * @return The current cooldown timer in ticks
     */
    int getCooldown();

    /**
     * Sets the cooldown timer.
     */
    void setCooldown(int cooldown);

    /**
     * @return The total running time of the current ritual in ticks
     */
    long getRunningTime();

    /**
     * Activates a ritual with the given activation crystal.
     *
     * @param ritual The ritual to activate
     * @param player The player activating the ritual
     * @param crystalLevel The level of the activation crystal used
     * @return True if activation was successful
     */
    boolean activateRitual(Ritual ritual, Player player, int crystalLevel);

    /**
     * Performs the current ritual's effect.
     */
    void performRitual();

    /**
     * Stops the current ritual.
     *
     * @param breakType The reason for stopping
     */
    void stopRitual(Ritual.BreakType breakType);

    /**
     * Checks if the ritual structure is valid for the given ritual.
     */
    boolean checkStructure(Ritual ritual);

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
     * @return The current demon will type configuration
     */
    EnumWillType getActiveWillConfig();

    /**
     * Sets the active demon will configuration.
     */
    void setActiveWillConfig(EnumWillType type);

    /**
     * Provides information about the ritual to the player.
     */
    void provideInformationOfRitualToPlayer(Player player);

    /**
     * Provides information about the currently selected range to the player.
     */
    void provideInformationOfRangeToPlayer(Player player, String key);

    /**
     * Provides information about the area offset to the player.
     */
    void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor);

    /**
     * Creates a soul ticket for consuming LP from the owner's network.
     */
    default SoulTicket ticket() {
        return SoulTicket.create(0);
    }

    /**
     * Creates a soul ticket with a specific LP amount.
     */
    default SoulTicket ticket(int amount) {
        return SoulTicket.create(amount);
    }

    /**
     * Gets the owner's soul network for LP operations.
     */
    default SoulNetwork getOwnerNetwork() {
        UUID owner = getOwner();
        return owner != null ? SoulNetworkHelper.getSoulNetwork(owner) : null;
    }

    /**
     * Sends a message to the owner if they are online.
     */
    void notifyOwner(Component message);
}
