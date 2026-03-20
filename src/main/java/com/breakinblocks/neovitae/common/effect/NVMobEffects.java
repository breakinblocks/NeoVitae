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

public class NVMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, NeoVitae.MODID);

    public static final DeferredHolder<MobEffect, SoulSnareEffect> SOUL_SNARE =
            MOB_EFFECTS.register("soulsnare", () -> new SoulSnareEffect(MobEffectCategory.NEUTRAL, 0xFFFFFF));

    public static final DeferredHolder<MobEffect, FireFuseEffect> FIRE_FUSE =
            MOB_EFFECTS.register("firefuse", () -> new FireFuseEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    public static final DeferredHolder<MobEffect, SoulFrayEffect> SOUL_FRAY =
            MOB_EFFECTS.register("soulfray", () -> new SoulFrayEffect(MobEffectCategory.HARMFUL, 0xC0C0C0));

    public static final DeferredHolder<MobEffect, PlantLeechEffect> PLANT_LEECH =
            MOB_EFFECTS.register("plantleech", () -> new PlantLeechEffect(MobEffectCategory.HARMFUL, 0x00FF00));

    public static final DeferredHolder<MobEffect, SacrificialLambEffect> SACRIFICIAL_LAMB =
            MOB_EFFECTS.register("sacrificiallamb", () -> new SacrificialLambEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));

    public static final DeferredHolder<MobEffect, PassivityEffect> PASSIVITY =
            MOB_EFFECTS.register("passivity", () -> new PassivityEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));

    public static final DeferredHolder<MobEffect, FlightEffect> FLIGHT =
            MOB_EFFECTS.register("flight", () -> new FlightEffect(MobEffectCategory.BENEFICIAL, 0x23DDE1));

    public static final DeferredHolder<MobEffect, NeoVitaeEffect> SPECTRAL_SIGHT =
            MOB_EFFECTS.register("spectral_sight", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x2FB813));

    public static final DeferredHolder<MobEffect, NeoVitaeEffect> GRAVITY =
            MOB_EFFECTS.register("gravity", () -> {
                NeoVitaeEffect effect = new NeoVitaeEffect(MobEffectCategory.HARMFUL, 0x800080);
                effect.addAttributeModifier(Attributes.GRAVITY,
                        ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "gravity_effect"),
                        0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                return effect;
            });

    public static final DeferredHolder<MobEffect, HeavyHeartEffect> HEAVY_HEART =
            MOB_EFFECTS.register("heavy_heart", () -> new HeavyHeartEffect(MobEffectCategory.HARMFUL, 0x8B0000));

    public static final DeferredHolder<MobEffect, GroundedEffect> GROUNDED =
            MOB_EFFECTS.register("grounded", () -> new GroundedEffect(MobEffectCategory.HARMFUL, 0xBA855B));

    public static final DeferredHolder<MobEffect, SuspendedEffect> SUSPENDED =
            MOB_EFFECTS.register("suspended", () -> new SuspendedEffect(MobEffectCategory.NEUTRAL, 0x23DDE1));

    public static final DeferredHolder<MobEffect, NeoVitaeEffect> BOUNCE =
            MOB_EFFECTS.register("bounce", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x57FF2E));

    public static final DeferredHolder<MobEffect, SoftFallEffect> SOFT_FALL =
            MOB_EFFECTS.register("soft_fall", () -> new SoftFallEffect(MobEffectCategory.BENEFICIAL, 0x4AEDD9));

    public static final DeferredHolder<MobEffect, NeoVitaeEffect> OBSIDIAN_CLOAK =
            MOB_EFFECTS.register("obsidian_cloak", () -> new NeoVitaeEffect(MobEffectCategory.BENEFICIAL, 0x3C1A8D));

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

    public static MobEffect getEffect(ResourceLocation rl) {
        return MOB_EFFECTS.getRegistry().get().get(rl);
    }
}
