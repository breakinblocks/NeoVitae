package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;

import javax.annotation.Nonnull;

/**
 * Interface for activatable Items (toggleable sigils, etc.)
 */
public interface IActivatable {

    default boolean getActivated(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(BMDataComponents.SIGIL_ACTIVATED.get(), false);
    }

    @Nonnull
    default ItemStack setActivatedState(ItemStack stack, boolean activated) {
        if (!stack.isEmpty()) {
            stack.set(BMDataComponents.SIGIL_ACTIVATED.get(), activated);
        }
        return stack;
    }
}
