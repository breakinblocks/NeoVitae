package com.breakinblocks.neovitae.common.recipe.tiered;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

public class FluidTieredRecipe extends BaseTieredRecipe {
    public static final String NAME = "fluid_tiered";

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

    public FluidTieredRecipe(CraftingBookCategory category, ShapedRecipePattern pattern, int primary, int secondary, ItemStack baseOutput) {
        super(category, pattern, primary, secondary, baseOutput);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack primaryStack = input.getItem(primary);
        ItemStack secondaryStack = input.getItem(secondary);
        FluidStack primaryContent = primaryStack.getOrDefault(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        FluidStack secondaryContent = secondaryStack.getOrDefault(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        // add one to previous but not more than 16 -> make sure match returns false if input are 16 already
        int resultTier = Math.clamp(primaryStack.get(NVDataComponents.CONTAINER_TIER) + 1, 1, 16); // checked in super.matches

        // if same, add
        // if only one empty, take other
        // otherwise take primary
        FluidStack resultContent = primaryContent.copy();
        if (primaryContent.is(secondaryContent.getFluid())) {
            resultContent = primaryContent.copyWithAmount(primaryContent.getAmount() + secondaryContent.getAmount());
        } else if (primaryContent.isEmpty() && !secondaryContent.isEmpty()) {
            resultContent = secondaryContent.copy();
        } else if (!primaryContent.isEmpty() && secondaryContent.isEmpty()) {
            resultContent = primaryContent.copy();
        }

        ItemStack resultStack = output.copy();
        resultStack.set(NVDataComponents.CONTAINER_TIER, resultTier);
        resultStack.set(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(resultContent));

        return resultStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLUID_TIERED_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return super.getType();
    }
}
