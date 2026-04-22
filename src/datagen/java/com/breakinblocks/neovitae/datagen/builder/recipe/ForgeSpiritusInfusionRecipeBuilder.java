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

public class ForgeSpiritusInfusionRecipeBuilder extends BaseRecipeBuilder {

    private double minWill;
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
        this.gemInput = Ingredient.of() /* TODO(phase15): tag-based ingredient � needs HolderGetter plumb-through */;
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder minWill(double minWill) {
        this.minWill = minWill;
        return this;
    }

    public ForgeSpiritusInfusionRecipeBuilder drain(double drain) {
        this.drainedWill = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeSpiritusInfusionRecipe recipe = new ForgeSpiritusInfusionRecipe(minWill, drainedWill, gemInput);
        output.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id.identifier().withPrefix("hellfire_forge/")), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
