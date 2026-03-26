package com.breakinblocks.neovitae.common.recipe.athanor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class AthanorRecipeInput implements RecipeInput {

    private final ItemStack toolStack;
    private final ItemStack inputStack;
    private final FluidStack inputFluid;
    public AthanorRecipeInput(ItemStack toolStack, ItemStack inputStack, FluidStack inputFluid) {
        this.toolStack = toolStack;
        this.inputStack = inputStack;
        this.inputFluid = inputFluid;
    }

    @Override
    public ItemStack getItem(int index) {
        return switch(index) {
            case 0 -> toolStack;
            case 1 -> inputStack;
            default -> ItemStack.EMPTY;
        };
    }

    public FluidStack getFluid() {
        return inputFluid;
    }

    @Override
    public int size() {
        return 2;
    }
}
