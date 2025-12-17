package com.breakinblocks.neovitae.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AlchemyArrayInput(ItemStack base, ItemStack catalyst) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> base;
            case 1 -> catalyst;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
