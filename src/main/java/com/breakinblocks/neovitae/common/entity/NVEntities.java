package com.breakinblocks.neovitae.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCorrodisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCruorisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumGlaciarisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumIgnisEntity;
import com.breakinblocks.neovitae.common.entity.mob.SlimeVitaeEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumAnimarisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumDolorisEntity;
import com.breakinblocks.neovitae.common.entity.mob.NecromancySummonEntity;
import com.breakinblocks.neovitae.common.entity.mob.NecromancySummonHuskEntity;
import com.breakinblocks.neovitae.common.entity.mob.NecromancySummonSkeletonEntity;
import com.breakinblocks.neovitae.common.entity.mob.NecromancySummonStrayEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumVoraxisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumFervidisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumPestisEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumRancorisEntity;
import com.breakinblocks.neovitae.common.entity.projectile.EntityBloodLight;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.entity.projectile.EntityPotionFlask;
import com.breakinblocks.neovitae.common.entity.projectile.EntityShapedCharge;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDaggerSyringe;
import com.breakinblocks.neovitae.common.entity.projectile.SpiritusSnareEntity;

public class NVEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SpiritusSnareEntity>> SPIRITUS_SNARE = ENTITIES.register("spiritus_snare",
            () -> EntityType.Builder.<SpiritusSnareEntity>of(SpiritusSnareEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("spiritus_snare"));

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

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumIgnisEntity>> DAEMONIUM_IGNIS = ENTITIES.register("daemonium_ignis",
            () -> EntityType.Builder.<DaemoniumIgnisEntity>of(DaemoniumIgnisEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 2.8F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .fireImmune()
                    .build("daemonium_ignis"));

    public static final DeferredHolder<EntityType<?>, EntityType<BloodShieldEntity>> BLOOD_SHIELD = ENTITIES.register("blood_shield",
            () -> EntityType.Builder.<BloodShieldEntity>of(BloodShieldEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build("blood_shield"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumCorrodisEntity>> DAEMONIUM_CORRODIS = ENTITIES.register("daemonium_corrodis",
            () -> EntityType.Builder.<DaemoniumCorrodisEntity>of(DaemoniumCorrodisEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 2.2F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_corrodis"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumCruorisEntity>> DAEMONIUM_CRUORIS = ENTITIES.register("daemonium_cruoris",
            () -> EntityType.Builder.<DaemoniumCruorisEntity>of(DaemoniumCruorisEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.8F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_cruoris"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumGlaciarisEntity>> DAEMONIUM_GLACIARIS = ENTITIES.register("daemonium_glaciaris",
            () -> EntityType.Builder.<DaemoniumGlaciarisEntity>of(DaemoniumGlaciarisEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.2F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_glaciaris"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumPestisEntity>> DAEMONIUM_PESTIS = ENTITIES.register("daemonium_pestis",
            () -> EntityType.Builder.<DaemoniumPestisEntity>of(DaemoniumPestisEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("daemonium_pestis"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumVoraxisEntity>> DAEMONIUM_VORAXIS = ENTITIES.register("daemonium_voraxis",
            () -> EntityType.Builder.<DaemoniumVoraxisEntity>of(DaemoniumVoraxisEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.8F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_voraxis"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumDolorisEntity>> DAEMONIUM_DOLORIS = ENTITIES.register("daemonium_doloris",
            () -> EntityType.Builder.<DaemoniumDolorisEntity>of(DaemoniumDolorisEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 3.0F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_doloris"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumFervidisEntity>> DAEMONIUM_FERVIDIS = ENTITIES.register("daemonium_fervidis",
            () -> EntityType.Builder.<DaemoniumFervidisEntity>of(DaemoniumFervidisEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 2.8F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_fervidis"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumAnimarisEntity>> DAEMONIUM_ANIMARIS = ENTITIES.register("daemonium_animaris",
            () -> EntityType.Builder.<DaemoniumAnimarisEntity>of(DaemoniumAnimarisEntity::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.8F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("daemonium_animaris"));

    public static final DeferredHolder<EntityType<?>, EntityType<DaemoniumRancorisEntity>> DAEMONIUM_RANCORIS = ENTITIES.register("daemonium_rancoris",
            () -> EntityType.Builder.<DaemoniumRancorisEntity>of(DaemoniumRancorisEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.2F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("daemonium_rancoris"));

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancySummonEntity>> NECROMANCY_SUMMON = ENTITIES.register("necromancy_summon",
            () -> EntityType.Builder.<NecromancySummonEntity>of(NecromancySummonEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("necromancy_summon"));

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancySummonHuskEntity>> NECROMANCY_SUMMON_HUSK = ENTITIES.register("necromancy_summon_husk",
            () -> EntityType.Builder.<NecromancySummonHuskEntity>of(NecromancySummonHuskEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("necromancy_summon_husk"));

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancySummonSkeletonEntity>> NECROMANCY_SUMMON_SKELETON = ENTITIES.register("necromancy_summon_skeleton",
            () -> EntityType.Builder.<NecromancySummonSkeletonEntity>of(NecromancySummonSkeletonEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("necromancy_summon_skeleton"));

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancySummonStrayEntity>> NECROMANCY_SUMMON_STRAY = ENTITIES.register("necromancy_summon_stray",
            () -> EntityType.Builder.<NecromancySummonStrayEntity>of(NecromancySummonStrayEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("necromancy_summon_stray"));

    public static final DeferredHolder<EntityType<?>, EntityType<SlimeVitaeEntity>> SLIME_VITAE = ENTITIES.register("slime_vitae",
            () -> EntityType.Builder.<SlimeVitaeEntity>of(SlimeVitaeEntity::new, MobCategory.MONSTER)
                    .sized(2.04F, 2.04F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build("slime_vitae"));

    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DAEMONIUM_IGNIS.get(), DaemoniumIgnisEntity.createAttributes().build());
        event.put(DAEMONIUM_CORRODIS.get(), DaemoniumCorrodisEntity.createAttributes().build());
        event.put(DAEMONIUM_CRUORIS.get(), DaemoniumCruorisEntity.createAttributes().build());
        event.put(DAEMONIUM_GLACIARIS.get(), DaemoniumGlaciarisEntity.createAttributes().build());
        event.put(DAEMONIUM_RANCORIS.get(), DaemoniumRancorisEntity.createAttributes().build());
        event.put(DAEMONIUM_ANIMARIS.get(), DaemoniumAnimarisEntity.createAttributes().build());
        event.put(DAEMONIUM_FERVIDIS.get(), DaemoniumFervidisEntity.createAttributes().build());
        event.put(DAEMONIUM_PESTIS.get(), DaemoniumPestisEntity.createAttributes().build());
        event.put(DAEMONIUM_VORAXIS.get(), DaemoniumVoraxisEntity.createAttributes().build());
        event.put(DAEMONIUM_DOLORIS.get(), DaemoniumDolorisEntity.createAttributes().build());
        event.put(NECROMANCY_SUMMON.get(), NecromancySummonEntity.createAttributes().build());
        event.put(NECROMANCY_SUMMON_HUSK.get(), NecromancySummonHuskEntity.createAttributes().build());
        event.put(NECROMANCY_SUMMON_SKELETON.get(), NecromancySummonSkeletonEntity.createAttributes().build());
        event.put(NECROMANCY_SUMMON_STRAY.get(), NecromancySummonStrayEntity.createAttributes().build());
        event.put(SLIME_VITAE.get(), net.minecraft.world.entity.monster.Monster.createMonsterAttributes().build());
    }

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
        modBus.addListener(NVEntities::registerEntityAttributes);
    }
}
