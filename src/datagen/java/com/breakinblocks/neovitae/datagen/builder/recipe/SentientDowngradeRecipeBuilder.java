package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;

public class SentientDowngradeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final ResourceLocation sentientUpgradeId;

    private SentientDowngradeRecipeBuilder(Ingredient input, ResourceLocation sentientUpgradeId) {
        this.input = input;
        this.sentientUpgradeId = sentientUpgradeId;
    }

    public static SentientDowngradeRecipeBuilder downgrade(Ingredient input, ResourceLocation sentientUpgradeId) {
        return new SentientDowngradeRecipeBuilder(input, sentientUpgradeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        SentientDowngradeRecipe recipe = new SentientDowngradeRecipe(input, sentientUpgradeId);
        output.accept(id, recipe, null);
    }
}
