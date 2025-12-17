package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

/**
 * Serializer for flask item transform recipes (converting between flask types).
 */
public class FlaskItemTransformSerializer implements RecipeSerializer<FlaskItemTransformRecipe> {

    public static final MapCodec<FlaskItemTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskItemTransformRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(FlaskItemTransformRecipe::getOutputItem),
            Codec.INT.fieldOf("syphon").forGetter(FlaskItemTransformRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskItemTransformRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskItemTransformRecipe::getMinimumTier)
    ).apply(instance, FlaskItemTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskItemTransformRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskItemTransformRecipe::getInput,
            ItemStack.STREAM_CODEC, FlaskItemTransformRecipe::getOutputItem,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getTicks,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getMinimumTier,
            FlaskItemTransformRecipe::new
    );

    @Override
    public MapCodec<FlaskItemTransformRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskItemTransformRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
