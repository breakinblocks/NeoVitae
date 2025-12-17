package com.breakinblocks.neovitae.common.recipe.arc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Serializer for ARCPotionRecipe - same structure as ARCSerializer but creates ARCPotionRecipe instances.
 */
public class ARCPotionSerializer implements RecipeSerializer<ARCPotionRecipe> {

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    public static final MapCodec<ARCPotionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(ARCPotionRecipe::getTool),
            Ingredient.CODEC.fieldOf("input").forGetter(ARCPotionRecipe::getInput),
            ItemStack.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(ARCPotionRecipe::getGuaranteedOutput),
            Codec.pair(ItemStack.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(ARCPotionRecipe::getChanceOutput),
            FluidStack.CODEC.optionalFieldOf("input_fluid").forGetter(ARCPotionRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("output_fluid").forGetter(ARCPotionRecipe::getOutputFluid)
    ).apply(inst, ARCPotionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ARCPotionRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ARCPotionRecipe::getTool,
            Ingredient.CONTENTS_STREAM_CODEC, ARCPotionRecipe::getInput,
            ItemStack.LIST_STREAM_CODEC, ARCPotionRecipe::getGuaranteedOutput,
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()), ARCPotionRecipe::getChanceOutput,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), ARCPotionRecipe::getInputFluid,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), ARCPotionRecipe::getOutputFluid,
            ARCPotionRecipe::new
    );

    @Override
    public MapCodec<ARCPotionRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ARCPotionRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
