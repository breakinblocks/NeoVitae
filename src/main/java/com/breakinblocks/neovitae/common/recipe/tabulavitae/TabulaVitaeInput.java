package com.breakinblocks.neovitae.common.recipe.tabulavitae;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record TabulaVitaeInput(List<ItemStack> items, int orbTier) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < items.size()) {
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
