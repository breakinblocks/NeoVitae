package com.breakinblocks.neovitae.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.projectile.EntityBloodLight;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.entity.projectile.EntityPotionFlask;
import com.breakinblocks.neovitae.common.entity.projectile.EntityShapedCharge;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDaggerSyringe;
import com.breakinblocks.neovitae.common.entity.projectile.SoulSnareEntity;

public class BMEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SoulSnareEntity>> SOUL_SNARE = ENTITIES.register("soul_snare",
            () -> EntityType.Builder.<SoulSnareEntity>of(SoulSnareEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("soul_snare"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityBloodLight>> BLOOD_LIGHT = ENTITIES.register("blood_light",
            () -> EntityType.Builder.<EntityBloodLight>of(EntityBloodLight::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(6)
                    .updateInterval(10)
                    .build("blood_light"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityMeteor>> METEOR = ENTITIES.register("meteor",
            () -> EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build("meteor"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityPotionFlask>> POTION_FLASK = ENTITIES.register("potion_flask",
            () -> EntityType.Builder.<EntityPotionFlask>of(EntityPotionFlask::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("potion_flask"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityShapedCharge>> SHAPED_CHARGE = ENTITIES.register("shaped_charge",
            () -> EntityType.Builder.<EntityShapedCharge>of(EntityShapedCharge::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(10)
                    .build("shaped_charge"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityThrowingDagger>> THROWING_DAGGER = ENTITIES.register("throwing_dagger",
            () -> EntityType.Builder.<EntityThrowingDagger>of(EntityThrowingDagger::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("throwing_dagger"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityThrowingDaggerSyringe>> THROWING_DAGGER_SYRINGE = ENTITIES.register("throwing_dagger_syringe",
            () -> EntityType.Builder.<EntityThrowingDaggerSyringe>of(EntityThrowingDaggerSyringe::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("throwing_dagger_syringe"));

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
    }
}
