package com.breakinblocks.neovitae.common.recipe.alchemyarray;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;

public class AlchemyArraySerializer implements RecipeSerializer<AlchemyArrayRecipe> {

    public static final MapCodec<AlchemyArrayRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(AlchemyArrayRecipe::getTexture),
            Ingredient.CODEC_NONEMPTY.fieldOf("baseinput").forGetter(AlchemyArrayRecipe::getBaseInput),
            Ingredient.CODEC_NONEMPTY.fieldOf("addedinput").forGetter(AlchemyArrayRecipe::getAddedInput),
            ItemStack.CODEC.optionalFieldOf("output", ItemStack.EMPTY).forGetter(AlchemyArrayRecipe::getOutput),
            AlchemyArrayEffectType.CODEC.optionalFieldOf("effect_type", AlchemyArrayEffectType.CRAFTING).forGetter(AlchemyArrayRecipe::getEffectType)
    ).apply(instance, AlchemyArrayRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyArrayRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AlchemyArrayRecipe::getTexture,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getBaseInput,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getAddedInput,
            ItemStack.OPTIONAL_STREAM_CODEC, AlchemyArrayRecipe::getOutput,
            AlchemyArrayEffectType.STREAM_CODEC, AlchemyArrayRecipe::getEffectType,
            AlchemyArrayRecipe::new
    );

    @Override
    public MapCodec<AlchemyArrayRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AlchemyArrayRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
