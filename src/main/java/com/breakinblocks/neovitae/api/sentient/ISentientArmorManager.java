package com.breakinblocks.neovitae.api.sentient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Manager interface for Sentient Armor operations.
 * Provides methods to query and modify Sentient Armor upgrades on players.
 *
 * <p>Access via the Neo Vitae API:</p>
 * <pre>{@code
 * INeoVitaeAPI api = NeoVitaeAPI.get();
 * ISentientArmorManager manager = api.getSentientArmorManager();
 * if (manager.hasFullSet(player)) {
 *     List<UpgradeInfo> upgrades = manager.getUpgrades(player);
 * }
 * }</pre>
 */
public interface ISentientArmorManager {

    /**
     * Checks if a player is wearing a full set of Sentient Armor.
     *
     * @param player The player to check
     * @return true if wearing full Sentient Armor set
     */
    boolean hasFullSet(Player player);

    /**
     * Gets the Sentient Armor chest piece if the player is wearing one.
     *
     * @param player The player to check
     * @return The chest piece ItemStack, or ItemStack.EMPTY if not wearing
     */
    ItemStack getChestPiece(Player player);

    /**
     * Gets all upgrades on the player's Sentient Armor.
     *
     * @param player The player to check
     * @return List of upgrade information, or empty list if no Sentient Armor
     */
    List<UpgradeInfo> getUpgrades(Player player);

    /**
     * Gets the level of a specific upgrade on the player's armor.
     *
     * @param player    The player to check
     * @param upgradeId The resource location of the upgrade
     * @return The upgrade level, or 0 if not present
     */
    int getUpgradeLevel(Player player, ResourceLocation upgradeId);

    /**
     * Grants experience to a specific upgrade on the player's armor.
     *
     * @param player    The player
     * @param upgradeId The resource location of the upgrade
     * @param amount    The amount of experience to grant
     * @return true if experience was successfully granted
     */
    boolean grantUpgradeExperience(Player player, ResourceLocation upgradeId, float amount);

    /**
     * Gets the current experience for a specific upgrade.
     *
     * @param player    The player
     * @param upgradeId The resource location of the upgrade
     * @return Current experience amount, or 0 if upgrade not present
     */
    float getUpgradeExperience(Player player, ResourceLocation upgradeId);

    /**
     * Gets the total upgrade points used by the player's armor.
     *
     * @param player The player to check
     * @return Total points used
     */
    int getUsedUpgradePoints(Player player);

    /**
     * Gets the default maximum upgrade points available.
     *
     * <p>Note: For armor that has evolved, use {@link #getMaxUpgradePoints(Player)}
     * to get the actual maximum based on the armor's state.</p>
     *
     * @return Default maximum points (typically 100)
     */
    int getMaxUpgradePoints();

    /**
     * Gets the maximum upgrade points available for the player's current armor.
     *
     * <p>This takes into account armor evolution and any other modifiers
     * that may have increased the maximum points.</p>
     *
     * @param player The player to check
     * @return Maximum points for this player's armor, or default if not wearing Sentient Armor
     */
    int getMaxUpgradePoints(Player player);

    /**
     * Gets the available (unused) upgrade points on the player's armor.
     *
     * @param player The player to check
     * @return Available points
     */
    int getAvailableUpgradePoints(Player player);

    /**
     * Information about a single upgrade on Sentient Armor.
     *
     * @param upgradeId The resource location identifier of the upgrade
     * @param level     Current level of the upgrade
     * @param experience Current experience accumulated
     * @param pointCost Point cost for the current level
     */
    record UpgradeInfo(ResourceLocation upgradeId, int level, float experience, int pointCost) {
    }
}
