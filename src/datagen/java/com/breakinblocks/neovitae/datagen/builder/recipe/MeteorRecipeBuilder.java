package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for meteor recipes in datagen.
 */
public class MeteorRecipeBuilder extends BaseRecipeBuilder {

    private final Ingredient input;
    private final int syphon;
    private final float explosionRadius;
    private final List<MeteorLayer> layers = new ArrayList<>();

    private MeteorRecipeBuilder(Ingredient input, int syphon, float explosionRadius) {
        super(ItemStack.EMPTY); // Meteor recipes don't have a result item
        this.input = input;
        this.syphon = syphon;
        this.explosionRadius = explosionRadius;
    }

    public static MeteorRecipeBuilder meteor(Ingredient input, int syphon, float explosionRadius) {
        return new MeteorRecipeBuilder(input, syphon, explosionRadius);
    }

    public MeteorRecipeBuilder addLayer(MeteorLayer layer) {
        this.layers.add(layer);
        return this;
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        MeteorRecipe recipe = new MeteorRecipe(input, syphon, explosionRadius, layers);
        output.accept(id, recipe, null);
    }
}
