package com.breakinblocks.neovitae.api.altar;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Interface representing a Blood Altar's public functionality.
 *
 * <p>This interface provides read-only access to the altar's current state,
 * including blood levels, crafting progress, and rune-modified stats.
 * It is implemented by the Blood Altar block entity and exposed via capabilities.</p>
 *
 * <h2>Accessing the Altar</h2>
 * <p>The altar interface can be accessed via block entity capabilities:</p>
 * <pre>{@code
 * IBloodAltar altar = level.getCapability(BMCapabilities.BLOOD_ALTAR, pos, null);
 * if (altar != null) {
 *     int blood = altar.getCurrentBlood();
 *     int tier = altar.getTier();
 * }
 * }</pre>
 *
 * <h2>Common Operations</h2>
 * <ul>
 *   <li>Check blood levels: {@link #getCurrentBlood()}, {@link #getCapacity()}</li>
 *   <li>Check crafting: {@link #getProgressFloat()}, {@link #getLiquidRequired()}</li>
 *   <li>Check tier: {@link #getTier()}</li>
 *   <li>Access fluid handler: {@link #getFluidHandler()}</li>
 * </ul>
 */
public interface IBloodAltar {

    /**
     * Gets the current altar tier (0-indexed).
     * Tier 0 = no structure, Tier 1-5 = valid structures.
     *
     * @return The current tier
     */
    int getTier();

    /**
     * Gets the current amount of blood (LP) in the main tank.
     *
     * @return The current blood amount in mB
     */
    int getCurrentBlood();

    /**
     * Gets the maximum blood capacity of the main tank.
     * This is affected by capacity runes.
     *
     * @return The maximum capacity in mB
     */
    int getCapacity();

    /**
     * Gets the crafting progress as a float between 0.0 and 1.0.
     *
     * @return The progress (0.0 = not started, 1.0 = complete)
     */
    float getProgressFloat();

    /**
     * Gets the crafting progress as raw LP consumed.
     *
     * @return The LP consumed so far
     */
    int getCraftingProgress();

    /**
     * Gets the blood consumption rate per tick during crafting.
     * This is affected by speed runes.
     *
     * @return LP consumed per tick
     */
    int getConsumptionRate();

    /**
     * Gets the blood drain rate per tick when crafting is paused.
     * This is affected by efficiency runes.
     *
     * @return LP lost per tick when paused
     */
    int getDrainRate();

    /**
     * Gets the total LP required to complete the current recipe.
     *
     * @return The total LP required, or 0 if no recipe
     */
    int getLiquidRequired();

    /**
     * Gets the total crafting time in ticks for the current recipe.
     *
     * @return The total time in ticks, or 0 if no recipe
     */
    int getTotalCraftingTime();

    /**
     * Gets the item currently in the altar's input slot.
     *
     * @return The input item stack
     */
    ItemStack getStackInSlot();

    /**
     * Gets the fluid handler for direct fluid interaction.
     * Use this for inserting/extracting blood via pipes.
     *
     * @return The fluid handler
     */
    IFluidHandler getFluidHandler();

    /**
     * Forces a tier recalculation.
     * Called when the structure may have changed.
     */
    void checkTier();

    /**
     * Gets the charging rate (LP per tick when idle).
     *
     * @return The charging rate
     */
    int getChargingRate();

    /**
     * Gets the charging frequency (ticks between charge operations).
     *
     * @return The tick rate
     */
    int getChargingFrequency();

    /**
     * Gets the capacity bonus multiplier from runes.
     *
     * @return The capacity multiplier (1.0 = no bonus)
     */
    float getBonusCapacity();

    /**
     * Gets the efficiency multiplier from runes.
     * Lower values mean less blood is lost when crafting pauses.
     *
     * @return The efficiency multiplier
     */
    float getEfficiency();

    /**
     * Gets the self-sacrifice bonus multiplier from runes.
     *
     * @return The bonus (0.0 = no bonus, 1.0 = +100%)
     */
    float getSelfSacrificeBonus();

    /**
     * Gets the sacrifice bonus multiplier from runes.
     *
     * @return The bonus (0.0 = no bonus, 1.0 = +100%)
     */
    float getSacrificeBonus();

    /**
     * Gets the speed/consumption multiplier from runes.
     * Higher values mean faster crafting but more LP consumed per tick.
     *
     * @return The speed bonus (0.0 = base speed)
     */
    float getSpeedBonus();

    /**
     * Gets the dislocation/displacement multiplier from runes.
     * Higher values mean faster fluid transfer via pipes.
     *
     * @return The dislocation multiplier (1.0 = base rate)
     */
    float getDislocationBonus();

    /**
     * Gets the orb capacity bonus from runes.
     * Increases the effective capacity when filling blood orbs.
     *
     * @return The orb capacity bonus (0.0 = no bonus)
     */
    float getOrbCapacityBonus();

    /**
     * Gets the acceleration value from runes.
     * Lower tick rate = faster operations.
     *
     * @return The tick rate (lower is faster)
     */
    int getTickRate();
}
