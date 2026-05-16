package com.breakinblocks.neovitae.datagen.builder;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.datagen.builder.recipe.BaseRecipeBuilder;

import java.util.ArrayList;
import java.util.List;

public class TabulaVitaeRecipeBuilder {
    public static final int MAX_INPUTS = TabulaVitaeRecipe.MAX_INPUTS;

    private final ItemStackTemplate output;
    private final List<Ingredient> inputs = new ArrayList<>();
    private int syphon = 0;
    private int ticks = 200;
    private int minimumTier = 0;

    private TabulaVitaeRecipeBuilder(ItemStackTemplate output) {
        if (output == null) {
            throw new IllegalArgumentException("TabulaVitaeRecipe output cannot be null");
        }
        this.output = output;
    }

    public static TabulaVitaeRecipeBuilder build(ItemLike output) {
        return new TabulaVitaeRecipeBuilder(new ItemStackTemplate(output.asItem(), 1));
    }

    public static TabulaVitaeRecipeBuilder build(ItemLike output, int count) {
        return new TabulaVitaeRecipeBuilder(new ItemStackTemplate(output.asItem(), count));
    }

    public TabulaVitaeRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public TabulaVitaeRecipeBuilder input(TagKey<Item> tag) {
        return input(BaseRecipeBuilder.ingredientOf(tag));
    }

    public TabulaVitaeRecipeBuilder input(Ingredient ingredient) {
        if (inputs.size() >= MAX_INPUTS) {
            throw new IllegalStateException("TabulaVitaeRecipe cannot have more than " + MAX_INPUTS + " inputs");
        }
        this.inputs.add(ingredient);
        return this;
    }

    public TabulaVitaeRecipeBuilder syphon(int syphon) {
        if (syphon < 0) {
            throw new IllegalArgumentException("syphon cannot be negative");
        }
        this.syphon = syphon;
        return this;
    }

    public TabulaVitaeRecipeBuilder ticks(int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("ticks cannot be negative");
        }
        this.ticks = ticks;
        return this;
    }

    public TabulaVitaeRecipeBuilder minimumTier(int tier) {
        if (tier < 0) {
            throw new IllegalArgumentException("minimumTier cannot be negative");
        }
        this.minimumTier = tier;
        return this;
    }

    public void save(RecipeOutput output, String name) {
        save(output, ResourceKey.create(Registries.RECIPE, NeoVitae.rl("alchemytable/" + name)));
    }

    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        if (inputs.isEmpty()) {
            throw new IllegalStateException("TabulaVitaeRecipe must have at least one input");
        }
        TabulaVitaeRecipe recipe = new TabulaVitaeRecipe(inputs, output, syphon, ticks, minimumTier);
        recipeOutput.accept(id, recipe, null);
    }
}
