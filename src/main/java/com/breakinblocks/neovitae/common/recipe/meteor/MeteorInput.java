package com.breakinblocks.neovitae.common.recipe.meteor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Recipe input for meteor recipes.
 * Contains the catalyst item used to trigger the meteor.
 */
public record MeteorInput(ItemStack catalyst) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? catalyst : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
