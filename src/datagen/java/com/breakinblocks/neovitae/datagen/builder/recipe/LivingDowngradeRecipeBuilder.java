package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Builder for Living Downgrade recipes.
 * These recipes define what items can be used to apply downgrades to living armor.
 */
public class LivingDowngradeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final Identifier livingUpgradeId;

    private LivingDowngradeRecipeBuilder(Ingredient input, Identifier livingUpgradeId) {
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
    public static LivingDowngradeRecipeBuilder downgrade(Ingredient input, Identifier livingUpgradeId) {
        return new LivingDowngradeRecipeBuilder(input, livingUpgradeId);
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
        LivingDowngradeRecipe recipe = new LivingDowngradeRecipe(input, livingUpgradeId);
        output.accept(id, recipe, null);
    }

    @Override
    public net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> defaultId() {
        return net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE,
                net.minecraft.resources.Identifier.fromNamespaceAndPath(com.breakinblocks.neovitae.NeoVitae.MODID, "auto"));
    }
}
