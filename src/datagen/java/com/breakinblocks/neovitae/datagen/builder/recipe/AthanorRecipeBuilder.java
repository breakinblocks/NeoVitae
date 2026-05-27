package com.breakinblocks.neovitae.datagen.builder.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public class AthanorRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private final List<Ingredient> inputs = new ArrayList<>();
    private final List<ItemStackTemplate> guaranteed = new ArrayList<>();
    private final List<Pair<ItemStackTemplate, Double>> chanced = new ArrayList<>();
    private SizedFluidIngredient inputFluid = null;
    private FluidStackTemplate outputFluid = null;
    private final EnumMap<SpiritusType, Double> spiritusCosts = new EnumMap<>(SpiritusType.class);
    private boolean spiritusBoost = false;

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
        return input(Ingredient.of(stack.getItem()));
    }

    public AthanorRecipeBuilder input(Ingredient ingredient) {
        if (inputs.size() >= AthanorRecipe.MAX_INPUTS) {
            throw new IllegalStateException("AthanorRecipe supports at most " + AthanorRecipe.MAX_INPUTS + " inputs");
        }
        this.inputs.add(ingredient);
        return this;
    }

    public AthanorRecipeBuilder guaranteedOutput(ItemLike item) {
        return guaranteedOutput(item, 1);
    }

    public AthanorRecipeBuilder guaranteedOutput(ItemLike item, int count) {
        guaranteed.add(new ItemStackTemplate(item.asItem(), count));
        return this;
    }

    public AthanorRecipeBuilder chancedOutput(ItemLike item, double chance) {
        return chancedOutput(item, 1, chance);
    }

    public AthanorRecipeBuilder chancedOutput(ItemLike item, int count, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(new ItemStackTemplate(item.asItem(), count), chance));
        return this;
    }

    public AthanorRecipeBuilder fluidInput(SizedFluidIngredient fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public AthanorRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = new SizedFluidIngredient(FluidIngredient.of(fluidInput.getFluid()), fluidInput.getAmount());
        return this;
    }

    public AthanorRecipeBuilder fluidInput(Fluid fluid, int amount) {
        this.inputFluid = SizedFluidIngredient.of(fluid, amount);
        return this;
    }

    public AthanorRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        this.inputFluid = sizedFluidOf(tag, amount);
        return this;
    }

    public AthanorRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = new FluidStackTemplate(fluidOutput.getFluid(), fluidOutput.getAmount());
        return this;
    }

    public AthanorRecipeBuilder fluidOutput(Fluid fluid, int amount) {
        this.outputFluid = new FluidStackTemplate(fluid, amount);
        return this;
    }

    public AthanorRecipeBuilder spiritusCost(SpiritusType type, double amount) {
        spiritusCosts.put(type, amount);
        return this;
    }

    public AthanorRecipeBuilder spiritusBoost() {
        this.spiritusBoost = true;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        if (inputs.isEmpty()) {
            throw new IllegalStateException("AthanorRecipe requires at least one input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("AthanorRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        AthanorRecipe recipe = new AthanorRecipe(ingredientOf(toolTag), inputs, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid), Map.copyOf(spiritusCosts), spiritusBoost);
        output.accept(
                ResourceKey.create(Registries.RECIPE,
                        makeId(id.identifier(), toolTag.location())),
                recipe,
                advBuilder.build(advancementId(id, "athanor")));
    }

    private static Identifier makeId(Identifier id, Identifier tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("athanor/" + segments[segments.length-1] + "/");
    }
}
