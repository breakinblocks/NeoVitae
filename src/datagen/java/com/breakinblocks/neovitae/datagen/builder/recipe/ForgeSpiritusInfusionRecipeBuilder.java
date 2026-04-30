package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSpiritusInfusionRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.registries.Registries;

public class ForgeSpiritusInfusionRecipeBuilder extends BaseRecipeBuilder {

    private double minSpiritus;
    private double drainedWill;
    private Ingredient gemInput;

    private ForgeSpiritusInfusionRecipeBuilder() {
        super(Items.DIAMOND_SWORD);
    }

    public static ForgeSpiritusInfusionRecipeBuilder build() {
        return new ForgeSpiritusInfusionRecipeBuilder();
    }

    public ForgeSpiritusInfusionRecipeBuilder gemInput(ItemLike item) {
        this.gemInput = Ingredient.of(item);
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder gemInput(TagKey<Item> tag) {
        this.gemInput = ingredientOf(tag);
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder minSpiritus(double minSpiritus) {
        this.minSpiritus = minSpiritus;
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder drain(double drain) {
        this.drainedWill = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeSpiritusInfusionRecipe recipe = new ForgeSpiritusInfusionRecipe(minSpiritus, drainedWill, gemInput);
        output.accept(ResourceKey.create(Registries.RECIPE, id.identifier().withPrefix("hellfire_forge/")), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
