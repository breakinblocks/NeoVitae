package com.breakinblocks.neovitae.datagen.builder.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AthanorRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private Ingredient input;
    private List<ItemStack> guaranteed = new ArrayList<>();
    private List<Pair<ItemStack, Double>> chanced = new ArrayList<>();
    private FluidStack inputFluid = null;
    private FluidStack outputFluid = null;
    private final EnumMap<SpiritusType, Double> spiritusCosts = new EnumMap<>(SpiritusType.class);

    protected AthanorRecipeBuilder(TagKey<Item> tag) {
        super(ItemStack.EMPTY);
        if (tag == null) {
            throw new IllegalArgumentException("AthanorRecipe tool tag cannot be null");
        }
        this.toolTag = tag;
    }

    public static AthanorRecipeBuilder build(TagKey<Item> tag) {
        return new AthanorRecipeBuilder(tag);
    }

    public AthanorRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public AthanorRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public AthanorRecipeBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public AthanorRecipeBuilder guaranteedOutput(ItemStack output) {
        guaranteed.add(output);
        return this;
    }

    public AthanorRecipeBuilder chancedOutput(ItemStack stack, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(stack, chance));
        return this;
    }

    public AthanorRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public AthanorRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = fluidOutput;
        return this;
    }

    public AthanorRecipeBuilder spiritusCost(SpiritusType type, double amount) {
        spiritusCosts.put(type, amount);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (input == null) {
            throw new IllegalStateException("AthanorRecipe requires an input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("AthanorRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        AthanorRecipe recipe = new AthanorRecipe(Ingredient.of(toolTag), input, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid), Map.copyOf(spiritusCosts));
        output.accept(makeId(id, toolTag.location()), recipe, advBuilder.build(advancementId(id, "athanor")));
    }

    private static ResourceLocation makeId(ResourceLocation id, ResourceLocation tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("athanor/" + segments[segments.length-1] + "/");
    }
}
