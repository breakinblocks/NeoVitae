package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * TODO(phase15 stage2): Full recipe provider rewrite against the 26.1 RecipeProvider.Runner
 * pattern with the 6+ custom NeoVitae recipe builders (AlchemyArray, Tabula Vitae, Athanor,
 * Hellfire Forge, Flask, LivingDowngrade, Meteor, Tiered, Altar, etc.).
 *
 * Stubbed for Stage 1 — the generated recipe JSON under src/generated/resources/data/neovitae/recipe/
 * is already committed and is what the runtime loads.
 */
public class NVRecipeProvider extends RecipeProvider {

    protected NVRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Intentionally empty — see class-level TODO.
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new NVRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "NeoVitae Recipes";
        }
    }
}
