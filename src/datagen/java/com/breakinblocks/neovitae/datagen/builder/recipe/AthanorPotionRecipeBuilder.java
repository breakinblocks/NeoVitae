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
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorPotionRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

/**
 * Recipe builder for Athanor Potion recipes - recipes that transfer potion effects
 * from the tool (lingering alchemy flask) to the output item.
 */
public class AthanorPotionRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private Ingredient input;
    private final List<ItemStackTemplate> guaranteed = new ArrayList<>();
    private final List<Pair<ItemStackTemplate, Double>> chanced = new ArrayList<>();
    private SizedFluidIngredient inputFluid = null;
    private FluidStackTemplate outputFluid = null;

    protected AthanorPotionRecipeBuilder(TagKey<Item> tag) {
        super(ItemStack.EMPTY);
        if (tag == null) {
            throw new IllegalArgumentException("AthanorPotionRecipe tool tag cannot be null");
        }
        this.toolTag = tag;
    }

    public static AthanorPotionRecipeBuilder build(TagKey<Item> tag) {
        return new AthanorPotionRecipeBuilder(tag);
    }

    public AthanorPotionRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public AthanorPotionRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack.getItem()));
    }

    public AthanorPotionRecipeBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public AthanorPotionRecipeBuilder guaranteedOutput(ItemLike item) {
        return guaranteedOutput(item, 1);
    }

    public AthanorPotionRecipeBuilder guaranteedOutput(ItemLike item, int count) {
        guaranteed.add(new ItemStackTemplate(item.asItem(), count));
        return this;
    }

    public AthanorPotionRecipeBuilder chancedOutput(ItemLike item, double chance) {
        return chancedOutput(item, 1, chance);
    }

    public AthanorPotionRecipeBuilder chancedOutput(ItemLike item, int count, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(new ItemStackTemplate(item.asItem(), count), chance));
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(SizedFluidIngredient fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = new SizedFluidIngredient(FluidIngredient.of(fluidInput.getFluid()), fluidInput.getAmount());
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(Fluid fluid, int amount) {
        this.inputFluid = SizedFluidIngredient.of(fluid, amount);
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        this.inputFluid = sizedFluidOf(tag, amount);
        return this;
    }

    public AthanorPotionRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = new FluidStackTemplate(fluidOutput.getFluid(), fluidOutput.getAmount());
        return this;
    }

    public AthanorPotionRecipeBuilder fluidOutput(Fluid fluid, int amount) {
        this.outputFluid = new FluidStackTemplate(fluid, amount);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        if (input == null) {
            throw new IllegalStateException("AthanorPotionRecipe requires an input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("AthanorPotionRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        AthanorPotionRecipe recipe = new AthanorPotionRecipe(ingredientOf(toolTag), input, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid));
        output.accept(
                ResourceKey.create(Registries.RECIPE,
                        makeId(id.identifier(), toolTag.location())),
                recipe,
                advBuilder.build(advancementId(id, "athanor_potion")));
    }

    private static Identifier makeId(Identifier id, Identifier tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("athanor_potion/" + segments[segments.length-1] + "/");
    }
}
