package com.breakinblocks.neovitae.api.living;

import net.minecraft.core.component.DataComponentMap;
import java.util.function.Function;

/**
 * Interface representing a Living Armor upgrade.
 * Living Armor upgrades are enhancements applied to the Living Armor chest piece
 * that provide various benefits and abilities to the wearer.
 *
 * <p>Upgrades gain experience and level up, with each level typically providing
 * stronger effects but costing more upgrade points.</p>
 */
public interface ILivingArmorUpgrade {

    /**
     * Gets the maximum level this upgrade can reach.
     *
     * @return Maximum upgrade level
     */
    int getMaxLevel();

    /**
     * Gets the level achieved at a given amount of experience.
     *
     * @param exp Current experience amount
     * @return The level corresponding to this experience
     */
    int getLevelFromExp(float exp);

    /**
     * Gets the experience required to reach the next level.
     *
     * @param currentLevel Current upgrade level
     * @return Experience needed for next level, or 0 if at max level
     */
    float getExpForNextLevel(int currentLevel);

    /**
     * Gets the total experience required to reach a specific level.
     *
     * @param level Target level
     * @return Total experience needed to reach that level
     */
    float getTotalExpForLevel(int level);

    /**
     * Gets the upgrade point cost for a specific level.
     *
     * @param level The upgrade level
     * @return Point cost for that level
     */
    int getPointCost(int level);

    /**
     * Gets the effects provided by this upgrade.
     *
     * @return DataComponentMap containing the upgrade effects
     */
    DataComponentMap getEffects();
}
