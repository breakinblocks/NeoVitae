package com.breakinblocks.neovitae.common.recipe.meteor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import java.util.Optional;

/**
 * Helper class for finding meteor recipes.
 */
public class MeteorRecipeHelper {

    /**
     * Finds a meteor recipe that matches the given catalyst item.
     *
     * @param level The level to search in
     * @param catalyst The catalyst item to match
     * @return The matching recipe, or null if not found
     */
    public static MeteorRecipe findRecipe(Level level, ItemStack catalyst) {
        if (catalyst.isEmpty()) {
            return null;
        }

        MeteorInput input = new MeteorInput(catalyst);
        Optional<RecipeHolder<MeteorRecipe>> result = level.getRecipeManager()
                .getRecipeFor(BMRecipes.METEOR_TYPE.get(), input, level);

        return result.map(RecipeHolder::value).orElse(null);
    }
}
