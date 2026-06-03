package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;

public class SentientDowngradeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final Identifier sentientUpgradeId;

    private SentientDowngradeRecipeBuilder(Ingredient input, Identifier sentientUpgradeId) {
        this.input = input;
        this.sentientUpgradeId = sentientUpgradeId;
    }

    public static SentientDowngradeRecipeBuilder downgrade(Ingredient input, Identifier sentientUpgradeId) {
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

    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        SentientDowngradeRecipe recipe = new SentientDowngradeRecipe(input, sentientUpgradeId);
        output.accept(id, recipe, null);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(NeoVitae.MODID, "auto"));
    }
}
