package com.breakinblocks.neovitae.common.recipe.livingdowngrade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Input container for Living Downgrade recipes.
 * Contains the item being used to apply the downgrade.
 */
public record LivingDowngradeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? input : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
