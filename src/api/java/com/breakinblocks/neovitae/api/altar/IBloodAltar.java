package com.breakinblocks.neovitae.api.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;

import javax.annotation.Nullable;

/**
 * Interface representing a Blood Altar block entity.
 * Provides access to altar state, crafting progress, and fluid handling.
 *
 * <p>Blood Altars are the core crafting mechanic in Neo Vitae, used to
 * convert items using Life Points (LP) stored as fluid.</p>
 *
 * <p>The altar tier determines what recipes can be crafted:
 * <ul>
 *   <li>Tier 1: Basic altar (no runes required)</li>
 *   <li>Tier 2: Requires rune structure</li>
 *   <li>Tier 3: Requires advanced rune structure</li>
 *   <li>Tier 4: Requires complex rune structure</li>
 *   <li>Tier 5: Requires master rune structure</li>
 *   <li>Tier 6: Requires transcendent rune structure</li>
 * </ul>
 * </p>
 */
public interface IBloodAltar {

    /**
     * Gets the current tier of this altar (0-5).
     * Tier is determined by the rune structure built around the altar.
     *
     * @return The altar tier (0 = no runes, 5 = max tier)
     */
    int getTier();

    /**
     * Gets the current amount of Life Points stored in the altar.
     *
     * @return Current LP amount
     */
    int getCurrentBlood();

    /**
     * Gets the maximum LP capacity of this altar.
     * Capacity is affected by tier and capacity runes.
     *
     * @return Maximum LP capacity
     */
    int getCapacity();

    /**
     * Gets the current crafting progress as a percentage (0.0 to 1.0).
     *
     * @return Current progress, or 0 if not crafting
     */
    float getProgressFloat();

    /**
     * Gets the LP consumption rate per tick during crafting.
     * Affected by speed runes.
     *
     * @return LP consumed per tick
     */
    int getConsumptionRate();

    /**
     * Gets the rate at which the altar drains LP from bound players.
     * Affected by sacrifice runes.
     *
     * @return LP drained per tick when filling
     */
    int getDrainRate();

    /**
     * Gets the recipe currently being crafted, if any.
     *
     * @return Current recipe, or null if not crafting
     */
    @Nullable
    BloodAltarRecipe getCurrentRecipe();

    /**
     * Gets the item stack currently in the altar.
     *
     * @return The altar's inventory item, or ItemStack.EMPTY
     */
    ItemStack getStackInSlot();

    /**
     * Gets the fluid handler for this altar.
     * The altar stores LP as a fluid (1 LP = 1 mB).
     *
     * @return The fluid handler for LP storage
     */
    IFluidHandler getFluidHandler();

    /**
     * Gets the world position of this altar.
     *
     * @return Block position
     */
    BlockPos getBlockPos();

    /**
     * Gets the level this altar is in.
     *
     * @return The level
     */
    Level getLevel();

    /**
     * Checks if the altar is currently active (crafting or filling).
     *
     * @return True if the altar is active
     */
    boolean isActive();

    /**
     * Checks if the altar can currently be filled from player sacrifice.
     *
     * @return True if filling is enabled
     */
    boolean canFill();

    /**
     * Forces a tier check on next tick.
     * Call this after modifying the rune structure.
     */
    void checkTier();

    /**
     * Gets the total LP required for the current recipe.
     *
     * @return Required LP, or 0 if not crafting
     */
    int getLiquidRequired();

    /**
     * Gets the total crafting time in ticks for the current recipe.
     *
     * @return Crafting time, or 0 if not crafting
     */
    int getTotalCraftingTime();

    /**
     * Gets the current crafting progress in ticks.
     *
     * @return Current progress in ticks
     */
    int getCraftingProgress();

    /**
     * Gets the charging rate for blood orbs in the altar.
     * Affected by orb capacity runes.
     *
     * @return Charge rate per tick
     */
    int getChargingRate();

    /**
     * Gets the charging frequency (ticks between charges).
     * Affected by acceleration runes.
     *
     * @return Ticks between charge operations
     */
    int getChargingFrequency();

    /**
     * Gets the bonus capacity for the current crafting operation.
     * Affected by better capacity runes.
     *
     * @return Bonus capacity percentage
     */
    float getBonusCapacity();

    /**
     * Gets the efficiency of this altar.
     * Affects LP consumption during crafting.
     *
     * @return Efficiency multiplier
     */
    float getEfficiency();

    /**
     * Gets the self-sacrifice bonus.
     * Affects LP gained from self-sacrifice.
     *
     * @return Self-sacrifice bonus percentage
     */
    float getSelfSacrificeBonus();

    /**
     * Gets the sacrifice bonus.
     * Affects LP gained from mob sacrifice.
     *
     * @return Sacrifice bonus percentage
     */
    float getSacrificeBonus();
}
