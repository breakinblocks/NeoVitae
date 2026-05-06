package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.registries.Registries;

/**
 * Builder for Sentient Downgrade recipes.
 * These recipes define what items can be used to apply downgrades to living armor.
 */
public class SentientDowngradeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final Identifier livingUpgradeId;

    private SentientDowngradeRecipeBuilder(Ingredient input, Identifier livingUpgradeId) {
        this.input = input;
        this.livingUpgradeId = livingUpgradeId;
    }

    /**
     * Creates a new living downgrade recipe builder.
     *
     * @param input          The item ingredient used to apply the downgrade
     * @param livingUpgradeId The resource location of the living upgrade (downgrade)
     * @return A new builder instance
     */
    public static SentientDowngradeRecipeBuilder downgrade(Ingredient input, Identifier livingUpgradeId) {
        return new SentientDowngradeRecipeBuilder(input, livingUpgradeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        // Living downgrade recipes don't use advancement criteria
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        // Living downgrade recipes don't use groups
        return this;
    }

    // 26.1: RecipeBuilder.getResult() was removed.

    /**
     * Saves the recipe to the output.
     *
     * @param output The recipe output
     * @param id     The recipe ID
     */
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        SentientDowngradeRecipe recipe = new SentientDowngradeRecipe(input, livingUpgradeId);
        output.accept(id, recipe, null);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(com.breakinblocks.neovitae.NeoVitae.MODID, "auto"));
    }
}
