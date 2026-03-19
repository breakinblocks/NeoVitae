package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

import javax.annotation.Nonnull;

/**
 * Interface for activatable Items (toggleable sigils, etc.)
 */
public interface IActivatable {

    default boolean getActivated(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(NVDataComponents.SIGIL_ACTIVATED.get(), false);
    }

    @Nonnull
    default ItemStack setActivatedState(ItemStack stack, boolean activated) {
        if (!stack.isEmpty()) {
            stack.set(NVDataComponents.SIGIL_ACTIVATED.get(), activated);
        }
        return stack;
    }
}
