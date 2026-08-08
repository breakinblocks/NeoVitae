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

/**
 * Recipe that adds a new potion effect to a flask.
 */
public class FlaskEffectRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskEffectRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NVRecipeCodecs.INGREDIENT.listOf().fieldOf("input").forGetter(FlaskEffectRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskEffectRecipe::getOutputEffect),
            Codec.INT.fieldOf("baseDuration").forGetter(FlaskEffectRecipe::getBaseDuration),
            Codec.INT.fieldOf("syphon").forGetter(FlaskEffectRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskEffectRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskEffectRecipe::getMinimumTier)
    ).apply(instance, FlaskEffectRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> MOB_EFFECT_CODEC =
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key());

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskEffectRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskEffectRecipe::getInput,
            MOB_EFFECT_CODEC, FlaskEffectRecipe::getOutputEffect,
            ByteBufCodecs.INT, FlaskEffectRecipe::getBaseDuration,
            ByteBufCodecs.INT, FlaskEffectRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskEffectRecipe::getTicks,
            ByteBufCodecs.INT, FlaskEffectRecipe::getMinimumTier,
            FlaskEffectRecipe::new
    );

    private final Holder<MobEffect> outputEffect;
    private final int baseDuration;

    public FlaskEffectRecipe(List<Ingredient> input, Holder<MobEffect> outputEffect, int baseDuration, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.outputEffect = outputEffect;
        this.baseDuration = baseDuration;
    }

    public Holder<MobEffect> getOutputEffect() {
        return outputEffect;
    }

    public int getBaseDuration() {
        return baseDuration;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can't add an effect that's already present
        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(outputEffect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();
        List<EffectHolder> newEffects = new ArrayList<>(flaskEffects);
        newEffects.add(EffectHolder.create(outputEffect, baseDuration, 0));
        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(newEffects));
        return copyStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        // Return empty list since this recipe adds an effect to an empty flask
        return new ArrayList<>();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLASK_EFFECT_SERIALIZER.get();
    }
}
