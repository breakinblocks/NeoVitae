package com.breakinblocks.neovitae.common.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import com.breakinblocks.neovitae.common.tag.NVTags;

/**
 * Helper class for looking up entity sacrifice EV values from the datamap.
 *
 * <p>This provides a simple API for rituals and other sacrifice mechanics
 * to determine how much EV an entity should generate when sacrificed.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * float damage = 1.0f;
 * int evGenerated = EntitySacrificeHelper.calculateEV(entity, damage);
 *
 * EntitySacrificeValue value = EntitySacrificeHelper.getSacrificeValue(entity);
 * }</pre>
 */
public final class EntitySacrificeHelper {

    private EntitySacrificeHelper() {}

    public static EntitySacrificeValue getSacrificeValue(EntityType<?> entityType) {
        EntitySacrificeValue value = BuiltInRegistries.ENTITY_TYPE
                .wrapAsHolder(entityType)
                .getData(NVDataMaps.ENTITY_SACRIFICE_VALUE);

        return value != null ? value : EntitySacrificeValue.DEFAULT;
    }

    public static EntitySacrificeValue getSacrificeValue(LivingEntity entity) {
        return getSacrificeValue(entity.getType());
    }

    public static int calculateEV(LivingEntity entity, float damage) {
        return calculateEV(entity.getType(), damage);
    }

    public static int calculateEV(EntityType<?> entityType, float damage) {
        if (isSacrificeBlocked(entityType)) {
            return 0;
        }
        return getSacrificeValue(entityType).calculateEV(damage);
    }

    public static boolean isSacrificeBlocked(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entityType).is(NVTags.Entities.NO_SACRIFICE);
    }

    public static int getEvPerDamage(LivingEntity entity) {
        return getSacrificeValue(entity).evPerDamage();
    }

    public static boolean hasCustomValue(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE
                .wrapAsHolder(entityType)
                .getData(NVDataMaps.ENTITY_SACRIFICE_VALUE) != null;
    }
}
