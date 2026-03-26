package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Interface for Items that contain Will (like Monster Souls).
 */
public interface ISpiritus {
    double getWill(SpiritusType type, ItemStack willStack);

    boolean setWill(SpiritusType type, ItemStack willStack, double will);

    /**
     * Drains will from the stack. If all will is drained, the stack should be removed by the caller.
     */
    double drainWill(SpiritusType type, ItemStack willStack, double drainAmount);

    ItemStack createWill(double number);

    SpiritusType getType(ItemStack stack);
}
