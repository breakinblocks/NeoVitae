package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.registries.Registries;

public class ForgeTransformRecipeBuilder extends BaseRecipeBuilder {

    private double minSpiritus;
    private double drainedSpiritus;
    private final List<Ingredient> catalysts = new ArrayList<>();
    private Ingredient transformInput;

    private ForgeTransformRecipeBuilder(ItemLike result) {
        super(result);
    }

    public static ForgeTransformRecipeBuilder build(ItemLike result) {
        return new ForgeTransformRecipeBuilder(result);
    }

    public ForgeTransformRecipeBuilder transformInput(ItemLike item) {
        this.transformInput = Ingredient.of(item);
        return this;
    }

    public ForgeTransformRecipeBuilder catalyst(ItemLike item) {
        this.catalysts.add(Ingredient.of(item));
        return this;
    }

    public ForgeTransformRecipeBuilder catalyst(TagKey<Item> tag) {
        this.catalysts.add(ingredientOf(tag));
        return this;
    }

    public ForgeTransformRecipeBuilder minSpiritus(double minSpiritus) {
        this.minSpiritus = minSpiritus;
        return this;
    }

    public ForgeTransformRecipeBuilder drain(double drain) {
        this.drainedSpiritus = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeTransformRecipe recipe = new ForgeTransformRecipe(minSpiritus, drainedSpiritus, catalysts, transformInput, resultTemplate());
        output.accept(ResourceKey.create(Registries.RECIPE, id.identifier().withPrefix("hellfire_forge/")), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
