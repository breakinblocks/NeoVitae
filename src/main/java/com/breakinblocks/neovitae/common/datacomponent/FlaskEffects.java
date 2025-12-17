package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data component for storing alchemy flask effects.
 * Holds a list of EffectHolders with their duration modifiers.
 */
public record FlaskEffects(List<EffectHolder> effects) {

    public static final FlaskEffects EMPTY = new FlaskEffects(List.of());

    public static final Codec<FlaskEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(EffectHolder.CODEC).fieldOf("effects").forGetter(FlaskEffects::effects)
    ).apply(instance, FlaskEffects::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskEffects> STREAM_CODEC = StreamCodec.composite(
            EffectHolder.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FlaskEffects::effects,
            FlaskEffects::new
    );

    /**
     * Creates a FlaskEffects with a single effect.
     */
    public static FlaskEffects single(EffectHolder holder) {
        return new FlaskEffects(List.of(holder));
    }

    /**
     * Creates a FlaskEffects from multiple holders.
     */
    public static FlaskEffects of(EffectHolder... holders) {
        return new FlaskEffects(List.of(holders));
    }

    public boolean isEmpty() {
        return effects.isEmpty();
    }

    public int size() {
        return effects.size();
    }

    /**
     * Checks if this flask contains the given effect.
     */
    public boolean hasEffect(Holder<MobEffect> effect) {
        return effects.stream().anyMatch(h -> h.matches(effect));
    }

    /**
     * Gets the effect holder for a specific effect, if present.
     */
    public Optional<EffectHolder> getHolder(Holder<MobEffect> effect) {
        return effects.stream().filter(h -> h.matches(effect)).findFirst();
    }

    /**
     * Gets the index of an effect in the list, or -1 if not found.
     */
    public int indexOf(Holder<MobEffect> effect) {
        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).matches(effect)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a new FlaskEffects with an additional effect.
     */
    public FlaskEffects withEffect(EffectHolder holder) {
        List<EffectHolder> newList = new ArrayList<>(effects);
        newList.add(holder);
        return new FlaskEffects(newList);
    }

    /**
     * Returns a new FlaskEffects with the effect at the given index replaced.
     */
    public FlaskEffects withReplacedEffect(int index, EffectHolder holder) {
        if (index < 0 || index >= effects.size()) {
            return this;
        }
        List<EffectHolder> newList = new ArrayList<>(effects);
        newList.set(index, holder);
        return new FlaskEffects(newList);
    }

    /**
     * Returns a new FlaskEffects with a specific effect updated.
     */
    public FlaskEffects withUpdatedEffect(Holder<MobEffect> effect, EffectHolder newHolder) {
        List<EffectHolder> newList = new ArrayList<>();
        for (EffectHolder h : effects) {
            if (h.matches(effect)) {
                newList.add(newHolder);
            } else {
                newList.add(h);
            }
        }
        return new FlaskEffects(newList);
    }

    /**
     * Returns a new FlaskEffects with the effect at the given index removed.
     */
    public FlaskEffects withRemovedEffect(int index) {
        if (index < 0 || index >= effects.size()) {
            return this;
        }
        List<EffectHolder> newList = new ArrayList<>(effects);
        newList.remove(index);
        return new FlaskEffects(newList);
    }

    /**
     * Returns a new FlaskEffects with a specific effect removed.
     */
    public FlaskEffects withRemovedEffect(Holder<MobEffect> effect) {
        List<EffectHolder> newList = new ArrayList<>();
        for (EffectHolder h : effects) {
            if (!h.matches(effect)) {
                newList.add(h);
            }
        }
        return new FlaskEffects(newList);
    }

    /**
     * Returns a new FlaskEffects with effects cycled (first becomes last).
     */
    public FlaskEffects cycled(int times) {
        if (effects.size() < 2) {
            return this;
        }
        List<EffectHolder> newList = new ArrayList<>(effects);
        for (int i = 0; i < times; i++) {
            EffectHolder first = newList.remove(0);
            newList.add(first);
        }
        return new FlaskEffects(newList);
    }

    /**
     * Converts all effect holders to MobEffectInstances for application.
     */
    public List<MobEffectInstance> toEffectInstances(boolean ambient, boolean showParticles) {
        return effects.stream()
                .map(h -> h.getEffectInstance(ambient, showParticles))
                .toList();
    }

    /**
     * Gets a copy of the effects list for modification.
     */
    public List<EffectHolder> toMutableList() {
        return new ArrayList<>(effects);
    }

    /**
     * Calculates the combined potion color for display purposes.
     */
    public int getColor() {
        if (effects.isEmpty()) {
            return 0x385DC6; // Default water color
        }

        float r = 0, g = 0, b = 0;
        int totalDuration = 0;

        for (EffectHolder holder : effects) {
            int duration = holder.getTotalDuration();
            int color = holder.effect().value().getColor();
            r += (float) duration * (float) (color >> 16 & 255) / 255.0F;
            g += (float) duration * (float) (color >> 8 & 255) / 255.0F;
            b += (float) duration * (float) (color & 255) / 255.0F;
            totalDuration += duration;
        }

        if (totalDuration == 0) {
            return 0;
        }

        r = r / (float) totalDuration * 255.0F;
        g = g / (float) totalDuration * 255.0F;
        b = b / (float) totalDuration * 255.0F;

        return (int) r << 16 | (int) g << 8 | (int) b;
    }
}
