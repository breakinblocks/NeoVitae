package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

/**
 * Serializer for flask fill recipes (refilling depleted flasks).
 */
public class FlaskFillSerializer implements RecipeSerializer<FlaskFillRecipe> {

    public static final MapCodec<FlaskFillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskFillRecipe::getInput),
            Codec.INT.fieldOf("max").forGetter(FlaskFillRecipe::getMaxEffects),
            Codec.INT.fieldOf("syphon").forGetter(FlaskFillRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskFillRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(FlaskFillRecipe::getMinimumTier)
    ).apply(instance, FlaskFillRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskFillRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskFillRecipe::getInput,
            ByteBufCodecs.INT, FlaskFillRecipe::getMaxEffects,
            ByteBufCodecs.INT, FlaskFillRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskFillRecipe::getTicks,
            ByteBufCodecs.INT, FlaskFillRecipe::getMinimumTier,
            FlaskFillRecipe::new
    );

    @Override
    public MapCodec<FlaskFillRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskFillRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
