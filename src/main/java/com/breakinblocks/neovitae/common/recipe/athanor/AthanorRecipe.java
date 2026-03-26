package com.breakinblocks.neovitae.common.recipe.athanor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AthanorRecipe implements Recipe<AthanorRecipeInput> {

    public static final String RECIPE_TYPE_NAME = "athanor";

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    public static final MapCodec<AthanorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(AthanorRecipe::getTool),
            Ingredient.CODEC.fieldOf("input").forGetter(AthanorRecipe::getInput),
            ItemStack.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(AthanorRecipe::getGuaranteedOutput),
            Codec.pair(ItemStack.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(AthanorRecipe::getChanceOutput),
            FluidStack.CODEC.optionalFieldOf("input_fluid").forGetter(AthanorRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("output_fluid").forGetter(AthanorRecipe::getOutputFluid)
    ).apply(inst, AthanorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AthanorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AthanorRecipe::getTool,
            Ingredient.CONTENTS_STREAM_CODEC, AthanorRecipe::getInput,
            ItemStack.LIST_STREAM_CODEC, AthanorRecipe::getGuaranteedOutput,
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()), AthanorRecipe::getChanceOutput,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorRecipe::getInputFluid,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorRecipe::getOutputFluid,
            AthanorRecipe::new
    );
    private final Ingredient tool;
    private final Ingredient input;
    private final List<ItemStack> guaranteedOutput;
    private final List<Pair<ItemStack, Double>> chanceOutput;
    private final Optional<FluidStack> inputFluid;
    private final Optional<FluidStack> outputFluid;
    private final List<Pair<ItemStack, Double>> allListed;
    public AthanorRecipe(Ingredient tool, Ingredient input, List<ItemStack> guaranteedOutput, List<Pair<ItemStack, Double>> chanceOutput, Optional<FluidStack> inputFluid, Optional<FluidStack> outputStack) {
        this.tool = tool;
        this.input = input;
        this.guaranteedOutput = guaranteedOutput;
        this.chanceOutput = chanceOutput;
        this.inputFluid = inputFluid;
        this.outputFluid = outputStack;

        List<Pair<ItemStack, Double>> outputs = new ArrayList<>();
        guaranteedOutput.forEach(stack -> outputs.add(Pair.of(stack, 1D)));
        outputs.addAll(chanceOutput);
        allListed = List.copyOf(outputs);
    }

    public Ingredient getTool() {
        return tool;
    }

    public Ingredient getInput() {
        return input;
    }

    public List<ItemStack> getGuaranteedOutput() {
        return guaranteedOutput;
    }

    public List<Pair<ItemStack, Double>> getChanceOutput() {
        return chanceOutput;
    }

    public Optional<FluidStack> getInputFluid() {
        return inputFluid;
    }

    public Optional<FluidStack> getOutputFluid() {
        return outputFluid;
    }

    @Override
    public boolean matches(AthanorRecipeInput recipeInput, Level level) {
        if (!tool.test(recipeInput.getItem(0))) {
            return false;
        }
        if (!input.test(recipeInput.getItem(1))) {
            return false;
        }
        if (inputFluid.isPresent()) {
            if (!(inputFluid.get().is(recipeInput.getFluid().getFluidType()) && inputFluid.get().getAmount() <= recipeInput.getFluid().getAmount())) {
                return false;
            }
        }
        return true;
    }

    private List<ItemStack> outputStacks = new ArrayList<>();
    private FluidStack outputFluidStack = FluidStack.EMPTY;
    @Override
    public ItemStack assemble(AthanorRecipeInput input, HolderLookup.Provider registries) {
        outputStacks.clear();
        outputFluidStack = outputFluid.orElse(FluidStack.EMPTY);
        outputStacks.addAll(guaranteedOutput);
        ItemStack toolStack = input.getItem(0);
        double bonusChance = toolStack.getOrDefault(NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStack, Double> entry : chanceOutput) {
            if (Math.random() < entry.getSecond() * bonusChance) {
                outputStacks.add(entry.getFirst());
            }
        }
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getActualOutputs() {
        return outputStacks;
    }

    public FluidStack getActualOutputFluid() {
        return outputFluidStack;
    }

    public List<Pair<ItemStack, Double>> getAllListedOutputs() {
        return allListed;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ATHANOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.ATHANOR_TYPE.get();
    }
}
