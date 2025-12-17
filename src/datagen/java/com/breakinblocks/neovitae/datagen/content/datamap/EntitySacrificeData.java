package com.breakinblocks.neovitae.datagen.content.datamap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeValue;

import java.util.function.Function;

/**
 * Generates default entity sacrifice LP values for Blood Magic.
 *
 * <p>These values determine how much LP is generated when entities are
 * damaged by sacrifice rituals like the Well of Suffering.</p>
 *
 * <p>Values can be overridden via datapacks at:
 * {@code data/<namespace>/data_maps/entity_type/entity_sacrifice_value.json}</p>
 *
 * <h2>Entity Tags</h2>
 * <p>Datapacks can also use entity tags for bulk configuration:</p>
 * <pre>{@code
 * {
 *   "values": {
 *     "#minecraft:undead": { "lp_per_damage": 30 },
 *     "#c:bosses": { "lp_per_damage": 500, "max_lp_per_hit": 2500 }
 *   }
 * }
 * }</pre>
 */
public class EntitySacrificeData {

    // Default value for unlisted entities
    public static final int DEFAULT_LP = 25;

    // Passive mobs - lower value (easy to farm)
    public static final int PASSIVE_LP = 25;

    // Hostile mobs - standard value
    public static final int HOSTILE_LP = 50;

    // Strong hostile mobs
    public static final int STRONG_HOSTILE_LP = 100;

    // Mini-bosses and rare mobs
    public static final int MINIBOSS_LP = 250;

    // Bosses - high value but capped
    public static final int BOSS_LP = 500;
    public static final int BOSS_CAP = 2500;

    public static void bootstrap(Function<DataMapType<EntityType<?>, EntitySacrificeValue>, DataMapProvider.Builder<EntitySacrificeValue, EntityType<?>>> setup) {
        var builder = setup.apply(BMDataMaps.ENTITY_SACRIFICE_VALUE);

        // ===== Passive Mobs =====
        // Easy to farm, low LP value
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.CHICKEN), EntitySacrificeValue.of(15), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.COW), EntitySacrificeValue.of(PASSIVE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PIG), EntitySacrificeValue.of(PASSIVE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SHEEP), EntitySacrificeValue.of(PASSIVE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.RABBIT), EntitySacrificeValue.of(15), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.HORSE), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.DONKEY), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.MULE), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.LLAMA), EntitySacrificeValue.of(35), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.MOOSHROOM), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.GOAT), EntitySacrificeValue.of(30), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.CAMEL), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SNIFFER), EntitySacrificeValue.of(75), false);

        // ===== Common Hostile Mobs =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ZOMBIE), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SKELETON), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SPIDER), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.CAVE_SPIDER), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.CREEPER), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SLIME), EntitySacrificeValue.of(20), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.DROWNED), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.HUSK), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.STRAY), EntitySacrificeValue.of(HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PHANTOM), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SILVERFISH), EntitySacrificeValue.of(15), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ENDERMITE), EntitySacrificeValue.of(25), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.BOGGED), EntitySacrificeValue.of(60), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.BREEZE), EntitySacrificeValue.of(100), false);

        // ===== Stronger Hostile Mobs =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ENDERMAN), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WITCH), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.BLAZE), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.GHAST), EntitySacrificeValue.of(150), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.MAGMA_CUBE), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ZOMBIFIED_PIGLIN), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PIGLIN), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PIGLIN_BRUTE), EntitySacrificeValue.of(150), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.HOGLIN), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ZOGLIN), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.GUARDIAN), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SHULKER), EntitySacrificeValue.of(150), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.VEX), EntitySacrificeValue.of(40), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.VINDICATOR), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PILLAGER), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WARDEN), EntitySacrificeValue.withCap(BOSS_LP, BOSS_CAP), false);

        // ===== Mini-Bosses / Rare Mobs =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ELDER_GUARDIAN), EntitySacrificeValue.of(MINIBOSS_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.EVOKER), EntitySacrificeValue.of(MINIBOSS_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.RAVAGER), EntitySacrificeValue.of(MINIBOSS_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ILLUSIONER), EntitySacrificeValue.of(MINIBOSS_LP), false);

        // ===== Bosses =====
        // High LP but capped to prevent exploitation
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WITHER), EntitySacrificeValue.withCap(BOSS_LP, BOSS_CAP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ENDER_DRAGON), EntitySacrificeValue.withCap(1000, 5000), false);

        // ===== Aquatic Mobs =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SQUID), EntitySacrificeValue.of(20), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.GLOW_SQUID), EntitySacrificeValue.of(30), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.COD), EntitySacrificeValue.of(10), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SALMON), EntitySacrificeValue.of(10), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.TROPICAL_FISH), EntitySacrificeValue.of(10), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PUFFERFISH), EntitySacrificeValue.of(20), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.DOLPHIN), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.TURTLE), EntitySacrificeValue.of(35), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.AXOLOTL), EntitySacrificeValue.of(30), false);

        // ===== Villagers & Golems =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.VILLAGER), EntitySacrificeValue.of(STRONG_HOSTILE_LP), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WANDERING_TRADER), EntitySacrificeValue.of(150), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.IRON_GOLEM), EntitySacrificeValue.of(200), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SNOW_GOLEM), EntitySacrificeValue.of(50), false);

        // ===== Misc Mobs =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.BAT), EntitySacrificeValue.of(10), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.BEE), EntitySacrificeValue.of(20), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.CAT), EntitySacrificeValue.of(25), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.FOX), EntitySacrificeValue.of(30), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.OCELOT), EntitySacrificeValue.of(30), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PANDA), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.PARROT), EntitySacrificeValue.of(20), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.POLAR_BEAR), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WOLF), EntitySacrificeValue.of(35), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.FROG), EntitySacrificeValue.of(15), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.TADPOLE), EntitySacrificeValue.of(5), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ALLAY), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ARMADILLO), EntitySacrificeValue.of(25), false);

        // ===== Zombie Variants =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ZOMBIE_VILLAGER), EntitySacrificeValue.of(75), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.ZOMBIE_HORSE), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.SKELETON_HORSE), EntitySacrificeValue.of(50), false)
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.GIANT), EntitySacrificeValue.of(MINIBOSS_LP), false);

        // ===== Wither Skeleton =====
        builder
            .add(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityType.WITHER_SKELETON), EntitySacrificeValue.of(150), false);
    }
}
