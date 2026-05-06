package com.breakinblocks.neovitae.common.recipe.sentientdowngrade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record SentientDowngradeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? input : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
