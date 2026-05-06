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

/**
 * Builder for Sentient Downgrade recipes.
 * These recipes define what items can be used to apply downgrades to sentient armor.
 */
public class SentientDowngradeRecipeBuilder implements RecipeBuilder {
    private final Ingredient input;
    private final ResourceLocation livingUpgradeId;

    private SentientDowngradeRecipeBuilder(Ingredient input, ResourceLocation livingUpgradeId) {
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
    public static SentientDowngradeRecipeBuilder downgrade(Ingredient input, ResourceLocation livingUpgradeId) {
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

    @Override
    public Item getResult() {
        return Items.AIR; // These recipes don't produce an item output
    }

    /**
     * Saves the recipe to the output.
     *
     * @param output The recipe output
     * @param id     The recipe ID
     */
    public void save(RecipeOutput output, ResourceLocation id) {
        SentientDowngradeRecipe recipe = new SentientDowngradeRecipe(input, livingUpgradeId);
        output.accept(id, recipe, null);
    }
}
