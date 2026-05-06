package com.breakinblocks.neovitae.common.sentient;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.effects.*;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import java.util.List;

public class SentientEffectComponents {
    public static final DeferredRegister.DataComponents LIVING_EFFECT_COMPONENTS = DeferredRegister.createDataComponents(NVRegistries.Keys.SENTIENT_EFFECT_COMPONENTS, NeoVitae.MODID);
    public static final Codec<DataComponentType<?>> COMPONENT_CODEC = Codec.lazyInitialized(() -> LIVING_EFFECT_COMPONENTS.getRegistry().get().byNameCodec());
    public static final Codec<DataComponentMap> CODEC = DataComponentMap.makeCodec(COMPONENT_CODEC);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AttributeEffect>>> ATTRIBUTES = LIVING_EFFECT_COMPONENTS.registerComponentType("attributes", builder -> builder.persistent(AttributeEffect.CODEC.codec().listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> TAKING_DAMAGE = LIVING_EFFECT_COMPONENTS.registerComponentType("taking_damage", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.DAMAGE_BASED).listOf()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> DEALING_DAMAGE = LIVING_EFFECT_COMPONENTS.registerComponentType("dealing_damage", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.DAMAGE_BASED).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> KNOCKBACK = LIVING_EFFECT_COMPONENTS.registerComponentType("knockback", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.DAMAGE_BASED).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> DAMAGE_TAKEN_EXP = LIVING_EFFECT_COMPONENTS.registerComponentType("damage_taken_exp", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.DAMAGE_BASED).listOf()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> DAMAGE_DEALT_EXP = LIVING_EFFECT_COMPONENTS.registerComponentType("damage_dealt_exp", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.DAMAGE_BASED).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientEntityEffect>>>> BREAK_BLOCK = LIVING_EFFECT_COMPONENTS.registerComponentType("break_block", builder -> builder.persistent(ConditionalEffect.codec(SentientEntityEffect.CODEC, SentientContextParamSets.BREAK_BLOCK).listOf()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientEntityEffect>>>> TICK = LIVING_EFFECT_COMPONENTS.registerComponentType("tick", builder -> builder.persistent(ConditionalEffect.codec(SentientEntityEffect.CODEC, SentientContextParamSets.TICK).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientEntityEffect>>>> PROJECTILE_SHOT = LIVING_EFFECT_COMPONENTS.registerComponentType("eating", builder -> builder.persistent(ConditionalEffect.codec(SentientEntityEffect.CODEC, SentientContextParamSets.PROJECTILE).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> EXP_PICKUP = LIVING_EFFECT_COMPONENTS.registerComponentType("exp_pickup", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.TICK).listOf()));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<SentientValueEffect>>>> HEALING = LIVING_EFFECT_COMPONENTS.registerComponentType("healing", builder -> builder.persistent(ConditionalEffect.codec(SentientValueEffect.CODEC, SentientContextParamSets.TICK).listOf()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> GILDED = LIVING_EFFECT_COMPONENTS.registerComponentType("gilded", builder -> builder.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> ELYTRA = LIVING_EFFECT_COMPONENTS.registerComponentType("elytra", builder -> builder.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> QUENCHED = LIVING_EFFECT_COMPONENTS.registerComponentType("quenched", builder -> builder.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> CRIPPLED_ARM = LIVING_EFFECT_COMPONENTS.registerComponentType("crippled_arm", builder -> builder.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WALK_ON_POWDERED_SNOW = LIVING_EFFECT_COMPONENTS.registerComponentType("walk_on_powdered_snow", builder -> builder.persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_ENDER_MASK = LIVING_EFFECT_COMPONENTS.registerComponentType("is_ender_mask", builder -> builder.persistent(Unit.CODEC));

    public static void register(IEventBus modBus) {
        LIVING_EFFECT_COMPONENTS.makeRegistry(builder -> {});
        LIVING_EFFECT_COMPONENTS.register(modBus);
    }
}
