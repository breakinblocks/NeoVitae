package com.breakinblocks.neovitae.spiritus;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Interface for Items that contain Spiritus (like Spiritus (e.g. Monster Souls)).
 */
public interface ISpiritus {
    double getSpiritus(SpiritusType type, ItemStack spiritusStack);

    boolean setSpiritus(SpiritusType type, ItemStack spiritusStack, double will);

    /**
     * Drains will from the stack. If all will is drained, the stack should be removed by the caller.
     */
    double drainSpiritus(SpiritusType type, ItemStack spiritusStack, double drainAmount);

    ItemStack createSpiritus(double number);

    SpiritusType getType(ItemStack stack);
}
