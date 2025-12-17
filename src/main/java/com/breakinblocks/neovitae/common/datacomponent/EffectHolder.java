package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Holds effect data for alchemy flasks with customizable duration modifiers.
 * Unlike standard potion effects, flask effects can have their duration and potency
 * modified through alchemy table recipes.
 */
public record EffectHolder(
        Holder<MobEffect> effect,
        int baseDuration,
        int amplifier,
        double ampDurationMod,
        double lengthDurationMod
) {
    public static final Codec<EffectHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(EffectHolder::effect),
            Codec.INT.fieldOf("base_duration").forGetter(EffectHolder::baseDuration),
            Codec.INT.fieldOf("amplifier").forGetter(EffectHolder::amplifier),
            Codec.DOUBLE.fieldOf("amp_duration_mod").forGetter(EffectHolder::ampDurationMod),
            Codec.DOUBLE.fieldOf("length_duration_mod").forGetter(EffectHolder::lengthDurationMod)
    ).apply(instance, EffectHolder::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectHolder> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()), EffectHolder::effect,
            ByteBufCodecs.INT, EffectHolder::baseDuration,
            ByteBufCodecs.INT, EffectHolder::amplifier,
            ByteBufCodecs.DOUBLE, EffectHolder::ampDurationMod,
            ByteBufCodecs.DOUBLE, EffectHolder::lengthDurationMod,
            EffectHolder::new
    );

    /**
     * Creates a new EffectHolder with default modifiers (1.0).
     */
    public static EffectHolder create(Holder<MobEffect> effect, int baseDuration, int amplifier) {
        return new EffectHolder(effect, baseDuration, amplifier, 1.0, 1.0);
    }

    /**
     * Creates a MobEffectInstance from this holder with the specified parameters.
     */
    public MobEffectInstance getEffectInstance(boolean ambient, boolean showParticles) {
        return getEffectInstance(1.0, ambient, showParticles);
    }

    /**
     * Creates a MobEffectInstance with an additional duration modifier.
     */
    public MobEffectInstance getEffectInstance(double durationModifier, boolean ambient, boolean showParticles) {
        int duration = (int) (baseDuration * ampDurationMod * lengthDurationMod * durationModifier);
        return new MobEffectInstance(effect, duration, amplifier, ambient, showParticles);
    }

    /**
     * Gets the calculated total duration with all modifiers applied.
     */
    public int getTotalDuration() {
        return (int) (baseDuration * ampDurationMod * lengthDurationMod);
    }

    /**
     * Returns a new EffectHolder with updated base duration.
     */
    public EffectHolder withBaseDuration(int newDuration) {
        return new EffectHolder(effect, newDuration, amplifier, ampDurationMod, lengthDurationMod);
    }

    /**
     * Returns a new EffectHolder with updated amplifier.
     */
    public EffectHolder withAmplifier(int newAmplifier) {
        return new EffectHolder(effect, baseDuration, newAmplifier, ampDurationMod, lengthDurationMod);
    }

    /**
     * Returns a new EffectHolder with updated amplifier duration modifier.
     */
    public EffectHolder withAmpDurationMod(double newMod) {
        return new EffectHolder(effect, baseDuration, amplifier, newMod, lengthDurationMod);
    }

    /**
     * Returns a new EffectHolder with updated length duration modifier.
     */
    public EffectHolder withLengthDurationMod(double newMod) {
        return new EffectHolder(effect, baseDuration, amplifier, ampDurationMod, newMod);
    }

    /**
     * Returns a new EffectHolder with updated amplifier and amp duration modifier.
     */
    public EffectHolder withPotency(int newAmplifier, double newAmpMod) {
        return new EffectHolder(effect, baseDuration, newAmplifier, newAmpMod, lengthDurationMod);
    }

    /**
     * Checks if this holder's effect matches the given effect.
     */
    public boolean matches(Holder<MobEffect> other) {
        return effect.equals(other);
    }
}
