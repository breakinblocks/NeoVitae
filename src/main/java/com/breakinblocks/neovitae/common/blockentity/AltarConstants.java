package com.breakinblocks.neovitae.common.blockentity;

/**
 * Constants for Blood Altar balance calculations.
 * Extracted for easier configuration and balance tuning.
 */
public final class AltarConstants {
    private AltarConstants() {} // Prevent instantiation

    // Base modifier values per rune
    public static final double CAPACITY_PER_RUNE = 0.2;
    public static final double AUGMENTED_CAPACITY_POWER = 1.075;
    public static final float CONSUMPTION_PER_RUNE = 0.2F;
    public static final float SACRIFICE_PER_RUNE = 0.1F;
    public static final float SELF_SACRIFICE_PER_RUNE = 0.1F;
    public static final double DISLOCATION_POWER = 1.2;
    public static final float ORB_CAPACITY_PER_RUNE = 0.2F;
    public static final int CHARGE_AMOUNT_PER_RUNE = 10;
    public static final double CHARGE_CAPACITY_MIN_FACTOR = 0.5;
    public static final double EFFICIENCY_POWER = 0.85;

    // Tick rates
    public static final int BASE_TICK_RATE = 20;
    public static final int MIN_TICK_RATE = 1;
    public static final int STRUCTURE_CHECK_INTERVAL = 20 * 5; // 5 seconds

    // IO rates
    public static final float BASE_IO_RATE = 20F;

    // Blood Altar specific constants
    public static final int CRAFTING_COOLDOWN_TICKS = 30;
    public static final int PARTICLE_FREQUENCY_REDSTONE = 4;
    public static final int PARTICLE_FREQUENCY_SMOKE = 2;
    public static final int CAPACITY_GRACE_PERIOD = 100; // 5 seconds
}
