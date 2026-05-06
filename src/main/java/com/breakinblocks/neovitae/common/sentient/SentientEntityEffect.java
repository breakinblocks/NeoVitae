package com.breakinblocks.neovitae.common.sentient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.effects.*;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public interface SentientEntityEffect {
    DeferredRegister<MapCodec<? extends SentientEntityEffect>> ENTITY_EFFECT_TYPE = DeferredRegister.create(NVRegistries.Keys.ENTITY_EFFECT_TYPE, NeoVitae.MODID);
    Codec<SentientEntityEffect> CODEC = Codec.lazyInitialized(() -> ENTITY_EFFECT_TYPE
            .getRegistry()
            .get()
            .byNameCodec()
            .dispatch(SentientEntityEffect::codec, Function.identity())
    );

    Supplier<MapCodec<CooldownEffect>> COOLDOWN = ENTITY_EFFECT_TYPE.register("cooldown", () -> CooldownEffect.CODEC);
    Supplier<MapCodec<ResetCooldownEffect>> RESET_COOLDOWN = ENTITY_EFFECT_TYPE.register("reset_cooldown", () -> ResetCooldownEffect.CODEC);
    Supplier<MapCodec<MovementModifier>> MOVEMENT_MODIFIER = ENTITY_EFFECT_TYPE.register("movement_modifier", () -> MovementModifier.CODEC);
    Supplier<MapCodec<AddMobEffect>> ADD_MOB_EFFECT = ENTITY_EFFECT_TYPE.register("add_mob_effect", () -> AddMobEffect.CODEC);
    Supplier<MapCodec<RemoveMobEffect>> REMOVE_MOB_EFFECT = ENTITY_EFFECT_TYPE.register("remove_mob_effect", () -> RemoveMobEffect.CODEC);
    Supplier<MapCodec<RandomArmourDamageEffect>> RANDOM_ARMOUR_DAMAGE = ENTITY_EFFECT_TYPE.register("random_armour_damage", () -> RandomArmourDamageEffect.CODEC);
    Supplier<MapCodec<CauseExhaustionEffect>> EXHAUST = ENTITY_EFFECT_TYPE.register("exhaust", () -> CauseExhaustionEffect.CODEC);

    Supplier<MapCodec<EntityBasedExp>> ENTITY_BASED_EXP = ENTITY_EFFECT_TYPE.register("sentient_exp", () -> EntityBasedExp.CODEC);
    Supplier<MapCodec<DistanceExpGain>> DISTANCE_EXP = ENTITY_EFFECT_TYPE.register("distance_sentient_exp", () -> DistanceExpGain.CODEC);
    Supplier<MapCodec<EatingExpEffect>> EATING_EXP = ENTITY_EFFECT_TYPE.register("eating_sentient_exp", () -> EatingExpEffect.CODEC);
    Supplier<MapCodec<ItemDamageBasedExpGain>> REPAIR_EXP = ENTITY_EFFECT_TYPE.register("repairing_sentient_exp", () -> ItemDamageBasedExpGain.CODEC);

    static void register(IEventBus modBus) {
        ENTITY_EFFECT_TYPE.makeRegistry(builder -> {});
        ENTITY_EFFECT_TYPE.register(modBus);
    }

    void apply(int upgradeLevel, Entity entity);

    MapCodec<? extends SentientEntityEffect> codec();
}
