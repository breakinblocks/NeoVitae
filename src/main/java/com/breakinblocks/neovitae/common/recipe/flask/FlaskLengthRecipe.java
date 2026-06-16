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

/**
 * Recipe that increases the duration modifier of a specific effect in a flask.
 */
public class FlaskLengthRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskLengthRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskLengthRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskLengthRecipe::getTargetEffect),
            Codec.DOUBLE.fieldOf("lengthDurationMod").forGetter(FlaskLengthRecipe::getLengthDurationMod),
            Codec.INT.fieldOf("syphon").forGetter(FlaskLengthRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskLengthRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskLengthRecipe::getMinimumTier),
            Codec.INT.optionalFieldOf("exampleBaseDuration", 3600).forGetter(FlaskLengthRecipe::getExampleBaseDuration)
    ).apply(instance, FlaskLengthRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> MOB_EFFECT_CODEC =
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key());

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskLengthRecipe> STREAM_CODEC = StreamCodec.of(
            FlaskLengthRecipe::toNetwork,
            FlaskLengthRecipe::fromNetwork
    );

    private static FlaskLengthRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        List<Ingredient> inputs = RecipeSerializerUtils.INGREDIENT_LIST_CODEC.decode(buffer);
        Holder<MobEffect> effect = MOB_EFFECT_CODEC.decode(buffer);
        double lengthDurationMod = buffer.readDouble();
        int syphon = buffer.readInt();
        int ticks = buffer.readInt();
        int minimumTier = buffer.readInt();
        int exampleBaseDuration = buffer.readInt();
        return new FlaskLengthRecipe(inputs, effect, lengthDurationMod, syphon, ticks, minimumTier, exampleBaseDuration);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, FlaskLengthRecipe recipe) {
        RecipeSerializerUtils.INGREDIENT_LIST_CODEC.encode(buffer, recipe.getInput());
        MOB_EFFECT_CODEC.encode(buffer, recipe.getTargetEffect());
        buffer.writeDouble(recipe.getLengthDurationMod());
        buffer.writeInt(recipe.getSyphon());
        buffer.writeInt(recipe.getTicks());
        buffer.writeInt(recipe.getMinimumTier());
        buffer.writeInt(recipe.getExampleBaseDuration());
    }

    private final Holder<MobEffect> targetEffect;
    private final double lengthDurationMod;
    private final int exampleBaseDuration;

    public FlaskLengthRecipe(List<Ingredient> input, Holder<MobEffect> targetEffect, double lengthDurationMod, int syphon, int ticks, int minimumTier, int exampleBaseDuration) {
        super(input, syphon, ticks, minimumTier);
        this.targetEffect = targetEffect;
        this.lengthDurationMod = lengthDurationMod;
        this.exampleBaseDuration = exampleBaseDuration;
    }

    public int getExampleBaseDuration() {
        return exampleBaseDuration;
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
        effects.add(EffectHolder.create(targetEffect, exampleBaseDuration, 0));
        return effects;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLASK_LENGTH_SERIALIZER.get();
    }
}
