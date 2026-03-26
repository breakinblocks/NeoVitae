package com.breakinblocks.neovitae.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that can hold Living Armor upgrades.
 *
 * <p>This interface is implemented by Neo Vitae's Living Armor pieces and can be
 * used by external mods to:</p>
 * <ul>
 *   <li>Detect if an item is a Living Armor piece via {@code instanceof IUpgradeHolder}</li>
 *   <li>Query upgrade-related information from the armor</li>
 *   <li>Check if the player has a complete Living Armor set</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
 * if (chestplate.getItem() instanceof IUpgradeHolder holder) {
 *     int maxPoints = holder.getMaxUpgradePoints(chestplate, player);
 *     boolean hasFullSet = holder.hasFullLivingArmorSet(player);
 * }
 * }</pre>
 *
 * @see com.breakinblocks.neovitae.api.living.ILivingArmorUpgrade
 */
public interface IUpgradeHolder {

    /**
     * Gets the maximum upgrade points available for this armor piece.
     *
     * <p>The maximum points depend on whether the armor has evolved and
     * server configuration settings.</p>
     *
     * @param stack The armor ItemStack
     * @param player The player wearing the armor
     * @return The maximum upgrade points available
     */
    int getMaxUpgradePoints(ItemStack stack, Player player);

    /**
     * Checks if the player is wearing a complete Living Armor set.
     *
     * <p>A complete set requires all four armor slots (helmet, chestplate,
     * leggings, boots) to contain valid Living Armor pieces.</p>
     *
     * @param player The player to check
     * @return true if the player has a full Living Armor set
     */
    boolean hasFullLivingArmorSet(Player player);

    /**
     * Checks if this armor piece is in a "dead" or invalid state.
     *
     * <p>Living Armor can become invalid if it's damaged beyond repair
     * or has been corrupted in some way.</p>
     *
     * @param stack The armor ItemStack to check
     * @return true if the armor is no longer valid for upgrades
     */
    boolean isInvalidArmor(ItemStack stack);
}
