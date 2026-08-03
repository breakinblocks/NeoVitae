package com.breakinblocks.neovitae.datagen.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Builder for effect-only alchemy array recipes (no item output).
 * These arrays create environmental effects like bounce, movement, updraft, etc.
 */
public class AlchemyArrayEffectRecipeBuilder {
    private final AlchemyArrayEffectType effectType;
    private Ingredient baseInput;
    private Ingredient addedInput;
    private Identifier texture;
    private int evCost = 0;

    private AlchemyArrayEffectRecipeBuilder(AlchemyArrayEffectType effectType) {
        this.effectType = effectType;
        this.texture = NeoVitae.rl("textures/models/alchemyarrays/defaultarray.png");
    }

    public static AlchemyArrayEffectRecipeBuilder effect(AlchemyArrayEffectType effectType) {
        return new AlchemyArrayEffectRecipeBuilder(effectType);
    }

    public AlchemyArrayEffectRecipeBuilder base(ItemLike item) {
        this.baseInput = Ingredient.of(item);
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder base(Ingredient ingredient) {
        this.baseInput = ingredient;
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder added(ItemLike item) {
        this.addedInput = Ingredient.of(item);
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder added(Ingredient ingredient) {
        this.addedInput = ingredient;
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder texture(String path) {
        this.texture = NeoVitae.rl(path);
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder texture(Identifier texture) {
        this.texture = texture;
        return this;
    }

    public AlchemyArrayEffectRecipeBuilder evCost(int evCost) {
        this.evCost = evCost;
        return this;
    }

    public void save(RecipeOutput output, String name) {
        save(output, ResourceKey.create(Registries.RECIPE, NeoVitae.rl("array/" + name)));
    }

    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        if (baseInput == null) {
            throw new IllegalStateException("AlchemyArrayRecipe requires a base input");
        }
        if (addedInput == null) {
            throw new IllegalStateException("AlchemyArrayRecipe requires an added input");
        }
        AlchemyArrayRecipe recipe = new AlchemyArrayRecipe(texture, baseInput, addedInput, null, effectType, evCost);
        recipeOutput.accept(id, recipe, null);
    }
}
