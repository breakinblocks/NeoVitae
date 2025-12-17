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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

/**
 * Serializer for flask effect recipes (adding new effects to flasks).
 */
public class FlaskEffectSerializer implements RecipeSerializer<FlaskEffectRecipe> {

    public static final MapCodec<FlaskEffectRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskEffectRecipe::getInput),
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

    @Override
    public MapCodec<FlaskEffectRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskEffectRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
