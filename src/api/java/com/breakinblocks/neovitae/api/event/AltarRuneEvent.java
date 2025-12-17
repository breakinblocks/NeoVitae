package com.breakinblocks.neovitae.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;
import com.breakinblocks.neovitae.api.altar.rune.AltarRuneModifiers;
import com.breakinblocks.neovitae.api.altar.rune.EnumAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;

import java.util.Collections;
import java.util.Map;

/**
 * Events fired when the Blood Altar calculates or applies rune effects.
 *
 * <p>These events allow other mods to:</p>
 * <ul>
 *   <li>Add custom rune types that modify altar behavior</li>
 *   <li>Modify the effect of existing runes</li>
 *   <li>React to altar stat recalculations</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * @SubscribeEvent
 * public static void onRuneCalculate(AltarRuneEvent.CalculateStats event) {
 *     // Get any rune count (works for both built-in and custom)
 *     int speedCount = event.getRuneCount(EnumAltarRuneType.SPEED);
 *     int myRuneCount = event.getRuneCount(MyMod.MY_RUNE_TYPE);
 *
 *     // Modify altar stats based on runes
 *     if (myRuneCount > 0) {
 *         AltarRuneModifiers modifiers = event.getModifiers();
 *         modifiers.addCapacityMod(0.1f * myRuneCount);  // +10% capacity per rune
 *     }
 * }
 * }</pre>
 *
 * @see AltarRuneModifiers
 * @see IAltarRuneType
 */
public abstract class AltarRuneEvent extends Event {
    private final IBloodAltar altar;
    private final Level level;
    private final BlockPos pos;
    private final int tier;

    protected AltarRuneEvent(IBloodAltar altar, Level level, BlockPos pos, int tier) {
        this.altar = altar;
        this.level = level;
        this.pos = pos;
        this.tier = tier;
    }

    /**
     * Gets the Blood Altar involved in this event.
     * @return The blood altar
     */
    public IBloodAltar getAltar() {
        return altar;
    }

    /**
     * Gets the level containing the altar.
     * @return The level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the position of the altar.
     * @return The block position
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Gets the current tier of the altar.
     * @return The altar tier (0-5)
     */
    public int getTier() {
        return tier;
    }

    // ========================================
    // CalculateStats Event
    // ========================================

    /**
     * Fired when the Blood Altar recalculates its statistics from runes.
     *
     * <p>This event fires periodically (every 20 ticks by default) when the altar
     * checks its structure and recounts runes. Handlers can modify the
     * {@link AltarRuneModifiers} to affect altar behavior.</p>
     *
     * <p>The event provides:</p>
     * <ul>
     *   <li>All rune counts via unified map (via {@link #getRuneCount(IAltarRuneType)})</li>
     *   <li>Modifiable altar modifiers (via {@link #getModifiers()})</li>
     * </ul>
     *
     * <p>Neo Vitae calculates built-in rune effects before firing this event.
     * The modifiers passed to handlers already reflect the built-in rune effects.</p>
     *
     * <p>This event is not cancellable.</p>
     */
    public static class CalculateStats extends AltarRuneEvent {
        private final AltarRuneModifiers modifiers;
        private final Map<IAltarRuneType, Integer> allRunes;

        /**
         * Creates a new CalculateStats event.
         *
         * @param altar The blood altar
         * @param level The level
         * @param pos The altar position
         * @param tier The altar tier
         * @param modifiers The modifiers (already calculated for built-in runes)
         * @param allRunes Unified map of all rune types to counts
         */
        public CalculateStats(IBloodAltar altar, Level level, BlockPos pos, int tier,
                              AltarRuneModifiers modifiers,
                              Map<IAltarRuneType, Integer> allRunes) {
            super(altar, level, pos, tier);
            this.modifiers = modifiers;
            this.allRunes = Collections.unmodifiableMap(allRunes);
        }

        /**
         * Gets the modifiable altar modifiers.
         *
         * <p>These modifiers already reflect the effects of built-in runes.
         * Handlers can further modify these values based on custom runes.</p>
         *
         * @return The altar modifiers
         */
        public AltarRuneModifiers getModifiers() {
            return modifiers;
        }

        /**
         * Gets the count of any rune type (built-in or custom).
         *
         * @param type The rune type
         * @return The number of runes of that type (0 if none)
         */
        public int getRuneCount(IAltarRuneType type) {
            return allRunes.getOrDefault(type, 0);
        }

        /**
         * Gets the count of a built-in rune type.
         * Convenience method for backwards compatibility.
         *
         * @param type The built-in rune type
         * @return The number of runes of that type (0 if none)
         */
        public int getBuiltInRuneCount(EnumAltarRuneType type) {
            return getRuneCount(type);
        }

        /**
         * Gets the count of a custom rune type.
         * @deprecated Use {@link #getRuneCount(IAltarRuneType)} instead.
         *
         * @param type The custom rune type
         * @return The number of runes of that type (0 if none)
         */
        @Deprecated
        public int getCustomRuneCount(IAltarRuneType type) {
            return getRuneCount(type);
        }

        /**
         * Gets all rune counts (both built-in and custom).
         *
         * @return An unmodifiable map of rune type to count
         */
        public Map<IAltarRuneType, Integer> getAllRunes() {
            return allRunes;
        }

        /**
         * Gets built-in rune counts only.
         * @deprecated Use {@link #getAllRunes()} for unified access.
         *
         * @return An unmodifiable map of built-in rune types to counts
         */
        @Deprecated
        public Map<EnumAltarRuneType, Integer> getBuiltInRunes() {
            // Filter to only built-in types for backwards compatibility
            java.util.Map<EnumAltarRuneType, Integer> builtIn = new java.util.HashMap<>();
            for (EnumAltarRuneType type : EnumAltarRuneType.values()) {
                int count = allRunes.getOrDefault(type, 0);
                if (count > 0) {
                    builtIn.put(type, count);
                }
            }
            return Collections.unmodifiableMap(builtIn);
        }

        /**
         * Gets custom rune counts only (non-built-in types).
         * @deprecated Use {@link #getAllRunes()} for unified access.
         *
         * @return An unmodifiable map of custom rune types to counts
         */
        @Deprecated
        public Map<IAltarRuneType, Integer> getCustomRunes() {
            // Filter to only custom types for backwards compatibility
            java.util.Map<IAltarRuneType, Integer> custom = new java.util.HashMap<>();
            for (Map.Entry<IAltarRuneType, Integer> entry : allRunes.entrySet()) {
                if (!(entry.getKey() instanceof EnumAltarRuneType)) {
                    custom.put(entry.getKey(), entry.getValue());
                }
            }
            return Collections.unmodifiableMap(custom);
        }
    }

    // ========================================
    // PostCalculate Event
    // ========================================

    /**
     * Fired after the Blood Altar has finished calculating and applying all rune effects.
     *
     * <p>This event is informational and fires after all modifications have been applied.
     * Use this to react to the final altar stats without modifying them.</p>
     *
     * <p>This event is not cancellable.</p>
     */
    public static class PostCalculate extends AltarRuneEvent {
        private final AltarRuneModifiers finalModifiers;

        /**
         * Creates a new PostCalculate event.
         *
         * @param altar The blood altar
         * @param level The level
         * @param pos The altar position
         * @param tier The altar tier
         * @param finalModifiers The final calculated modifiers
         */
        public PostCalculate(IBloodAltar altar, Level level, BlockPos pos, int tier,
                             AltarRuneModifiers finalModifiers) {
            super(altar, level, pos, tier);
            this.finalModifiers = finalModifiers;
        }

        /**
         * Gets the final calculated modifiers.
         *
         * <p>These values reflect the final state after all event handlers
         * have had a chance to modify them.</p>
         *
         * @return The final modifiers (read-only view recommended)
         */
        public AltarRuneModifiers getFinalModifiers() {
            return finalModifiers;
        }
    }

    // ========================================
    // GatherRunes Event
    // ========================================

    /**
     * Fired when the Blood Altar gathers rune information from surrounding blocks.
     *
     * <p>This event allows handlers to add runes that might not be detected by
     * the standard structure check. For example, runes that are applied via
     * enchantments, items, or other non-block sources.</p>
     *
     * <p>This event fires before {@link CalculateStats}.</p>
     */
    public static class GatherRunes extends AltarRuneEvent {
        private final Map<IAltarRuneType, Integer> allRunes;

        /**
         * Creates a new GatherRunes event.
         *
         * @param altar The blood altar
         * @param level The level
         * @param pos The altar position
         * @param tier The altar tier
         * @param allRunes Mutable map of all rune counts
         */
        public GatherRunes(IBloodAltar altar, Level level, BlockPos pos, int tier,
                           Map<IAltarRuneType, Integer> allRunes) {
            super(altar, level, pos, tier);
            this.allRunes = allRunes;
        }

        /**
         * Adds runes to the count (works for any rune type).
         *
         * @param type The rune type
         * @param count The number of runes to add
         */
        public void addRunes(IAltarRuneType type, int count) {
            allRunes.merge(type, count, Integer::sum);
        }

        /**
         * Adds built-in runes to the count.
         * Convenience method for backwards compatibility.
         *
         * @param type The rune type
         * @param count The number of runes to add
         */
        public void addBuiltInRunes(EnumAltarRuneType type, int count) {
            addRunes(type, count);
        }

        /**
         * Adds custom runes to the count.
         * @deprecated Use {@link #addRunes(IAltarRuneType, int)} instead.
         *
         * @param type The custom rune type
         * @param count The number of runes to add
         */
        @Deprecated
        public void addCustomRunes(IAltarRuneType type, int count) {
            addRunes(type, count);
        }

        /**
         * Gets all current rune counts (mutable).
         *
         * @return The mutable map of all runes
         */
        public Map<IAltarRuneType, Integer> getAllRunes() {
            return allRunes;
        }

        /**
         * Gets the current built-in rune counts.
         * @deprecated Use {@link #getAllRunes()} for unified access.
         *
         * @return A filtered map of built-in runes only
         */
        @Deprecated
        public Map<EnumAltarRuneType, Integer> getBuiltInRunes() {
            java.util.Map<EnumAltarRuneType, Integer> builtIn = new java.util.HashMap<>();
            for (EnumAltarRuneType type : EnumAltarRuneType.values()) {
                int count = allRunes.getOrDefault(type, 0);
                if (count > 0) {
                    builtIn.put(type, count);
                }
            }
            return builtIn;
        }

        /**
         * Gets the current custom rune counts.
         * @deprecated Use {@link #getAllRunes()} for unified access.
         *
         * @return A filtered map of custom runes only
         */
        @Deprecated
        public Map<IAltarRuneType, Integer> getCustomRunes() {
            java.util.Map<IAltarRuneType, Integer> custom = new java.util.HashMap<>();
            for (Map.Entry<IAltarRuneType, Integer> entry : allRunes.entrySet()) {
                if (!(entry.getKey() instanceof EnumAltarRuneType)) {
                    custom.put(entry.getKey(), entry.getValue());
                }
            }
            return custom;
        }
    }
}
