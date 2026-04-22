package com.breakinblocks.neovitae.datagen.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.living.effects.CauseExhaustionEffect;
import com.breakinblocks.neovitae.common.living.LivingEffectComponents;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.living.effects.*;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LivingUpgrades {
    // Downgrades
    public static final ResourceKey<LivingUpgrade> BATTLE_HUNGRY = key("battle_hungry");
    public static final ResourceKey<LivingUpgrade> CRIPPLED_ARM = key("crippled_arm");
    public static final ResourceKey<LivingUpgrade> DIG_SLOWDOWN = key("dig_slowdown");
    public static final ResourceKey<LivingUpgrade> MELEE_DECREASE = key("melee_decrease");
    public static final ResourceKey<LivingUpgrade> QUENCHED = key("quenched");
    public static final ResourceKey<LivingUpgrade> SLOW_HEAL = key("slow_heal");
    public static final ResourceKey<LivingUpgrade> SPEED_DECREASE = key("speed_decrease");
    public static final ResourceKey<LivingUpgrade> STORM_TROOPER = key("storm_trooper");
    public static final ResourceKey<LivingUpgrade> SWIM_DECREASE = key("swim_decrease");

    // Upgrades
    public static final ResourceKey<LivingUpgrade> ARROW_PROTECT = key("arrow_protect");
    public static final ResourceKey<LivingUpgrade> CURIOS_SOCKET = key("curios_socket");
    public static final ResourceKey<LivingUpgrade> NETHERITE_PROTECT = key("netherite_protect");
    public static final ResourceKey<LivingUpgrade> DIGGING = key("digging");
    public static final ResourceKey<LivingUpgrade> ELYTRA = key("elytra");
    public static final ResourceKey<LivingUpgrade> EXPERIENCED = key("experienced");
    public static final ResourceKey<LivingUpgrade> FALL_PROTECT = key("fall_protect");
    public static final ResourceKey<LivingUpgrade> FIRE_RESIST = key("fire_resist");
    public static final ResourceKey<LivingUpgrade> GILDED = key("gilded");
    public static final ResourceKey<LivingUpgrade> HEALTH = key("health");
    public static final ResourceKey<LivingUpgrade> JUMP = key("jump");
    public static final ResourceKey<LivingUpgrade> KNOCKBACK_RESIST = key("knockback_resist");
    public static final ResourceKey<LivingUpgrade> MELEE_DAMAGE = key("melee_damage");
    public static final ResourceKey<LivingUpgrade> PHYSICAL_PROTECT = key("physical_protect");
    public static final ResourceKey<LivingUpgrade> POISON_RESIST = key("poison_resist");
    public static final ResourceKey<LivingUpgrade> REPAIR = key("repair");
    public static final ResourceKey<LivingUpgrade> SELF_SACRIFICE = key("self_sacrifice");
    public static final ResourceKey<LivingUpgrade> SPEED = key("speed");
    public static final ResourceKey<LivingUpgrade> SPRINT_ATTACK = key("sprint_attack");

    public static final ResourceKey<LivingUpgrade> LUCK = key("luck");

    public static void bootstrap(BootstrapContext<LivingUpgrade> context) {
        context.register(
                BATTLE_HUNGRY,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -20)
                        .level(3, -30)
                        .level(4, -40)
                        .level(5, -50)
                        .withEffect(LivingEffectComponents.TICK.get(), new CooldownEffect(BATTLE_HUNGRY.identifier()))
                        .withEffect(LivingEffectComponents.TICK.get(), new ResetCooldownEffect(BATTLE_HUNGRY.identifier(), LevelBasedValue.constant(20), Optional.of(new CauseExhaustionEffect(LevelBasedValue.lookup(List.of(0.02F, 0.04F, 0.06F, 0.08F, 0.1F), LevelBasedValue.constant(0))))), cooldownCondition(BATTLE_HUNGRY))
                        .withEffect(LivingEffectComponents.DEALING_DAMAGE.get(), new DelegateEffect(new ResetCooldownEffect(BATTLE_HUNGRY.identifier(), LevelBasedValue.lookup(List.of(600f, 600f, 600f, 500f, 400f), LevelBasedValue.constant(300)), Optional.empty())))
                        .build()
        );
        context.register(
                CRIPPLED_ARM,
                new LivingUpgrade.Builder()
                        .level(1, -150)
                        .withEffect(LivingEffectComponents.CRIPPLED_ARM.get())
                        .build()
        );
        context.register(
                DIG_SLOWDOWN,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -17)
                        .level(3, -28)
                        .level(4, -42)
                        .level(5, -60)
                        .level(6, -80)
                        .level(7, -100)
                        .level(8, -125)
                        .level(9, -160)
                        .level(10, -200)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(DIG_SLOWDOWN.identifier(), Attributes.BLOCK_BREAK_SPEED, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(-0.1f, -0.2f, -0.3f, -0.4f, -0.45f, -0.5f, -0.6f, -0.65f, -0.7f, -0.8f), LevelBasedValue.constant(-0.8f))))
                        .build()
        );
        context.register(
                MELEE_DECREASE,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -17)
                        .level(3, -28)
                        .level(4, -42)
                        .level(5, -60)
                        .level(6, -80)
                        .level(7, -100)
                        .level(8, -125)
                        .level(9, -160)
                        .level(10, -200)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(MELEE_DECREASE.identifier(), Attributes.ATTACK_DAMAGE, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(-0.1f, -0.2f, -0.25f, -0.3f, -0.35f, -0.4f,  -0.5f, -0.6f, -0.7f, -0.8f), LevelBasedValue.constant(-0.8f))))
                        .build()
        );
        context.register(
                QUENCHED,
                new LivingUpgrade.Builder()
                        .level(1, -100)
                        .withEffect(LivingEffectComponents.QUENCHED.get())
                        .build()
        );
        context.register(
                SLOW_HEAL,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -17)
                        .level(3, -28)
                        .level(4, -42)
                        .level(5, -60)
                        .level(6, -80)
                        .level(7, -100)
                        .level(8, -125)
                        .level(9, -160)
                        .level(10, -200)
                        .withEffect(LivingEffectComponents.HEALING.get(), new MultiplyReduceValue(LevelBasedValue.lookup(List.of(0.1f, 0.2f, 0.3f, 0.4f, 0.45f, 0.5f, 0.6f, 0.65f, 0.7f, 0.8f), LevelBasedValue.constant(0.9f))))
                        .build()
        );
        context.register(
                SPEED_DECREASE,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -17)
                        .level(3, -28)
                        .level(4, -42)
                        .level(5, -60)
                        .level(6, -80)
                        .level(7, -100)
                        .level(8, -125)
                        .level(9, -160)
                        .level(10, -200)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(SPEED_DECREASE.identifier(), Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(-0.1f, -0.2f, -0.3f, -0.4f, -0.45f, -0.5f, -0.6f, -0.65f, -0.7f, -0.8f), LevelBasedValue.constant(-0.8f))))
                        .build()
        );
        context.register(
                STORM_TROOPER,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -25)
                        .level(3, -40)
                        .level(4, -65)
                        .level(5, -90)
                        .withEffect(LivingEffectComponents.PROJECTILE_SHOT.get(), new MovementModifier(LevelBasedValue.lookup(List.of(0.04f, 0.08f, 0.12f, 0.16f, 0.2f), LevelBasedValue.constant(0))))
                        .build()
        );
        context.register(
                SWIM_DECREASE,
                new LivingUpgrade.Builder()
                        .level(1, -10)
                        .level(2, -17)
                        .level(3, -28)
                        .level(4, -42)
                        .level(5, -60)
                        .level(6, -80)
                        .level(7, -100)
                        .level(8, -125)
                        .level(9, -160)
                        .level(10, -200)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(SWIM_DECREASE.identifier(), NeoForgeMod.SWIM_SPEED, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(-0.1f, -0.2f, -0.25f, -0.3f, -0.35f, -0.4f,  -0.5f, -0.6f, -0.7f, -0.8f), LevelBasedValue.constant(-0.9f))))
                        .build()
        );
        LootItemCondition.Builder arrowDamage = DamageSourceCondition.hasDamageSource(
                DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                        .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
        );
        context.register(
                ARROW_PROTECT,
                new LivingUpgrade.Builder()
                        .level(30, 4)
                        .level(200, 9)
                        .level(400, 16)
                        .level(800, 30)
                        .level(1500, 60)
                        .level(2500, 90)
                        .level(3500, 125)
                        .level(5000, 165)
                        .level(7000, 210)
                        .level(15000, 250)
                        .withEffect(LivingEffectComponents.TAKING_DAMAGE.get(), new MultiplyReduceValue(LevelBasedValue.lookup(List.of(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F), LevelBasedValue.constant(0))), arrowDamage)
                        .build()
        );
        // Curios Socket upgrade - adds living_armour_socket curios slots
        // Slot count is managed by CuriosCompat.recalculateCuriosSlots()
        context.register(
                CURIOS_SOCKET,
                new LivingUpgrade.Builder()
                        .level(1, 10)
                        .level(2, 30)
                        .level(3, 70)
                        .level(4, 150)
                        .level(5, 310)
                        .build()
        );
        context.register(
                NETHERITE_PROTECT,
                new LivingUpgrade.Builder() // TODO 6 dias = 1 xp, 1 1xp tome netherite upgraded = 1000xp
                        .level(1, 6)
                        .level(2, 10)
                        .level(3, 18)
                        .level(4, 25)
                        .level(1000, 40)
                        .level(2000, 55)
                        .level(3000, 70)
                        .level(4000, 85)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(NETHERITE_PROTECT.identifier(), Attributes.ARMOR, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(1f, 3f, 4f, 5f, 5f, 5f, 5f, 5f), LevelBasedValue.constant(0f))))
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(NETHERITE_PROTECT.identifier(), Attributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(2f, 4f, 6f, 8f, 9f, 10f, 11f, 12f), LevelBasedValue.constant(0f))))
                        .build()
        );
        context.register(
                DIGGING,
                new LivingUpgrade.Builder()
                        .level(128, 5)
                        .level(512, 10)
                        .level(1024, 18)
                        .level(2048, 32)
                        .level(8192, 60)
                        .level(16000, 90)
                        .level(32000, 140)
                        .level(50000, 180)
                        .level(80000, 240)
                        .level(150000, 300)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(DIGGING.identifier(), Attributes.MINING_EFFICIENCY, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 1f, 1.2f, 1.5f), LevelBasedValue.constant(0f))))
                        .withEffect(LivingEffectComponents.BREAK_BLOCK.get(), new AddMobEffect(MobEffects.HASTE, LevelBasedValue.lookup(List.of(0f, 0f, 0f, 1f, 1f, 1f, 1f, 1f, 2f, 2f), LevelBasedValue.constant(0)), LevelBasedValue.lookup(List.of(0f, 50f, 60f, 100f, 100f, 100f, 100f, 150f, 150f, 150f), LevelBasedValue.constant(0))))
                        .build()
        );
        context.register(
                ELYTRA,
                new LivingUpgrade.Builder()
                        .level(1, 15)
                        .withEffect(LivingEffectComponents.ELYTRA.get())
                        .build()
        );
        context.register(
                EXPERIENCED,
                new LivingUpgrade.Builder()
                        .level(100, 7)
                        .level(400, 13)
                        .level(1000, 22)
                        .level(1600, 40)
                        .level(3200, 65)
                        .level(5000, 90)
                        .level(7000, 130)
                        .level(9200, 180)
                        .level(11500, 250)
                        .level(14000, 350)
                        .withEffect(LivingEffectComponents.EXP_PICKUP.get(), new MultiplyIncreaseValue(LevelBasedValue.lookup(List.of(0.15f, 0.3f, 0.45f, 0.6f, 0.75f, 0.9f, 1.05f, 1.2f, 1.35f, 1.5f), LevelBasedValue.constant(1))))
                        .build()
        );
        LootItemCondition.Builder fallDamage = DamageSourceCondition.hasDamageSource(
                DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                        .tag(TagPredicate.is(DamageTypeTags.IS_FALL))
        );
        context.register(
                FALL_PROTECT,
                new LivingUpgrade.Builder()
                        .level(30, 2)
                        .level(200, 5)
                        .level(400, 9)
                        .level(800, 15)
                        .level(1500, 25)
                        .withEffect(LivingEffectComponents.TAKING_DAMAGE.get(), new MultiplyReduceValue(LevelBasedValue.lookup(List.of(0.2F, 0.4F, 0.6F, 0.8F, 1F), LevelBasedValue.constant(0))), fallDamage)
                        .build()
        );
        context.register(
                FIRE_RESIST,
                new LivingUpgrade.Builder()
                        .level(1200, 2)
                        .level(3600, 6)
                        .level(12000, 14)
                        .level(24000, 25)
                        .level(30000, 40)
                        .withEffect(LivingEffectComponents.TICK.get(), new CooldownEffect(FIRE_RESIST.identifier()))
                        .withEffect(LivingEffectComponents.TICK.get(), new ResetCooldownEffect(FIRE_RESIST.identifier(), LevelBasedValue.lookup(List.of(6000f, 4800f, 4800f, 3600f, 2400f), LevelBasedValue.constant(6000)), Optional.of(new AddMobEffect(MobEffects.FIRE_RESISTANCE, LevelBasedValue.constant(0f), LevelBasedValue.lookup(List.of(600f, 600f, 800f, 1000f, 1200f), LevelBasedValue.constant(0))))), AllOfCondition.allOf(cooldownCondition(FIRE_RESIST), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setOnFire(true)))))
                        .build()
        );
        context.register(
                GILDED,
                new LivingUpgrade.Builder()
                        .level(1, 5)
                        .withEffect(LivingEffectComponents.GILDED.get())
                        .build()
        );
        context.register(
                HEALTH,
                new LivingUpgrade.Builder()
                        .level(80, 5)
                        .level(200, 12)
                        .level(340, 20)
                        .level(540, 35)
                        .level(800, 49)
                        .level(1600, 78)
                        .level(2800, 110)
                        .level(5000, 160)
                        .level(7600, 215)
                        .level(10000, 320)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(HEALTH.identifier(), Attributes.MAX_HEALTH, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(4f, 8f, 12f, 16f, 20f, 26f, 32f, 38f, 44f, 50f), LevelBasedValue.constant(0f))))
                        .build()
        );
        context.register(
                JUMP,
                new LivingUpgrade.Builder()
                        .level(30, 3)
                        .level(200, 6)
                        .level(400, 11)
                        .level(700, 23)
                        .level(1100, 37)
                        .level(1500, 50)
                        .level(2000, 70)
                        .level(2800, 100)
                        .level(3600, 140)
                        .level(5000, 200)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(JUMP.identifier(), Attributes.JUMP_STRENGTH, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 0.75f, 0.9f, 1.1f, 1.3f), LevelBasedValue.constant(0f))))
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(JUMP.identifier(), Attributes.FALL_DAMAGE_MULTIPLIER, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(-0.33f, -0.4f, -0.45f, -0.5f, -0.55f, -0.6f, -0.65f, -0.75f, -0.85f, -0.95f), LevelBasedValue.constant(0f))))
                        .build()
        );
        context.register(
                KNOCKBACK_RESIST,
                new LivingUpgrade.Builder()
                        .level(100, 3)
                        .level(200, 7)
                        .level(300, 13)
                        .level(500, 26)
                        .level(1000, 42)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(KNOCKBACK_RESIST.identifier(), Attributes.MAX_HEALTH, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(0f, 0f, 0f, 4f, 10f), LevelBasedValue.constant(0f))))
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(KNOCKBACK_RESIST.identifier(), Attributes.KNOCKBACK_RESISTANCE, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(0.2f, 0.4f, 0.6f, 0.8f, 1f), LevelBasedValue.constant(0f))))
                        .build()
        );
        context.register(
                MELEE_DAMAGE,
                new LivingUpgrade.Builder()
                        .level(200, 5)
                        .level(800, 12)
                        .level(1300, 20)
                        .level(2500, 35)
                        .level(3800, 29)
                        .level(5000, 78)
                        .level(7000, 110)
                        .level(9200, 160)
                        .level(11500, 215)
                        .level(14000, 320)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(MELEE_DAMAGE.identifier(), Attributes.ATTACK_DAMAGE, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(0.5f, 1f, 1.5f, 2f, 2.5f, 3f, 4f, 5f, 6f, 7f), LevelBasedValue.constant(0f))))
                        .build()
        );
        LootItemCondition.Builder physicalDamage = DamageSourceCondition.hasDamageSource(
                DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))
                        .tag(TagPredicate.isNot(NVTags.DamageTypes.TOUGH_IGNORED))
        );
        context.register(
                PHYSICAL_PROTECT,
                new LivingUpgrade.Builder()
                        .level(30, 5)
                        .level(200, 10)
                        .level(400, 18)
                        .level(800, 35)
                        .level(1500, 65)
                        .level(2500, 100)
                        .level(3500, 140)
                        .level(5000, 190)
                        .level(7000, 250)
                        .level(15000, 300)
                        .withEffect(LivingEffectComponents.TAKING_DAMAGE.get(), new MultiplyReduceValue(LevelBasedValue.lookup(List.of(0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F), LevelBasedValue.constant(0))), physicalDamage)
                        .build()
        );
        context.register(
                POISON_RESIST,
                new LivingUpgrade.Builder()
                        .level(1200, 2)
                        .level(3600, 6)
                        .level(12000, 14)
                        .level(24000, 25)
                        .level(30000, 40)
                        .withEffect(LivingEffectComponents.TICK.get(), new CooldownEffect(POISON_RESIST.identifier()))
                        .withEffect(LivingEffectComponents.TICK.get(), new ResetCooldownEffect(POISON_RESIST.identifier(), LevelBasedValue.lookup(List.of(1200f, 800f, 600f, 300f, 100f), LevelBasedValue.constant(1200)), Optional.of(new RemoveMobEffect(MobEffects.POISON, LevelBasedValue.lookup(List.of(0f, 1f, 2f, 2f, 3f), LevelBasedValue.constant(0))))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().effects(new MobEffectsPredicate.Builder().and(MobEffects.POISON))))
                        .build()
        );
        context.register(
                REPAIR,
                new LivingUpgrade.Builder()
                        .level(10, 25) // TODO with repairing salve this could easily be 100xp needed (or called redundant tbh)
                        .withEffect(LivingEffectComponents.TICK.get(), new CooldownEffect(REPAIR.identifier()))
                        .withEffect(LivingEffectComponents.TICK.get(), new ResetCooldownEffect(REPAIR.identifier(), LevelBasedValue.constant(100), Optional.of(new RandomArmourDamageEffect(LevelBasedValue.constant(-2)))), cooldownCondition(REPAIR))
                        .build()
        );
        context.register(
                SELF_SACRIFICE,
                new LivingUpgrade.Builder()
                        .level(30, 7)
                        .level(200, 13)
                        .level(400, 22)
                        .level(700, 40)
                        .level(1100, 65)
                        .level(1500, 90)
                        .level(2000, 130)
                        .level(2800, 180)
                        .level(3600, 250)
                        .level(5000, 350)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(SELF_SACRIFICE.identifier(), NVAttributes.SELF_SACRIFICE_MULTIPLIER.getDelegate(), Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(0.15f, 0.3f, 0.45f, 0.6f, 0.75f, 0.9f, 1.05f, 1.2f, 1.35f, 1.5f), LevelBasedValue.constant(0f))))
                        .build()
        );
        context.register(
                SPEED,
                new LivingUpgrade.Builder()
                        .level(200, 3)
                        .level(1000, 7)
                        .level(2000, 13)
                        .level(4000, 26)
                        .level(7000, 42)
                        .level(15000, 60)
                        .level(25000, 90)
                        .level(35000, 130)
                        .level(50000, 180)
                        .level(70000, 250)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(SPEED.identifier(), Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, LevelBasedValue.lookup(List.of(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.9f, 1.1f, 1.3f, 1.5f), LevelBasedValue.constant(0f))))
                        .withEffect(LivingEffectComponents.TICK.get(), new AddMobEffect(MobEffects.SPEED, LevelBasedValue.lookup(List.of(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 2f), LevelBasedValue.constant(0)), LevelBasedValue.lookup(List.of(0f, 0f, 0f, 0f, 0f, 20f, 60f, 60f, 100f, 200f), LevelBasedValue.constant(0))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setSprinting(true))))
                        .build()
        );
        context.register(
                SPRINT_ATTACK,
                new LivingUpgrade.Builder()
                        .level(200, 3)
                        .level(400, 7)
                        .level(1300, 15)
                        .level(2500, 25)
                        .level(3800, 40)
                        .withEffect(LivingEffectComponents.DEALING_DAMAGE.get(), new MultiplyIncreaseValue(LevelBasedValue.lookup(List.of(0.5F, 0.75F, 1F, 1.25F, 1.5F), LevelBasedValue.constant(1))), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setSprinting(true))))
                        .withEffect(LivingEffectComponents.KNOCKBACK.get(), new AddValue(LevelBasedValue.perLevel(1)))
                        .build()
        );

        context.register(
                LUCK, // Added to chests/mines/decent_loot.json loot table
                new LivingUpgrade.Builder()
                        .level(1, 10)
                        .level(2, 25)
                        .level(3, 40)
                        .level(4, 65)
                        .level(5, 90)
                        .withEffect(LivingEffectComponents.ATTRIBUTES.get(), new AttributeEffect(LUCK.identifier(), Attributes.LUCK, Operation.ADD_VALUE, LevelBasedValue.lookup(List.of(2f, 4f, 6f, 8f, 10f), LevelBasedValue.constant(0f))))
                        .build()
        );

        HolderGetter<LivingUpgrade> lookup = context.lookup(NVRegistries.Keys.LIVING_UPGRADES);

        context.register(
                exp(ARROW_PROTECT),
                new LivingUpgrade.Builder()
                        .withEffect(LivingEffectComponents.DAMAGE_TAKEN_EXP.get(), new ValueBasedExp(lookup.getOrThrow(ARROW_PROTECT), ValueBasedExp.THIS_ENTITY), arrowDamage)
                        .build()
        );
        context.register(
                exp(PHYSICAL_PROTECT),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.DAMAGE_TAKEN_EXP.get(), new ValueBasedExp(lookup.getOrThrow(PHYSICAL_PROTECT), ValueBasedExp.THIS_ENTITY), physicalDamage)
                        .build()
        );
        context.register(
                exp(FALL_PROTECT),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.DAMAGE_TAKEN_EXP.get(), new ValueBasedExp(lookup.getOrThrow(FALL_PROTECT), ValueBasedExp.THIS_ENTITY), fallDamage)
                        .build()
        );
        context.register(
                exp(DIGGING),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.BREAK_BLOCK.get(), new EntityBasedExp(lookup.getOrThrow(DIGGING)))
                        .build()
        );
        context.register(
                exp(EXPERIENCED),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.EXP_PICKUP.get(), new ValueBasedExp(lookup.getOrThrow(EXPERIENCED), ValueBasedExp.THIS_ENTITY))
                        .build()
        );
        context.register(
                exp(FIRE_RESIST),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new EntityBasedExp(lookup.getOrThrow(FIRE_RESIST)), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setOnFire(true))))
                        .build()
        );
        context.register(
                exp(HEALTH),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.HEALING.get(), new ValueBasedExp(lookup.getOrThrow(HEALTH), ValueBasedExp.THIS_ENTITY))
                        .build()
        );
        context.register(
                exp(JUMP),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new DistanceExpGain(lookup.getOrThrow(JUMP), DistanceExpGain.Movement.VERTICAL), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setIsFlying(false).setOnGround(false))))
                        .build()
        );
        context.register(
                exp(KNOCKBACK_RESIST),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new EatingExpEffect(lookup.getOrThrow(KNOCKBACK_RESIST)))
                        .build()
        );
        context.register(
                exp(MELEE_DAMAGE),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.DAMAGE_DEALT_EXP.get(), new ValueBasedExp(lookup.getOrThrow(MELEE_DAMAGE), ValueBasedExp.ATTACKER))
                        .build()
        );
        context.register(
                exp(POISON_RESIST),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new EntityBasedExp(lookup.getOrThrow(POISON_RESIST)), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().effects(new MobEffectsPredicate.Builder().and(MobEffects.POISON))))
                        .build()
        );
        context.register(
                exp(REPAIR),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new ItemDamageBasedExpGain(lookup.getOrThrow(REPAIR)))
                        .build()
        );
        context.register(
                exp(SELF_SACRIFICE),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.DAMAGE_TAKEN_EXP.get(), new ValueBasedExp(lookup.getOrThrow(SELF_SACRIFICE), ValueBasedExp.THIS_ENTITY), DamageSourceCondition.hasDamageSource(new DamageSourcePredicate.Builder().tag(TagPredicate.is(NVTags.DamageTypes.SELF_SACRIFICE))))
                        .build()
        );
        context.register(
                exp(SPEED),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.TICK.get(), new DistanceExpGain(lookup.getOrThrow(SPEED), DistanceExpGain.Movement.HORIZONTAL), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setOnGround(true))))
                        .build()
        );
        context.register(
                exp(SPRINT_ATTACK),
                new LivingUpgrade.Builder()
                        .level(1, 0)
                        .withEffect(LivingEffectComponents.DAMAGE_DEALT_EXP.get(), new ValueBasedExp(lookup.getOrThrow(SPRINT_ATTACK), ValueBasedExp.ATTACKER), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, new EntityPredicate.Builder().flags(new EntityFlagsPredicate.Builder().setSprinting(true))))
                        .build()
        );
    }

    private static List<ResourceKey<LivingUpgrade>> expList = new ArrayList<>();
    private static ResourceKey<LivingUpgrade> exp(ResourceKey<LivingUpgrade> key) {
        ResourceKey<LivingUpgrade> xpKey = ResourceKey.create(key.registryKey(), key.identifier().withPrefix("exp/"));
        expList.add(xpKey);
        return xpKey;
    }

    // Ordered alphabetically by English translation
    private static final List<ResourceKey<LivingUpgrade>> downgrades = List.of(
            BATTLE_HUNGRY,    // Battle Hungry
            SWIM_DECREASE,    // Concrete Shoes
            CRIPPLED_ARM,     // Crippled Arm
            MELEE_DECREASE,   // Dulled Blade
            DIG_SLOWDOWN,     // Leadened Pick
            SPEED_DECREASE,   // Limp Leg
            QUENCHED,         // Quenched
            SLOW_HEAL,        // Slow Heal
            STORM_TROOPER     // Storm Trooper
    );
    private static final List<ResourceKey<LivingUpgrade>> upgrades = List.of(
            KNOCKBACK_RESIST,  // Body Builder
            SPRINT_ATTACK,     // Charging Strike
            DIGGING,           // Dwarven Might
            ELYTRA,            // Elytra
            EXPERIENCED,       // Experienced
            MELEE_DAMAGE,      // Fierce Strike
            NETHERITE_PROTECT, // Forgotten
            FIRE_RESIST,       // Gift of Ignis
            GILDED,            // Gilded
            HEALTH,            // Healthy
            ARROW_PROTECT,     // Pin Cushion
            POISON_RESIST,     // Poison Resistance
            SPEED,             // Quick Feet
            REPAIR,            // Repair
            LUCK,              // Skilled
            FALL_PROTECT,      // Soft Fall
            JUMP,              // Strong Legs
            PHYSICAL_PROTECT,  // Tough
            SELF_SACRIFICE     // Tough Palms
    );

    public static void tags(Function<TagKey<LivingUpgrade>, net.minecraft.data.tags.TagAppender<net.minecraft.resources.ResourceKey<LivingUpgrade>, LivingUpgrade>> adder) {
        adder.apply(NVTags.Living.TRAINERS)
                .addAll(expList);

        adder.apply(NVTags.Living.LIVING_START)
                .addTag(NVTags.Living.TRAINERS);

        adder.apply(NVTags.Living.IS_DOWNGRADE)
                .addAll(downgrades);

        adder.apply(NVTags.Living.IS_SCRAPPABLE)
                        .addAll(upgrades);

        adder.apply(NVTags.Living.TOOLTIP_ORDER)
                .addAll(upgrades)
                .addAll(downgrades);

        adder.apply(NVTags.Living.TOOLTIP_HIDE)
                .addTag(NVTags.Living.TRAINERS);
    }

    public static void translations(BiConsumer<String, String> translator) {
        addUpgrade(BATTLE_HUNGRY.identifier(), "Battle Hungry", translator);
        addUpgrade(CRIPPLED_ARM.identifier(), "Crippled Arm", translator);
        addUpgrade(DIG_SLOWDOWN.identifier(), "Leadened Pick", translator);
        addUpgrade(MELEE_DECREASE.identifier(), "Dulled Blade", translator);
        addUpgrade(QUENCHED.identifier(), "Quenched", translator);
        addUpgrade(SLOW_HEAL.identifier(), "Slow Heal", translator);
        addUpgrade(SPEED_DECREASE.identifier(), "Limp Leg", translator);
        addUpgrade(STORM_TROOPER.identifier(), "Storm Trooper", translator);
        addUpgrade(SWIM_DECREASE.identifier(), "Concrete Shoes", translator);

        addUpgrade(ARROW_PROTECT.identifier(), "Pin Cushion", translator);
        addUpgrade(DIGGING.identifier(), "Dwarven Might", translator);
        addUpgrade(ELYTRA.identifier(), "Elytra", translator);
        addUpgrade(EXPERIENCED.identifier(), "Experienced", translator);
        addUpgrade(FALL_PROTECT.identifier(), "Soft Fall", translator);
        addUpgrade(FIRE_RESIST.identifier(), "Gift of Ignis", translator);
        addUpgrade(GILDED.identifier(), "Gilded", translator);
        addUpgrade(HEALTH.identifier(), "Healthy", translator);
        addUpgrade(JUMP.identifier(), "Strong Legs", translator);
        addUpgrade(KNOCKBACK_RESIST.identifier(), "Body Builder", translator);
        addUpgrade(LUCK.identifier(), "Skilled", translator);
        addUpgrade(MELEE_DAMAGE.identifier(), "Fierce Strike", translator);
        addUpgrade(NETHERITE_PROTECT.identifier(), "Forgotten", translator);
        addUpgrade(PHYSICAL_PROTECT.identifier(), "Tough", translator);
        addUpgrade(POISON_RESIST.identifier(), "Poison Resistance", translator);
        addUpgrade(REPAIR.identifier(), "Repair", translator);
        addUpgrade(SELF_SACRIFICE.identifier(), "Tough Palms", translator);
        addUpgrade(SPEED.identifier(), "Quick Feet", translator);
        addUpgrade(SPRINT_ATTACK.identifier(), "Charging Strike", translator);
        addUpgrade(CURIOS_SOCKET.identifier(), "Curios Sockets", translator);
    }

    private static void addUpgrade(Identifier key, String translated, BiConsumer<String, String> translator) {
        translator.accept("living_upgrade.%s.%s".formatted(key.getNamespace(), key.getPath()), translated);
        translator.accept("item.%s.upgrade_tome.%s".formatted(key.getNamespace(), key.getPath()), "Upgrade Tome (%s)".formatted(translated));
    }

    private static LootItemCondition.Builder cooldownCondition(ResourceKey<LivingUpgrade> key) {
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().nbt(new NbtPredicate(
                getCooldownTag(key.identifier())
        )));
    }

    private static CompoundTag getCooldownTag(Identifier id) {
        DataResult<Tag> res = Codec.unboundedMap(Identifier.CODEC, Codec.DOUBLE).encodeStart(NbtOps.INSTANCE, Map.of(id, 0d));
        Tag resTag = res.getOrThrow();
        CompoundTag attachmentTag = new CompoundTag();
        attachmentTag.put(NVDataAttachments.LIVING_ADDITIONAL.getId().toString(), resTag);
        CompoundTag playerTag = new CompoundTag();
        playerTag.put(AttachmentHolder.ATTACHMENTS_NBT_KEY, attachmentTag);
        return playerTag;
    }

    private static ResourceKey<LivingUpgrade> key(String path) {
        return ResourceKey.create(NVRegistries.Keys.LIVING_UPGRADES, Identifier.fromNamespaceAndPath(NeoVitae.MODID, path));
    }
}
