package com.breakinblocks.neovitae.common.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Blood Magic mob effects registration.
 */
public class BMMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, NeoVitae.MODID);

    // ==================== Combat Effects ====================

    // Soul Snare - marks entities for soul capture on death
    public static final DeferredHolder<MobEffect, SoulSnareEffect> SOUL_SNARE =
            MOB_EFFECTS.register("soulsnare", () -> new SoulSnareEffect(MobEffectCategory.NEUTRAL, 0xFFFFFF));

    // Fire Fuse - explodes when duration expires
    public static final DeferredHolder<MobEffect, FireFuseEffect> FIRE_FUSE =
            MOB_EFFECTS.register("firefuse", () -> new FireFuseEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    // Soul Fray - prevents ceremonial sacrifice for a duration after performing one
    public static final DeferredHolder<MobEffect, SoulFrayEffect> SOUL_FRAY =
            MOB_EFFECTS.register("soulfray", () -> new SoulFrayEffect(MobEffectCategory.HARMFUL, 0xC0C0C0));

    // ==================== Nature Effects ====================

    // Plant Leech - damages entity to grow nearby plants
    public static final DeferredHolder<MobEffect, PlantLeechEffect> PLANT_LEECH =
            MOB_EFFECTS.register("plantleech", () -> new PlantLeechEffect(MobEffectCategory.HARMFUL, 0x00FF00));

    // ==================== AI Modification Effects ====================

    // Sacrificial Lamb - makes passive mobs attack monsters and explode
    public static final DeferredHolder<MobEffect, SacrificialLambEffect> SACRIFICIAL_LAMB =
            MOB_EFFECTS.register("sacrificallamb", () -> new SacrificialLambEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));

    // Passivity - makes hostile mobs non-aggressive
    public static final DeferredHolder<MobEffect, PassivityEffect> PASSIVITY =
            MOB_EFFECTS.register("passivity", () -> new PassivityEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));

    // ==================== Movement Effects ====================

    // Flight - grants creative flight
    public static final DeferredHolder<MobEffect, FlightEffect> FLIGHT =
            MOB_EFFECTS.register("flight", () -> new FlightEffect(MobEffectCategory.BENEFICIAL, 0x23DDE1));

    // Spectral Sight - enhanced vision
    public static final DeferredHolder<MobEffect, NeoVitaeEffect> SPECTRAL_SIGHT =
            MOB_EFFECTS.register("spectral_sight", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x2FB813));

    // Gravity - increased gravity (falling speed)
    public static final DeferredHolder<MobEffect, NeoVitaeEffect> GRAVITY =
            MOB_EFFECTS.register("gravity", () -> {
                NeoVitaeEffect effect = new NeoVitaeEffect(MobEffectCategory.HARMFUL, 0x800080);
                effect.addAttributeModifier(Attributes.GRAVITY,
                        ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "gravity_effect"),
                        0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                return effect;
            });

    // Heavy Heart - drags the target down, prevents flight
    public static final DeferredHolder<MobEffect, HeavyHeartEffect> HEAVY_HEART =
            MOB_EFFECTS.register("heavy_heart", () -> new HeavyHeartEffect(MobEffectCategory.HARMFUL, 0x8B0000));

    // Grounded - prevents jumping
    public static final DeferredHolder<MobEffect, GroundedEffect> GROUNDED =
            MOB_EFFECTS.register("grounded", () -> new GroundedEffect(MobEffectCategory.HARMFUL, 0xBA855B));

    // Suspended - disables gravity, makes entity float
    public static final DeferredHolder<MobEffect, SuspendedEffect> SUSPENDED =
            MOB_EFFECTS.register("suspended", () -> new SuspendedEffect(MobEffectCategory.NEUTRAL, 0x23DDE1));

    // Bounce - allows bouncing off the ground
    public static final DeferredHolder<MobEffect, NeoVitaeEffect> BOUNCE =
            MOB_EFFECTS.register("bounce", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x57FF2E));

    // Soft Fall - negates fall damage
    public static final DeferredHolder<MobEffect, SoftFallEffect> SOFT_FALL =
            MOB_EFFECTS.register("soft_fall", () -> new SoftFallEffect(MobEffectCategory.BENEFICIAL, 0x4AEDD9));

    // ==================== Armor Effects ====================

    // Obsidian Cloak - protective effect
    public static final DeferredHolder<MobEffect, NeoVitaeEffect> OBSIDIAN_CLOAK =
            MOB_EFFECTS.register("obsidian_cloak", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x3C1A8D));

    // Hard Cloak - armor toughness bonus
    public static final DeferredHolder<MobEffect, NeoVitaeEffect> HARD_CLOAK =
            MOB_EFFECTS.register("hard_cloak", () -> {
                NeoVitaeEffect effect = new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x3C1A8D);
                effect.addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                        ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hard_cloak_effect"),
                        3, AttributeModifier.Operation.ADD_VALUE);
                return effect;
            });

    public static void register(IEventBus modBus) {
        MOB_EFFECTS.register(modBus);
    }

    /**
     * Get an effect by resource location.
     */
    public static MobEffect getEffect(ResourceLocation rl) {
        return MOB_EFFECTS.getRegistry().get().get(rl);
    }
}
