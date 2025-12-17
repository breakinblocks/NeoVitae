package com.breakinblocks.neovitae.common.recipe.livingdowngrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Serializer for Living Downgrade recipes.
 * Handles JSON parsing and network serialization.
 */
public class LivingDowngradeSerializer implements RecipeSerializer<LivingDowngradeRecipe> {

    public static final MapCodec<LivingDowngradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(LivingDowngradeRecipe::getInput),
            ResourceLocation.CODEC.fieldOf("livingarmour").forGetter(LivingDowngradeRecipe::getLivingUpgradeId)
    ).apply(instance, LivingDowngradeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LivingDowngradeRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, LivingDowngradeRecipe::getInput,
            ResourceLocation.STREAM_CODEC, LivingDowngradeRecipe::getLivingUpgradeId,
            LivingDowngradeRecipe::new
    );

    @Override
    public MapCodec<LivingDowngradeRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LivingDowngradeRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
