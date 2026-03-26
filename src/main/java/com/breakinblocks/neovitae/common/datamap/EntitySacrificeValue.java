package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Data-driven sacrifice LP values for entities.
 *
 * <p>Defines how much LP is generated when an entity is damaged by the
 * Well of Suffering ritual or other sacrifice mechanics.</p>
 *
 * <h2>Priority System</h2>
 * <p>When looking up an entity's sacrifice value, the system checks in order:</p>
 * <ol>
 *   <li>Specific entity type entry (e.g., {@code minecraft:zombie})</li>
 *   <li>Entity tag entries (e.g., {@code #minecraft:undead})</li>
 *   <li>Default value (25 LP per damage)</li>
 * </ol>
 *
 * <h2>Example Datapack</h2>
 * <pre>{@code
 * // data/neovitae/data_maps/entity_type/entity_sacrifice_value.json
 * {
 *   "values": {
 *     "minecraft:zombie": { "ev_per_damage": 30 },
 *     "minecraft:wither": { "ev_per_damage": 500, "max_lp_per_hit": 2500 },
 *     "#minecraft:undead": { "ev_per_damage": 25 },
 *     "#c:bosses": { "ev_per_damage": 1000 }
 *   }
 * }
 * }</pre>
 *
 * <h2>Tag Support</h2>
 * <p>Entity tags are automatically supported by NeoForge datamaps. Use the
 * {@code #tag_name} syntax in the JSON to apply values to all entities in a tag.</p>
 *
 * @param evPerDamage LP generated per point of damage dealt to this entity
 * @param maxEvPerHit Optional cap on LP generated per hit (for balancing boss mobs)
 */
public record EntitySacrificeValue(
        int evPerDamage,
        Optional<Integer> maxEvPerHit
) {
    /**
     * Default LP per damage for entities without a specific value.
     */
    public static final int DEFAULT_LP_PER_DAMAGE = 25;

    /**
     * Default sacrifice value used as fallback.
     */
    public static final EntitySacrificeValue DEFAULT = new EntitySacrificeValue(DEFAULT_LP_PER_DAMAGE, Optional.empty());

    public static final Codec<EntitySacrificeValue> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("ev_per_damage").forGetter(EntitySacrificeValue::evPerDamage),
            Codec.INT.optionalFieldOf("max_lp_per_hit").forGetter(EntitySacrificeValue::maxEvPerHit)
    ).apply(instance, EntitySacrificeValue::new));

    /**
     * Creates a simple sacrifice value with just LP per damage.
     */
    public static EntitySacrificeValue of(int evPerDamage) {
        return new EntitySacrificeValue(evPerDamage, Optional.empty());
    }

    /**
     * Creates a sacrifice value with LP per damage and a max cap.
     */
    public static EntitySacrificeValue withCap(int evPerDamage, int maxEvPerHit) {
        return new EntitySacrificeValue(evPerDamage, Optional.of(maxEvPerHit));
    }

    /**
     * Calculates the LP generated for a given amount of damage.
     *
     * @param damage The damage dealt to the entity
     * @return The LP generated, capped by maxEvPerHit if present
     */
    public int calculateLP(float damage) {
        int baseEV = (int) (evPerDamage * damage);
        return maxEvPerHit.map(cap -> Math.min(baseEV, cap)).orElse(baseEV);
    }

    /**
     * Gets the max LP per hit, or Integer.MAX_VALUE if uncapped.
     */
    public int getMaxLpPerHit() {
        return maxEvPerHit.orElse(Integer.MAX_VALUE);
    }
}
