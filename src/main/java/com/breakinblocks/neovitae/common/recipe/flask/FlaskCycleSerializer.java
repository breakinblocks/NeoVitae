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
 * Serializer for flask cycle recipes (reordering effects in flasks).
 */
public class FlaskCycleSerializer implements RecipeSerializer<FlaskCycleRecipe> {

    public static final MapCodec<FlaskCycleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskCycleRecipe::getInput),
            Codec.INT.fieldOf("count").forGetter(FlaskCycleRecipe::getNumCycles),
            Codec.INT.fieldOf("syphon").forGetter(FlaskCycleRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskCycleRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(FlaskCycleRecipe::getMinimumTier)
    ).apply(instance, FlaskCycleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskCycleRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskCycleRecipe::getInput,
            ByteBufCodecs.INT, FlaskCycleRecipe::getNumCycles,
            ByteBufCodecs.INT, FlaskCycleRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskCycleRecipe::getTicks,
            ByteBufCodecs.INT, FlaskCycleRecipe::getMinimumTier,
            FlaskCycleRecipe::new
    );

    @Override
    public MapCodec<FlaskCycleRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskCycleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
