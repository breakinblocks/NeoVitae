package com.breakinblocks.neovitae.api.spiritus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Handler for managing spiritus items in a player's inventory.
 *
 * <p>Provides methods to query, consume, and add spiritus across all
 * will-holding items (raw Spiritus items and will gems) in a player's inventory.</p>
 *
 * <p>Access via {@code NeoVitaeAPI.getInstance().getPlayerSpiritusHandler()}.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * IPlayerSpiritusHandler handler = NeoVitaeAPI.getInstance().getPlayerSpiritusHandler();
 *
 * double total = handler.getTotalSpiritus(SpiritusType.RAW, player);
 * double consumed = handler.consumeSpiritus(SpiritusType.RAW, player, 10.0);
 * double added = handler.addSpiritus(SpiritusType.RUINA, player, 5.0);
 * }</pre>
 */
public interface IPlayerSpiritusHandler {

    /**
     * Gets the total spiritus of a specific type across all items in the player's inventory.
     */
    double getTotalSpiritus(SpiritusType type, Player player);

    /**
     * Gets the will type with the highest total amount in the player's inventory.
     */
    SpiritusType getLargestSpiritusType(Player player);

    /**
     * Checks if all will gems in the player's inventory are full for the given type.
     */
    boolean isSpiritusFull(SpiritusType type, Player player);

    /**
     * Consumes spiritus from items in the player's inventory.
     *
     * @return The amount actually consumed
     */
    double consumeSpiritus(SpiritusType type, Player player, double amount);

    /**
     * Adds spiritus to gems in the player's inventory from a will item stack.
     *
     * @return The remaining will stack (empty if fully absorbed)
     */
    ItemStack addSpiritus(Player player, ItemStack spiritusStack);

    /**
     * Adds a specific amount of spiritus to gems in the player's inventory.
     *
     * @return The amount actually added
     */
    double addSpiritus(SpiritusType type, Player player, double amount);

    /**
     * Adds spiritus to gems in the player's inventory, ignoring a specific stack.
     *
     * @param ignored Stack to skip (e.g., the source item)
     * @return The amount actually added
     */
    double addSpiritus(SpiritusType type, Player player, double amount, ItemStack ignored);
}
