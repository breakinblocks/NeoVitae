package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Interface for Items that contain Will (like Monster Souls).
 */
public interface IDemonWill {
    double getWill(EnumWillType type, ItemStack willStack);

    boolean setWill(EnumWillType type, ItemStack willStack, double will);

    /**
     * Drains will from the stack. If all will is drained, the stack should be removed by the caller.
     */
    double drainWill(EnumWillType type, ItemStack willStack, double drainAmount);

    ItemStack createWill(double number);

    EnumWillType getType(ItemStack stack);
}
