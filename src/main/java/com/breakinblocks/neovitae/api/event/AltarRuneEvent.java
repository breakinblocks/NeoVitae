package com.breakinblocks.neovitae.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;
import com.breakinblocks.neovitae.api.altar.IAraVitae;
import com.breakinblocks.neovitae.api.altar.rune.AltarRuneModifiers;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.RuneInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Events fired during Ara Vitae rune scanning and stat calculation.
 *
 * <p>These events allow addon mods to:</p>
 * <ul>
 *   <li>Add virtual rune counts during structure scanning</li>
 *   <li>Modify stat calculations based on custom rune types</li>
 *   <li>Access the actual rune block positions and block entities</li>
 * </ul>
 *
 * <h2>Event Order</h2>
 * <ol>
 *   <li>{@link GatherRunes} - Fired after scanning, allows adding virtual runes</li>
 *   <li>{@link CalculateStats} - Fired during stat calculation, allows modifying bonuses</li>
 *   <li>{@link PostCalculate} - Fired after stats are finalized, informational only</li>
 * </ol>
 *
 * <h2>Accessing Rune Instances</h2>
 * <p>The {@link CalculateStats} and {@link PostCalculate} events provide access to
 * the actual rune instances found during scanning. This eliminates the need for
 * addon mods to re-scan the altar structure:</p>
 *
 * <pre>{@code
 * @SubscribeEvent
 * public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
 *     // Filter to find our custom rune block entities
 *     List<MyRuneBlockEntity> myRunes = event.getRuneBlockEntities(MyRuneBlockEntity.class);
 *
 *     for (MyRuneBlockEntity rune : myRunes) {
 *         if (rune.isPowered()) {
 *             event.getModifiers().addConsumptionMod(0.15f);
 *         } else {
 *             event.getModifiers().addConsumptionMod(-0.10f);
 *         }
 *     }
 * }
 * }</pre>
 */
public abstract class AltarRuneEvent extends Event {

    private final IAraVitae altar;
    private final Level level;
    private final BlockPos pos;
    private final int tier;

    protected AltarRuneEvent(IAraVitae altar, Level level, BlockPos pos, int tier) {
        this.altar = altar;
        this.level = level;
        this.pos = pos;
        this.tier = tier;
    }

    /**
     * Gets the Ara Vitae that triggered this event.
     *
     * @return the altar interface for querying current altar state
     */
    public IAraVitae getAltar() {
        return altar;
    }

    /**
     * Gets the level (world) the altar is in.
     *
     * @return the level containing the altar
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the block position of the altar.
     *
     * @return the altar's position in the world
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Gets the altar's current tier (0-indexed).
     * Tier 0 means no valid multiblock structure; tiers 1-5 correspond to
     * progressively larger altar builds.
     *
     * @return the current altar tier
     */
    public int getTier() {
        return tier;
    }

    // ========================================
    // GatherRunes Event
    // ========================================

    /**
     * Event fired after the altar structure has been scanned for runes.
     *
     * <p>Use this event to add "virtual" runes that aren't physically present
     * in the structure. For example, an item in the player's inventory that
     * provides rune bonuses.</p>
     *
     * <p>The rune counts map can be modified directly to add or remove rune counts.</p>
     */
    public static class GatherRunes extends AltarRuneEvent {

        private final Map<IAltarRuneType, Integer> runeCounts;
        private final List<RuneInstance> runeInstances;

        public GatherRunes(IAraVitae altar, Level level, BlockPos pos, int tier,
                          Map<IAltarRuneType, Integer> runeCounts, List<RuneInstance> runeInstances) {
            super(altar, level, pos, tier);
            this.runeCounts = runeCounts;
            this.runeInstances = runeInstances;
        }

        /**
         * Gets the mutable map of rune types to their counts.
         * Modify this map directly or use {@link #addRunes} to inject virtual runes.
         *
         * @return mutable map of rune type to count
         */
        public Map<IAltarRuneType, Integer> getRuneCounts() {
            return runeCounts;
        }

        /**
         * Adds runes of the specified type.
         *
         * @param type The rune type to add
         * @param amount The amount to add
         */
        public void addRunes(IAltarRuneType type, int amount) {
            runeCounts.merge(type, amount, Integer::sum);
        }

        /**
         * Gets all physical rune instances found during structure scanning.
         * This list is read-only; to add virtual runes, use {@link #addRunes} instead.
         *
         * @return unmodifiable list of scanned rune instances
         */
        public List<RuneInstance> getRuneInstances() {
            return Collections.unmodifiableList(runeInstances);
        }
    }

    // ========================================
    // CalculateStats Event
    // ========================================

    /**
     * Event fired during stat calculation, after base values are computed.
     *
     * <p>Use this event to modify the altar's stat bonuses based on custom rune
     * logic. This is the primary event for addon mods that add custom rune types
     * with dynamic behavior.</p>
     *
     * <h2>Dynamic Runes Example</h2>
     * <pre>{@code
     * @SubscribeEvent
     * public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
     *     // Find all our arcane rune block entities
     *     for (BlockEntityArcaneRune rune : event.getRuneBlockEntities(BlockEntityArcaneRune.class)) {
     *         if (rune.hasSource()) {
     *             // Powered: +35% speed, +35% dislocation
     *             event.getModifiers().addConsumptionMod(0.15f); // +15% on top of base 20%
     *             event.getModifiers().multiplyDislocationMod(1.35f);
     *         } else {
     *             // Unpowered: -35% from base 20% = -15% total
     *             event.getModifiers().addConsumptionMod(-0.35f);
     *         }
     *     }
     * }
     * }</pre>
     */
    public static class CalculateStats extends AltarRuneEvent {

        private final AltarRuneModifiers modifiers;
        private final Map<IAltarRuneType, Integer> runeCounts;
        private final List<RuneInstance> runeInstances;

        public CalculateStats(IAraVitae altar, Level level, BlockPos pos, int tier,
                             AltarRuneModifiers modifiers, Map<IAltarRuneType, Integer> runeCounts,
                             List<RuneInstance> runeInstances) {
            super(altar, level, pos, tier);
            this.modifiers = modifiers;
            this.runeCounts = runeCounts;
            this.runeInstances = runeInstances;
        }

        /**
         * Gets the mutable modifiers container. Changes made here directly
         * affect the altar's operational stats (speed, capacity, sacrifice bonuses, etc.).
         *
         * @return the modifiers to adjust
         */
        public AltarRuneModifiers getModifiers() {
            return modifiers;
        }

        /**
         * Gets the read-only map of rune types to their final counts
         * (including any virtual runes added during {@link GatherRunes}).
         *
         * @return unmodifiable map of rune type to count
         */
        public Map<IAltarRuneType, Integer> getRuneCounts() {
            return Collections.unmodifiableMap(runeCounts);
        }

        /**
         * Gets the count of a specific rune type.
         *
         * @param type The rune type to check
         * @return The count, or 0 if not present
         */
        public int getRuneCount(IAltarRuneType type) {
            return runeCounts.getOrDefault(type, 0);
        }

        /**
         * Gets all physical rune instances found during structure scanning.
         *
         * @return unmodifiable list of scanned rune instances
         */
        public List<RuneInstance> getRuneInstances() {
            return Collections.unmodifiableList(runeInstances);
        }

        /**
         * Gets all rune instances with block entities of the specified type.
         *
         * <p>This is a convenience method for finding custom rune block entities
         * without having to iterate and filter manually.</p>
         *
         * @param type The block entity class to filter by
         * @param <T> The block entity type
         * @return List of matching block entities
         */
        public <T extends BlockEntity> List<T> getRuneBlockEntities(Class<T> type) {
            List<T> result = new ArrayList<>();
            for (RuneInstance instance : runeInstances) {
                T be = instance.getBlockEntityAs(type);
                if (be != null) {
                    result.add(be);
                }
            }
            return result;
        }

        /**
         * Gets all rune instances that match a specific rune type.
         *
         * @param runeType The rune type to filter by
         * @return List of matching rune instances
         */
        public List<RuneInstance> getRuneInstancesByType(IAltarRuneType runeType) {
            List<RuneInstance> result = new ArrayList<>();
            var registry = com.breakinblocks.neovitae.api.NeoVitaeAPI.getInstance().getRuneRegistry();
            for (RuneInstance instance : runeInstances) {
                Map<IAltarRuneType, Integer> blockRunes = registry.getRunesForBlock(instance.block());
                if (blockRunes.containsKey(runeType)) {
                    result.add(instance);
                }
            }
            return result;
        }
    }

    // ========================================
    // PostCalculate Event
    // ========================================

    /**
     * Event fired after all stat calculations are complete.
     *
     * <p>This is an informational event - the modifiers are finalized and
     * will be applied to the altar after this event completes.
     * Use this for logging, debugging, or triggering side effects.</p>
     */
    public static class PostCalculate extends AltarRuneEvent {

        private final AltarRuneModifiers finalModifiers;
        private final List<RuneInstance> runeInstances;

        public PostCalculate(IAraVitae altar, Level level, BlockPos pos, int tier,
                            AltarRuneModifiers finalModifiers, List<RuneInstance> runeInstances) {
            super(altar, level, pos, tier);
            this.finalModifiers = finalModifiers;
            this.runeInstances = runeInstances;
        }

        /**
         * Gets the finalized modifiers that will be applied to the altar.
         *
         * <p>While modifications to this object are technically possible and will
         * still take effect, this event is intended for read-only observation.
         * Use {@link CalculateStats} to make intentional modifications.</p>
         *
         * @return the final computed modifiers
         */
        public AltarRuneModifiers getFinalModifiers() {
            return finalModifiers;
        }

        /**
         * Gets all physical rune instances found during structure scanning.
         *
         * @return unmodifiable list of scanned rune instances
         */
        public List<RuneInstance> getRuneInstances() {
            return Collections.unmodifiableList(runeInstances);
        }
    }
}
