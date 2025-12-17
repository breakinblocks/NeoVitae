package com.breakinblocks.neovitae.api.altar.rune;

/**
 * Container for Blood Altar stat modifiers calculated from runes.
 *
 * <p>This class holds all the modifier values that runes provide to the altar.
 * It is populated during altar structure scanning and can be modified by
 * addon mods via the {@link com.breakinblocks.neovitae.api.event.AltarRuneEvent.CalculateStats} event.</p>
 *
 * <p>All additive modifiers stack linearly. Multiplicative modifiers (like dislocation)
 * compound with each other.</p>
 *
 * <h2>Modifier Types</h2>
 * <ul>
 *   <li><b>Capacity</b> - Multiplier for blood tank capacity</li>
 *   <li><b>Tick Rate</b> - Ticks between altar operations (lower = faster)</li>
 *   <li><b>Consumption</b> - Additive bonus to crafting speed</li>
 *   <li><b>Sacrifice</b> - Additive bonus to entity sacrifice LP</li>
 *   <li><b>Self Sacrifice</b> - Additive bonus to self-sacrifice LP</li>
 *   <li><b>Dislocation</b> - Multiplier for fluid I/O rate</li>
 *   <li><b>Orb Capacity</b> - Additive bonus to soul network fill rate</li>
 *   <li><b>Charge Amount</b> - LP charged per tick when idle</li>
 *   <li><b>Charge Capacity</b> - Maximum stored charge</li>
 *   <li><b>Efficiency</b> - Multiplier for drain rate when crafting pauses</li>
 * </ul>
 */
public class AltarRuneModifiers {

    private float capacityMod;
    private int tickRate;
    private float consumptionMod;
    private float sacrificeMod;
    private float selfSacrificeMod;
    private float dislocationMod;
    private float orbCapacityMod;
    private float chargeAmountMod;
    private float chargeCapacityMod;
    private float efficiencyMod;

    /**
     * Creates a new modifier container with the given base values.
     */
    public AltarRuneModifiers(
            float capacityMod,
            int tickRate,
            float consumptionMod,
            float sacrificeMod,
            float selfSacrificeMod,
            float dislocationMod,
            float orbCapacityMod,
            float chargeAmountMod,
            float chargeCapacityMod,
            float efficiencyMod
    ) {
        this.capacityMod = capacityMod;
        this.tickRate = tickRate;
        this.consumptionMod = consumptionMod;
        this.sacrificeMod = sacrificeMod;
        this.selfSacrificeMod = selfSacrificeMod;
        this.dislocationMod = dislocationMod;
        this.orbCapacityMod = orbCapacityMod;
        this.chargeAmountMod = chargeAmountMod;
        this.chargeCapacityMod = chargeCapacityMod;
        this.efficiencyMod = efficiencyMod;
    }

    // ========================================
    // Getters
    // ========================================

    public float getCapacityMod() { return capacityMod; }
    public int getTickRate() { return tickRate; }
    public float getConsumptionMod() { return consumptionMod; }
    public float getSacrificeMod() { return sacrificeMod; }
    public float getSelfSacrificeMod() { return selfSacrificeMod; }
    public float getDislocationMod() { return dislocationMod; }
    public float getOrbCapacityMod() { return orbCapacityMod; }
    public float getChargeAmountMod() { return chargeAmountMod; }
    public float getChargeCapacityMod() { return chargeCapacityMod; }
    public float getEfficiencyMod() { return efficiencyMod; }

    // ========================================
    // Modifiers for event handlers
    // ========================================

    /**
     * Adds to the capacity modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addCapacityMod(float amount) {
        this.capacityMod += amount;
    }

    /**
     * Multiplies the capacity modifier.
     *
     * @param factor The multiplication factor
     */
    public void multiplyCapacityMod(float factor) {
        this.capacityMod *= factor;
    }

    /**
     * Adjusts the tick rate.
     *
     * @param adjustment The amount to add (negative = faster)
     */
    public void adjustTickRate(int adjustment) {
        this.tickRate = Math.max(1, this.tickRate + adjustment);
    }

    /**
     * Sets the tick rate directly.
     *
     * @param tickRate The new tick rate (minimum 1)
     */
    public void setTickRate(int tickRate) {
        this.tickRate = Math.max(1, tickRate);
    }

    /**
     * Adds to the consumption (crafting speed) modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addConsumptionMod(float amount) {
        this.consumptionMod += amount;
    }

    /**
     * Adds to the sacrifice modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addSacrificeMod(float amount) {
        this.sacrificeMod += amount;
    }

    /**
     * Adds to the self-sacrifice modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addSelfSacrificeMod(float amount) {
        this.selfSacrificeMod += amount;
    }

    /**
     * Multiplies the dislocation (fluid I/O) modifier.
     *
     * @param factor The multiplication factor
     */
    public void multiplyDislocationMod(float factor) {
        this.dislocationMod *= factor;
    }

    /**
     * Adds to the dislocation modifier.
     *
     * @param amount The amount to add
     */
    public void addDislocationMod(float amount) {
        this.dislocationMod += amount;
    }

    /**
     * Adds to the orb capacity modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addOrbCapacityMod(float amount) {
        this.orbCapacityMod += amount;
    }

    /**
     * Adds to the charge amount modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addChargeAmountMod(float amount) {
        this.chargeAmountMod += amount;
    }

    /**
     * Adds to the charge capacity modifier.
     *
     * @param amount The amount to add (can be negative)
     */
    public void addChargeCapacityMod(float amount) {
        this.chargeCapacityMod += amount;
    }

    /**
     * Multiplies the efficiency modifier.
     *
     * @param factor The multiplication factor
     */
    public void multiplyEfficiencyMod(float factor) {
        this.efficiencyMod *= factor;
    }

    /**
     * Sets all modifiers to default values (no bonuses).
     */
    public void reset() {
        this.capacityMod = 1.0f;
        this.tickRate = 20;
        this.consumptionMod = 0.0f;
        this.sacrificeMod = 0.0f;
        this.selfSacrificeMod = 0.0f;
        this.dislocationMod = 1.0f;
        this.orbCapacityMod = 0.0f;
        this.chargeAmountMod = 0.0f;
        this.chargeCapacityMod = 0.0f;
        this.efficiencyMod = 1.0f;
    }

    @Override
    public String toString() {
        return "AltarRuneModifiers{" +
                "capacity=" + capacityMod +
                ", tickRate=" + tickRate +
                ", consumption=" + consumptionMod +
                ", sacrifice=" + sacrificeMod +
                ", selfSacrifice=" + selfSacrificeMod +
                ", dislocation=" + dislocationMod +
                ", orbCapacity=" + orbCapacityMod +
                ", chargeAmount=" + chargeAmountMod +
                ", chargeCapacity=" + chargeCapacityMod +
                ", efficiency=" + efficiencyMod +
                '}';
    }
}
