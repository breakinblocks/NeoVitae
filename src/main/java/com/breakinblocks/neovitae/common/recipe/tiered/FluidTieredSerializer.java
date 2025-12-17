package com.breakinblocks.neovitae.common.recipe.tiered;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

public class FluidTieredSerializer implements RecipeSerializer<FluidTieredRecipe> {
    public static final MapCodec<FluidTieredRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").forGetter(FluidTieredRecipe::category),
            ShapedRecipePattern.MAP_CODEC.fieldOf("pattern").forGetter(FluidTieredRecipe::getPattern),
            Codec.INT.fieldOf("primary").forGetter(FluidTieredRecipe::getPrimary),
            Codec.INT.fieldOf("secondary").forGetter(FluidTieredRecipe::getSecondary),
            ItemStack.CODEC.fieldOf("result").forGetter(FluidTieredRecipe::getOutput)
    ).apply(builder, FluidTieredRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTieredRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, FluidTieredRecipe::category,
            ShapedRecipePattern.STREAM_CODEC, FluidTieredRecipe::getPattern,
            ByteBufCodecs.INT, FluidTieredRecipe::getPrimary,
            ByteBufCodecs.INT, FluidTieredRecipe::getSecondary,
            ItemStack.STREAM_CODEC, FluidTieredRecipe::getOutput,
            FluidTieredRecipe::new
    );

    @Override
    public MapCodec<FluidTieredRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FluidTieredRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
