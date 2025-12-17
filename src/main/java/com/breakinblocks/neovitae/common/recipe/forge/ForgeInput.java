package com.breakinblocks.neovitae.common.recipe.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class ForgeInput implements RecipeInput {

    private final List<ItemStack> inputStacks;
    private final ItemStack gemStack;
    private final int gemIndex;

    public ForgeInput(List<ItemStack> items, ItemStack gemStack, int gemIndex) {
        this.inputStacks = items;
        this.gemStack = gemStack;
        this.gemIndex = gemIndex;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index >= 0 && index < inputStacks.size()) {
            return inputStacks.get(index);
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getGem() {
        return this.gemStack;
    }

    public int getGemIndex() {
        return this.gemIndex;
    }

    public ItemStack[] asArray() {
        ItemStack[] result = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            result[i] = i < inputStacks.size() ? inputStacks.get(i) : ItemStack.EMPTY;
        }
        return result;
    }

    @Override
    public int size() {
        return (int) inputStacks.stream().filter(stack -> !stack.isEmpty()).count();
    }
}
