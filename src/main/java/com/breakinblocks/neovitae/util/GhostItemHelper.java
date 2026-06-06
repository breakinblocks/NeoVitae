// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.util;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public class GhostItemHelper {

    public static void setItemGhostAmount(ItemStack stack, int amount) {
        stack.set(NVDataComponents.GHOST_STACK_SIZE, amount);
    }

    public static int getItemGhostAmount(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.GHOST_STACK_SIZE, 0);
    }

    public static boolean hasGhostAmount(ItemStack stack) {
        return stack.has(NVDataComponents.GHOST_STACK_SIZE);
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
        newStack.remove(NVDataComponents.GHOST_STACK_SIZE);
        newStack.setCount(amount);
        return newStack;
    }

    public static ItemStack getSingleStackFromGhost(ItemStack ghostStack) {
        ItemStack newStack = ghostStack.copy();
        newStack.remove(NVDataComponents.GHOST_STACK_SIZE);
        newStack.setCount(1);
        return newStack;
    }
}
