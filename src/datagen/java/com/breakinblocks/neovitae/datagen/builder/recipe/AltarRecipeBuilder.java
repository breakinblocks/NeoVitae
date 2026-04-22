package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.aravitae.AraVitaeRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.registries.Registries;

public class AltarRecipeBuilder extends BaseRecipeBuilder {

    protected int minTier = 0;
    protected int totalBlood;
    protected int craftingSpeed;
    protected int drainSpeed;
    protected Ingredient input;
    protected boolean copyInputComponents = false;

    protected AltarRecipeBuilder(ItemLike result, int count) {
        super(result, count);
        if (result == null) {
            throw new IllegalArgumentException("AltarRecipe result cannot be null");
        }
    }

    public static AltarRecipeBuilder build(ItemLike result) {
        return new AltarRecipeBuilder(result, 1);
    }

    public AltarRecipeBuilder minTier(int tier) {
        if (tier < 0) {
            throw new IllegalArgumentException("minTier cannot be negative");
        }
        this.minTier = tier;
        return this;
    }

    public AltarRecipeBuilder bloodNeeded(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("bloodNeeded cannot be negative");
        }
        this.totalBlood = amount;
        return this;
    }

    public AltarRecipeBuilder consumption(int craftingSpeed) {
        if (craftingSpeed < 0) {
            throw new IllegalArgumentException("consumption cannot be negative");
        }
        this.craftingSpeed = craftingSpeed;
        return this;
    }

    public AltarRecipeBuilder drain(int drainSpeed) {
        if (drainSpeed < 0) {
            throw new IllegalArgumentException("drain cannot be negative");
        }
        this.drainSpeed = drainSpeed;
        return this;
    }

    public AltarRecipeBuilder from(ItemLike input) {
        return from(Ingredient.of(input));
    }

    public AltarRecipeBuilder from(TagKey<Item> input) {
        return from(ingredientOf(input));
    }

    public AltarRecipeBuilder from(Ingredient input) {
        this.input = input;
        return this;
    }

    public AltarRecipeBuilder copyInputComponents() {
        this.copyInputComponents = true;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        if (input == null) {
            throw new IllegalStateException("AltarRecipe requires an input ingredient (use .from())");
        }
        if (totalBlood <= 0) {
            throw new IllegalStateException("AltarRecipe requires bloodNeeded > 0");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        AraVitaeRecipe recipe = new AraVitaeRecipe(input, resultTemplate(), minTier, totalBlood, craftingSpeed, drainSpeed, copyInputComponents);
        output.accept(ResourceKey.create(Registries.RECIPE, id.identifier().withPrefix("ara_vitae/")), recipe, advBuilder.build(advancementId(id, "ara_vitae")));
    }
}
