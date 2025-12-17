package com.breakinblocks.neovitae.common.recipe.alchemytable;

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

public class AlchemyTableSerializer implements RecipeSerializer<AlchemyTableRecipe> {

    public static final MapCodec<AlchemyTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(AlchemyTableRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(AlchemyTableRecipe::getOutput),
            Codec.INT.fieldOf("syphon").forGetter(AlchemyTableRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(AlchemyTableRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(AlchemyTableRecipe::getMinimumTier)
    ).apply(instance, AlchemyTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, AlchemyTableRecipe::getInput,
            ItemStack.STREAM_CODEC, AlchemyTableRecipe::getOutput,
            ByteBufCodecs.INT, AlchemyTableRecipe::getSyphon,
            ByteBufCodecs.INT, AlchemyTableRecipe::getTicks,
            ByteBufCodecs.INT, AlchemyTableRecipe::getMinimumTier,
            AlchemyTableRecipe::new
    );

    @Override
    public MapCodec<AlchemyTableRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
