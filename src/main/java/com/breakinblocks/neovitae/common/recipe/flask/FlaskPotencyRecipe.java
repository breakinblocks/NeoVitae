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
import com.breakinblocks.neovitae.common.recipe.NVRecipeCodecs;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Recipe that increases the potency (amplifier) of a specific effect in a flask.
 * Also adjusts the amplifier duration modifier.
 */
public class FlaskPotencyRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskPotencyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NVRecipeCodecs.INGREDIENT.listOf().fieldOf("input").forGetter(FlaskPotencyRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskPotencyRecipe::getTargetEffect),
            Codec.INT.fieldOf("amplifier").forGetter(FlaskPotencyRecipe::getAmplifier),
            Codec.DOUBLE.fieldOf("ampDurationMod").forGetter(FlaskPotencyRecipe::getAmpDurationMod),
            Codec.INT.fieldOf("syphon").forGetter(FlaskPotencyRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskPotencyRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskPotencyRecipe::getMinimumTier),
            Codec.INT.optionalFieldOf("exampleBaseDuration", 3600).forGetter(FlaskPotencyRecipe::getExampleBaseDuration)
    ).apply(instance, FlaskPotencyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskPotencyRecipe> STREAM_CODEC = StreamCodec.of(
            FlaskPotencyRecipe::toNetwork,
            FlaskPotencyRecipe::fromNetwork
    );

    private static FlaskPotencyRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        List<Ingredient> inputs = RecipeSerializerUtils.INGREDIENT_LIST_CODEC.decode(buffer);
        Holder<MobEffect> effect = ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).decode(buffer);
        int amplifier = buffer.readInt();
        double ampDurationMod = buffer.readDouble();
        int syphon = buffer.readInt();
        int ticks = buffer.readInt();
        int minimumTier = buffer.readInt();
        int exampleBaseDuration = buffer.readInt();
        return new FlaskPotencyRecipe(inputs, effect, amplifier, ampDurationMod, syphon, ticks, minimumTier, exampleBaseDuration);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, FlaskPotencyRecipe recipe) {
        RecipeSerializerUtils.INGREDIENT_LIST_CODEC.encode(buffer, recipe.getInput());
        ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).encode(buffer, recipe.getTargetEffect());
        buffer.writeInt(recipe.getAmplifier());
        buffer.writeDouble(recipe.getAmpDurationMod());
        buffer.writeInt(recipe.getSyphon());
        buffer.writeInt(recipe.getTicks());
        buffer.writeInt(recipe.getMinimumTier());
        buffer.writeInt(recipe.getExampleBaseDuration());
    }

    private final Holder<MobEffect> targetEffect;
    private final int amplifier;
    private final double ampDurationMod;
    private final int exampleBaseDuration;

    public FlaskPotencyRecipe(List<Ingredient> input, Holder<MobEffect> targetEffect, int amplifier, double ampDurationMod, int syphon, int ticks, int minimumTier, int exampleBaseDuration) {
        super(input, syphon, ticks, minimumTier);
        this.targetEffect = targetEffect;
        this.amplifier = amplifier;
        this.ampDurationMod = ampDurationMod;
        this.exampleBaseDuration = exampleBaseDuration;
    }

    public int getExampleBaseDuration() {
        return exampleBaseDuration;
    }

    public Holder<MobEffect> getTargetEffect() {
        return targetEffect;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public double getAmpDurationMod() {
        return ampDurationMod;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only increase potency if the effect exists and current amplifier is less than target
        // (or equal amplifier but lower amp duration mod)
        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(targetEffect)) {
                return holder.amplifier() < amplifier ||
                        (holder.amplifier() == amplifier && holder.ampDurationMod() < ampDurationMod);
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
                newEffects.add(holder.withPotency(amplifier, ampDurationMod));
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
    public RecipeSerializer<? extends Recipe<FlaskInput>> getSerializer() {
        return NVRecipes.FLASK_POTENCY_SERIALIZER.get();
    }
}
