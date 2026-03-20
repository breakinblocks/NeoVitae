package com.breakinblocks.neovitae.common.recipe.meteor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.Optional;

public class MeteorRecipeHelper {

    public static MeteorRecipe findRecipe(Level level, ItemStack catalyst) {
        if (catalyst.isEmpty()) {
            return null;
        }

        MeteorInput input = new MeteorInput(catalyst);
        Optional<RecipeHolder<MeteorRecipe>> result = level.getRecipeManager()
                .getRecipeFor(NVRecipes.METEOR_TYPE.get(), input, level);

        return result.map(RecipeHolder::value).orElse(null);
    }
}
