package com.breakinblocks.neovitae.compat.jei.flask;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record FlaskCombinationJEIRecipe(
        ItemStack inputFlask,
        List<Ingredient> ingredients,
        ItemStack outputFlask,
        int syphon,
        int ticks,
        int minimumTier
) {
}
