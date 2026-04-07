package com.breakinblocks.neovitae.common.recipe.athanor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class AthanorRecipeInput implements RecipeInput {

    private final ItemStack toolStack;
    private final ItemStack[] inputStacks;
    private final FluidStack inputFluid;

    public AthanorRecipeInput(ItemStack toolStack, ItemStack[] inputStacks, FluidStack inputFluid) {
        this.toolStack = toolStack;
        this.inputStacks = inputStacks;
        this.inputFluid = inputFluid;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) return toolStack;
        int inputIdx = index - 1;
        if (inputIdx >= 0 && inputIdx < inputStacks.length) {
            return inputStacks[inputIdx];
        }
        return ItemStack.EMPTY;
    }

    public FluidStack getFluid() {
        return inputFluid;
    }

    @Override
    public int size() {
        return 1 + inputStacks.length;
    }
}
