package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Serializer for the Upgrade Tome combining recipe.
 */
public class UpgradeTomeCombineSerializer implements RecipeSerializer<UpgradeTomeCombineRecipe> {

    public static final MapCodec<UpgradeTomeCombineRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> CraftingBookCategory.MISC)
    ).apply(builder, UpgradeTomeCombineRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeTomeCombineRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, r -> CraftingBookCategory.MISC,
            UpgradeTomeCombineRecipe::new
    );

    @Override
    public MapCodec<UpgradeTomeCombineRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, UpgradeTomeCombineRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
