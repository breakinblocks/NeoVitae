package com.breakinblocks.neovitae.common.recipe.livingdowngrade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

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
