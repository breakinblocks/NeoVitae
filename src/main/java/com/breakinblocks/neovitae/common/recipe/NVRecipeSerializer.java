package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Generic recipe serializer that eliminates boilerplate.
 * Each recipe type only needs to define its CODEC and STREAM_CODEC.
 *
 * <p>Usage:
 * <pre>{@code
 * // In NVRecipes registration:
 * SERIALIZERS.register("my_recipe", () -> new NVRecipeSerializer<>(MyRecipe.CODEC, MyRecipe.STREAM_CODEC));
 * }</pre>
 */
public record NVRecipeSerializer<T extends Recipe<?>>(
        MapCodec<T> mapCodec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
) implements RecipeSerializer<T> {

    @Override
    public MapCodec<T> codec() {
        return mapCodec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }
}
