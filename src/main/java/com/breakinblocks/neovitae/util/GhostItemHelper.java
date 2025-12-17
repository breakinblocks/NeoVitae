package com.breakinblocks.neovitae.util;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;

/**
 * Helper class for managing ghost items in filter slots.
 * Ghost items track a desired count separately from the actual stack size.
 */
public class GhostItemHelper {

    public static void setItemGhostAmount(ItemStack stack, int amount) {
        stack.set(BMDataComponents.GHOST_STACK_SIZE, amount);
    }

    public static int getItemGhostAmount(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.GHOST_STACK_SIZE, 0);
    }

    public static boolean hasGhostAmount(ItemStack stack) {
        return stack.has(BMDataComponents.GHOST_STACK_SIZE);
    }

    public static void incrementGhostAmount(ItemStack stack, int value) {
        int amount = getItemGhostAmount(stack);
        amount += value;
        setItemGhostAmount(stack, amount);
    }

    public static void decrementGhostAmount(ItemStack stack, int value) {
        int amount = getItemGhostAmount(stack);
        amount -= value;
        setItemGhostAmount(stack, amount);
    }

    public static ItemStack getStackFromGhost(ItemStack ghostStack) {
        ItemStack newStack = ghostStack.copy();
        int amount = getItemGhostAmount(ghostStack);
        newStack.remove(BMDataComponents.GHOST_STACK_SIZE);
        newStack.setCount(amount);
        return newStack;
    }

    public static ItemStack getSingleStackFromGhost(ItemStack ghostStack) {
        ItemStack newStack = ghostStack.copy();
        newStack.remove(BMDataComponents.GHOST_STACK_SIZE);
        newStack.setCount(1);
        return newStack;
    }
}
