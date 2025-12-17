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
 *     "minecraft:zombie": { "lp_per_damage": 30 },
 *     "minecraft:wither": { "lp_per_damage": 500, "max_lp_per_hit": 2500 },
 *     "#minecraft:undead": { "lp_per_damage": 25 },
 *     "#c:bosses": { "lp_per_damage": 1000 }
 *   }
 * }
 * }</pre>
 *
 * <h2>Tag Support</h2>
 * <p>Entity tags are automatically supported by NeoForge datamaps. Use the
 * {@code #tag_name} syntax in the JSON to apply values to all entities in a tag.</p>
 *
 * @param lpPerDamage LP generated per point of damage dealt to this entity
 * @param maxLpPerHit Optional cap on LP generated per hit (for balancing boss mobs)
 */
public record EntitySacrificeValue(
        int lpPerDamage,
        Optional<Integer> maxLpPerHit
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
            Codec.INT.fieldOf("lp_per_damage").forGetter(EntitySacrificeValue::lpPerDamage),
            Codec.INT.optionalFieldOf("max_lp_per_hit").forGetter(EntitySacrificeValue::maxLpPerHit)
    ).apply(instance, EntitySacrificeValue::new));

    /**
     * Creates a simple sacrifice value with just LP per damage.
     */
    public static EntitySacrificeValue of(int lpPerDamage) {
        return new EntitySacrificeValue(lpPerDamage, Optional.empty());
    }

    /**
     * Creates a sacrifice value with LP per damage and a max cap.
     */
    public static EntitySacrificeValue withCap(int lpPerDamage, int maxLpPerHit) {
        return new EntitySacrificeValue(lpPerDamage, Optional.of(maxLpPerHit));
    }

    /**
     * Calculates the LP generated for a given amount of damage.
     *
     * @param damage The damage dealt to the entity
     * @return The LP generated, capped by maxLpPerHit if present
     */
    public int calculateLP(float damage) {
        int baseLP = (int) (lpPerDamage * damage);
        return maxLpPerHit.map(cap -> Math.min(baseLP, cap)).orElse(baseLP);
    }

    /**
     * Gets the max LP per hit, or Integer.MAX_VALUE if uncapped.
     */
    public int getMaxLpPerHit() {
        return maxLpPerHit.orElse(Integer.MAX_VALUE);
    }
}
