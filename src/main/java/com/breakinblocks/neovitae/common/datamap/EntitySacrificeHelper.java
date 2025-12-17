package com.breakinblocks.neovitae.common.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Helper class for looking up entity sacrifice LP values from the datamap.
 *
 * <p>This provides a simple API for rituals and other sacrifice mechanics
 * to determine how much LP an entity should generate when sacrificed.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Get LP for damaging an entity
 * float damage = 1.0f;
 * int lpGenerated = EntitySacrificeHelper.calculateLP(entity, damage);
 *
 * // Or get the raw value for custom logic
 * EntitySacrificeValue value = EntitySacrificeHelper.getSacrificeValue(entity);
 * }</pre>
 */
public final class EntitySacrificeHelper {

    private EntitySacrificeHelper() {
        // Utility class
    }

    /**
     * Gets the sacrifice value for an entity type.
     *
     * <p>Looks up the value from the datamap. If no specific entry exists,
     * returns the default value (25 LP per damage).</p>
     *
     * @param entityType The entity type to look up
     * @return The sacrifice value, never null
     */
    public static EntitySacrificeValue getSacrificeValue(EntityType<?> entityType) {
        EntitySacrificeValue value = BuiltInRegistries.ENTITY_TYPE
                .wrapAsHolder(entityType)
                .getData(BMDataMaps.ENTITY_SACRIFICE_VALUE);

        return value != null ? value : EntitySacrificeValue.DEFAULT;
    }

    /**
     * Gets the sacrifice value for a living entity.
     *
     * @param entity The entity to look up
     * @return The sacrifice value, never null
     */
    public static EntitySacrificeValue getSacrificeValue(LivingEntity entity) {
        return getSacrificeValue(entity.getType());
    }

    /**
     * Calculates the LP generated for damaging an entity.
     *
     * @param entity The entity being damaged
     * @param damage The amount of damage dealt
     * @return The LP generated
     */
    public static int calculateLP(LivingEntity entity, float damage) {
        return getSacrificeValue(entity).calculateLP(damage);
    }

    /**
     * Calculates the LP generated for damaging an entity type.
     *
     * @param entityType The entity type
     * @param damage The amount of damage dealt
     * @return The LP generated
     */
    public static int calculateLP(EntityType<?> entityType, float damage) {
        return getSacrificeValue(entityType).calculateLP(damage);
    }

    /**
     * Gets the LP per damage for an entity.
     *
     * @param entity The entity
     * @return LP generated per point of damage
     */
    public static int getLpPerDamage(LivingEntity entity) {
        return getSacrificeValue(entity).lpPerDamage();
    }

    /**
     * Checks if an entity has a custom sacrifice value defined.
     *
     * @param entityType The entity type to check
     * @return True if a custom value exists in the datamap
     */
    public static boolean hasCustomValue(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE
                .wrapAsHolder(entityType)
                .getData(BMDataMaps.ENTITY_SACRIFICE_VALUE) != null;
    }
}
