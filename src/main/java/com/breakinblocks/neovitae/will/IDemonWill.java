package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Interface for Items that contain Will (like Monster Souls)
 */
public interface IDemonWill {
    /**
     * Obtains the amount of Will an ItemStack contains.
     *
     * @param type      - The type of Will to check for
     * @param willStack - The stack to retrieve the Will from
     * @return - The amount of Will an ItemStack contains
     */
    double getWill(EnumWillType type, ItemStack willStack);

    /**
     * Sets the amount of Will in a given ItemStack.
     *
     * @param type      - The type of Will
     * @param willStack - The ItemStack of the Will
     * @param will      - The amount of will to set the stack to
     * @return True if successfully set.
     */
    boolean setWill(EnumWillType type, ItemStack willStack, double will);

    /**
     * Drains the demonic will from the willStack. If all of the will is drained,
     * the willStack will be removed.
     *
     * @param type        - The type of Will
     * @param willStack   - The ItemStack of the will
     * @param drainAmount - The amount of Will to drain
     * @return The amount of will drained.
     */
    double drainWill(EnumWillType type, ItemStack willStack, double drainAmount);

    /**
     * Creates a new ItemStack with the specified number of will. Implementation
     * should respect the number requested.
     *
     * @param number - The amount of Will to create the Stack with.
     * @return - An ItemStack with the set amount of Will
     */
    ItemStack createWill(double number);

    /**
     * Gets the type of will this item contains.
     *
     * @param stack - The ItemStack
     * @return The will type
     */
    EnumWillType getType(ItemStack stack);
}
