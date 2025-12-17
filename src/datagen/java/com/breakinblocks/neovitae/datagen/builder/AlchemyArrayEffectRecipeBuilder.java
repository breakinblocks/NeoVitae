package com.breakinblocks.neovitae.datagen.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;

/**
 * Builder for effect-only alchemy array recipes (no item output).
 * These arrays create environmental effects like bounce, movement, updraft, etc.
 */
public class AlchemyArrayEffectRecipeBuilder {
    private final AlchemyArrayEffectType effectType;
    private Ingredient baseInput;
    private Ingredient addedInput;
    private ResourceLocation texture;

    private AlchemyArrayEffectRecipeBuilder(AlchemyArrayEffectType effectType) {
        this.effectType = effectType;
        this.texture = NeoVitae.rl("textures/models/alchemyarrays/sigil.png");
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

    public AlchemyArrayEffectRecipeBuilder texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public void save(RecipeOutput output, String name) {
        save(output, NeoVitae.rl("array/" + name));
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        if (baseInput == null) {
            throw new IllegalStateException("AlchemyArrayRecipe requires a base input");
        }
        if (addedInput == null) {
            throw new IllegalStateException("AlchemyArrayRecipe requires an added input");
        }
        // Effect recipes have empty output
        AlchemyArrayRecipe recipe = new AlchemyArrayRecipe(texture, baseInput, addedInput, ItemStack.EMPTY, effectType);
        recipeOutput.accept(id, recipe, null);
    }
}
