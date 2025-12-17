package com.breakinblocks.neovitae.api.altar.rune;

/**
 * Mutable container for Blood Altar modifier values affected by runes.
 *
 * <p>This class is passed to {@link com.breakinblocks.neovitae.api.event.AltarRuneEvent.CalculateStats}
 * so that event handlers can modify the altar's stats based on custom rune types.</p>
 *
 * <p>All modifiers start at their base values and are modified additively or multiplicatively
 * depending on the rune type:</p>
 * <ul>
 *   <li><b>capacityMod</b> - Multiplier for altar's blood capacity (base: 1.0)</li>
 *   <li><b>tickRate</b> - Ticks between altar operations (base: 20, lower = faster)</li>
 *   <li><b>consumptionMod</b> - Bonus blood consumption per tick during crafting (base: 0)</li>
 *   <li><b>sacrificeMod</b> - Bonus LP from sacrificing mobs (base: 0)</li>
 *   <li><b>selfSacrificeMod</b> - Bonus LP from self-sacrifice (base: 0)</li>
 *   <li><b>dislocationMod</b> - Multiplier for fluid I/O rate (base: 1.0)</li>
 *   <li><b>orbCapacityMod</b> - Bonus capacity when filling blood orbs (base: 0)</li>
 *   <li><b>chargeAmountMod</b> - Amount of LP stored per charge tick (base: 0)</li>
 *   <li><b>chargeCapacityMod</b> - Maximum charge capacity (base: 0)</li>
 *   <li><b>efficiencyMod</b> - Reduces LP drain rate when altar runs out (base: 1.0)</li>
 * </ul>
 *
 * @see com.breakinblocks.neovitae.api.event.AltarRuneEvent
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
     * Creates a new modifiers container with specified values.
     *
     * @param capacityMod Capacity multiplier
     * @param tickRate Tick rate
     * @param consumptionMod Consumption bonus
     * @param sacrificeMod Sacrifice bonus
     * @param selfSacrificeMod Self-sacrifice bonus
     * @param dislocationMod Dislocation multiplier
     * @param orbCapacityMod Orb capacity bonus
     * @param chargeAmountMod Charge amount
     * @param chargeCapacityMod Charge capacity
     * @param efficiencyMod Efficiency multiplier
     */
    public AltarRuneModifiers(float capacityMod, int tickRate, float consumptionMod,
                               float sacrificeMod, float selfSacrificeMod, float dislocationMod,
                               float orbCapacityMod, float chargeAmountMod, float chargeCapacityMod,
                               float efficiencyMod) {
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

    /**
     * Creates a new modifiers container with default base values.
     */
    public AltarRuneModifiers() {
        this(1.0f, 20, 0f, 0f, 0f, 1.0f, 0f, 0f, 0f, 1.0f);
    }

    // ===== Capacity =====

    /**
     * Gets the capacity multiplier.
     * @return The capacity multiplier (base: 1.0)
     */
    public float getCapacityMod() {
        return capacityMod;
    }

    /**
     * Sets the capacity multiplier.
     * @param capacityMod The new capacity multiplier
     */
    public void setCapacityMod(float capacityMod) {
        this.capacityMod = capacityMod;
    }

    /**
     * Adds to the capacity multiplier.
     * @param amount Amount to add
     */
    public void addCapacityMod(float amount) {
        this.capacityMod += amount;
    }

    /**
     * Multiplies the capacity multiplier.
     * @param factor Factor to multiply by
     */
    public void multiplyCapacityMod(float factor) {
        this.capacityMod *= factor;
    }

    // ===== Tick Rate =====

    /**
     * Gets the tick rate (ticks between operations).
     * @return The tick rate (base: 20, lower = faster)
     */
    public int getTickRate() {
        return tickRate;
    }

    /**
     * Sets the tick rate.
     * @param tickRate The new tick rate (minimum 1)
     */
    public void setTickRate(int tickRate) {
        this.tickRate = Math.max(1, tickRate);
    }

    /**
     * Reduces the tick rate by the specified amount.
     * @param amount Amount to reduce (will not go below 1)
     */
    public void reduceTickRate(int amount) {
        this.tickRate = Math.max(1, this.tickRate - amount);
    }

    // ===== Consumption =====

    /**
     * Gets the consumption modifier (bonus LP consumed per tick).
     * @return The consumption modifier (base: 0)
     */
    public float getConsumptionMod() {
        return consumptionMod;
    }

    /**
     * Sets the consumption modifier.
     * @param consumptionMod The new consumption modifier
     */
    public void setConsumptionMod(float consumptionMod) {
        this.consumptionMod = consumptionMod;
    }

    /**
     * Adds to the consumption modifier.
     * @param amount Amount to add
     */
    public void addConsumptionMod(float amount) {
        this.consumptionMod += amount;
    }

    // ===== Sacrifice =====

    /**
     * Gets the sacrifice modifier (bonus LP from mob sacrifice).
     * @return The sacrifice modifier (base: 0)
     */
    public float getSacrificeMod() {
        return sacrificeMod;
    }

    /**
     * Sets the sacrifice modifier.
     * @param sacrificeMod The new sacrifice modifier
     */
    public void setSacrificeMod(float sacrificeMod) {
        this.sacrificeMod = sacrificeMod;
    }

    /**
     * Adds to the sacrifice modifier.
     * @param amount Amount to add
     */
    public void addSacrificeMod(float amount) {
        this.sacrificeMod += amount;
    }

    // ===== Self Sacrifice =====

    /**
     * Gets the self-sacrifice modifier (bonus LP from player sacrifice).
     * @return The self-sacrifice modifier (base: 0)
     */
    public float getSelfSacrificeMod() {
        return selfSacrificeMod;
    }

    /**
     * Sets the self-sacrifice modifier.
     * @param selfSacrificeMod The new self-sacrifice modifier
     */
    public void setSelfSacrificeMod(float selfSacrificeMod) {
        this.selfSacrificeMod = selfSacrificeMod;
    }

    /**
     * Adds to the self-sacrifice modifier.
     * @param amount Amount to add
     */
    public void addSelfSacrificeMod(float amount) {
        this.selfSacrificeMod += amount;
    }

    // ===== Dislocation =====

    /**
     * Gets the dislocation modifier (fluid I/O rate multiplier).
     * @return The dislocation modifier (base: 1.0)
     */
    public float getDislocationMod() {
        return dislocationMod;
    }

    /**
     * Sets the dislocation modifier.
     * @param dislocationMod The new dislocation modifier
     */
    public void setDislocationMod(float dislocationMod) {
        this.dislocationMod = dislocationMod;
    }

    /**
     * Multiplies the dislocation modifier.
     * @param factor Factor to multiply by
     */
    public void multiplyDislocationMod(float factor) {
        this.dislocationMod *= factor;
    }

    // ===== Orb Capacity =====

    /**
     * Gets the orb capacity modifier (bonus capacity when filling orbs).
     * @return The orb capacity modifier (base: 0)
     */
    public float getOrbCapacityMod() {
        return orbCapacityMod;
    }

    /**
     * Sets the orb capacity modifier.
     * @param orbCapacityMod The new orb capacity modifier
     */
    public void setOrbCapacityMod(float orbCapacityMod) {
        this.orbCapacityMod = orbCapacityMod;
    }

    /**
     * Adds to the orb capacity modifier.
     * @param amount Amount to add
     */
    public void addOrbCapacityMod(float amount) {
        this.orbCapacityMod += amount;
    }

    // ===== Charge Amount =====

    /**
     * Gets the charge amount modifier (LP stored per charge tick).
     * @return The charge amount modifier (base: 0)
     */
    public float getChargeAmountMod() {
        return chargeAmountMod;
    }

    /**
     * Sets the charge amount modifier.
     * @param chargeAmountMod The new charge amount modifier
     */
    public void setChargeAmountMod(float chargeAmountMod) {
        this.chargeAmountMod = chargeAmountMod;
    }

    /**
     * Adds to the charge amount modifier.
     * @param amount Amount to add
     */
    public void addChargeAmountMod(float amount) {
        this.chargeAmountMod += amount;
    }

    // ===== Charge Capacity =====

    /**
     * Gets the charge capacity modifier (maximum charge storage).
     * @return The charge capacity modifier (base: 0)
     */
    public float getChargeCapacityMod() {
        return chargeCapacityMod;
    }

    /**
     * Sets the charge capacity modifier.
     * @param chargeCapacityMod The new charge capacity modifier
     */
    public void setChargeCapacityMod(float chargeCapacityMod) {
        this.chargeCapacityMod = chargeCapacityMod;
    }

    /**
     * Adds to the charge capacity modifier.
     * @param amount Amount to add
     */
    public void addChargeCapacityMod(float amount) {
        this.chargeCapacityMod += amount;
    }

    // ===== Efficiency =====

    /**
     * Gets the efficiency modifier (reduces LP loss when altar empties mid-craft).
     * @return The efficiency modifier (base: 1.0)
     */
    public float getEfficiencyMod() {
        return efficiencyMod;
    }

    /**
     * Sets the efficiency modifier.
     * @param efficiencyMod The new efficiency modifier
     */
    public void setEfficiencyMod(float efficiencyMod) {
        this.efficiencyMod = efficiencyMod;
    }

    /**
     * Multiplies the efficiency modifier.
     * @param factor Factor to multiply by
     */
    public void multiplyEfficiencyMod(float factor) {
        this.efficiencyMod *= factor;
    }

    @Override
    public String toString() {
        return "AltarRuneModifiers{" +
                "capacityMod=" + capacityMod +
                ", tickRate=" + tickRate +
                ", consumptionMod=" + consumptionMod +
                ", sacrificeMod=" + sacrificeMod +
                ", selfSacrificeMod=" + selfSacrificeMod +
                ", dislocationMod=" + dislocationMod +
                ", orbCapacityMod=" + orbCapacityMod +
                ", chargeAmountMod=" + chargeAmountMod +
                ", chargeCapacityMod=" + chargeCapacityMod +
                ", efficiencyMod=" + efficiencyMod +
                '}';
    }
}
