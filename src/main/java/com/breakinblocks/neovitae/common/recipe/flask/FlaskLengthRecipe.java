package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Recipe that increases the duration modifier of a specific effect in a flask.
 */
public class FlaskLengthRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskLengthRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(FlaskLengthRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskLengthRecipe::getTargetEffect),
            Codec.DOUBLE.fieldOf("lengthDurationMod").forGetter(FlaskLengthRecipe::getLengthDurationMod),
            Codec.INT.fieldOf("syphon").forGetter(FlaskLengthRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskLengthRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskLengthRecipe::getMinimumTier)
    ).apply(instance, FlaskLengthRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> MOB_EFFECT_CODEC =
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key());

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskLengthRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskLengthRecipe::getInput,
            MOB_EFFECT_CODEC, FlaskLengthRecipe::getTargetEffect,
            ByteBufCodecs.DOUBLE, FlaskLengthRecipe::getLengthDurationMod,
            ByteBufCodecs.INT, FlaskLengthRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskLengthRecipe::getTicks,
            ByteBufCodecs.INT, FlaskLengthRecipe::getMinimumTier,
            FlaskLengthRecipe::new
    );

    private final Holder<MobEffect> targetEffect;
    private final double lengthDurationMod;

    public FlaskLengthRecipe(List<Ingredient> input, Holder<MobEffect> targetEffect, double lengthDurationMod, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.targetEffect = targetEffect;
        this.lengthDurationMod = lengthDurationMod;
    }

    public Holder<MobEffect> getTargetEffect() {
        return targetEffect;
    }

    public double getLengthDurationMod() {
        return lengthDurationMod;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only increase length if the effect exists and current modifier is less than target
        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(targetEffect)) {
                return holder.lengthDurationMod() < lengthDurationMod;
            }
        }
        return false;
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        for (int i = 0; i < flaskEffects.size(); i++) {
            if (flaskEffects.get(i).matches(targetEffect)) {
                return i + 1;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();
        List<EffectHolder> newEffects = new ArrayList<>();

        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(targetEffect)) {
                newEffects.add(holder.withLengthDurationMod(lengthDurationMod));
            } else {
                newEffects.add(holder);
            }
        }

        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(newEffects));
        return copyStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        List<EffectHolder> effects = new ArrayList<>();
        effects.add(EffectHolder.create(targetEffect, 3600, 0));
        return effects;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FlaskInput>> getSerializer() {
        return NVRecipes.FLASK_LENGTH_SERIALIZER.get();
    }
}
