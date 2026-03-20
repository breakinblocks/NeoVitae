package com.breakinblocks.neovitae.api.will;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Handler for managing demon will items in a player's inventory.
 *
 * <p>Provides methods to query, consume, and add demon will across all
 * will-holding items (raw will items and will gems) in a player's inventory.</p>
 *
 * <p>Access via {@code NeoVitaeAPI.getInstance().getPlayerWillHandler()}.</p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * IPlayerDemonWillHandler handler = NeoVitaeAPI.getInstance().getPlayerWillHandler();
 *
 * double total = handler.getTotalDemonWill(EnumWillType.DEFAULT, player);
 * double consumed = handler.consumeDemonWill(EnumWillType.DEFAULT, player, 10.0);
 * double added = handler.addDemonWill(EnumWillType.CORROSIVE, player, 5.0);
 * }</pre>
 */
public interface IPlayerDemonWillHandler {

    /**
     * Gets the total demon will of a specific type across all items in the player's inventory.
     */
    double getTotalDemonWill(EnumWillType type, Player player);

    /**
     * Gets the will type with the highest total amount in the player's inventory.
     */
    EnumWillType getLargestWillType(Player player);

    /**
     * Checks if all will gems in the player's inventory are full for the given type.
     */
    boolean isDemonWillFull(EnumWillType type, Player player);

    /**
     * Consumes demon will from items in the player's inventory.
     *
     * @return The amount actually consumed
     */
    double consumeDemonWill(EnumWillType type, Player player, double amount);

    /**
     * Adds demon will to gems in the player's inventory from a will item stack.
     *
     * @return The remaining will stack (empty if fully absorbed)
     */
    ItemStack addDemonWill(Player player, ItemStack willStack);

    /**
     * Adds a specific amount of demon will to gems in the player's inventory.
     *
     * @return The amount actually added
     */
    double addDemonWill(EnumWillType type, Player player, double amount);

    /**
     * Adds demon will to gems in the player's inventory, ignoring a specific stack.
     *
     * @param ignored Stack to skip (e.g., the source item)
     * @return The amount actually added
     */
    double addDemonWill(EnumWillType type, Player player, double amount, ItemStack ignored);
}
