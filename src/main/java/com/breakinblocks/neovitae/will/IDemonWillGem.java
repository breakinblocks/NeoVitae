package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Interface for Items that store Will (like Tartaric Gems)
 */
public interface IDemonWillGem {
    /**
     * Fills the demon will gem from a will item stack.
     *
     * @param willGemStack - The ItemStack for this demon will gem.
     * @param willStack    - The ItemStack for the will. Item should implement IDemonWill
     * @return - The remainder willStack after the will has been absorbed into the
     *         gem. Return empty stack if there is no will left in the stack.
     */
    ItemStack fillDemonWillGem(ItemStack willGemStack, ItemStack willStack);

    /**
     * Returns the number of souls that are left in the soul gem. Returns a double
     * because souls can be fractionally drained.
     *
     * @param type         - The type of Will
     * @param willGemStack - The gem ItemStack
     * @return The amount of will stored
     */
    double getWill(EnumWillType type, ItemStack willGemStack);

    /**
     * Sets the amount of will in the gem.
     *
     * @param type         - The type of Will
     * @param willGemStack - The gem ItemStack
     * @param amount       - The amount to set
     */
    void setWill(EnumWillType type, ItemStack willGemStack, double amount);

    /**
     * Gets the maximum will capacity for this gem.
     *
     * @param type         - The type of Will
     * @param willGemStack - The gem ItemStack
     * @return The maximum will capacity
     */
    int getMaxWill(EnumWillType type, ItemStack willGemStack);

    /**
     * Drains will from the gem.
     *
     * @param type        - The type of Will
     * @param stack       - The gem ItemStack
     * @param drainAmount - The amount to drain
     * @param doDrain     - If true, actually drain. If false, simulate.
     * @return The amount drained (or that would be drained)
     */
    double drainWill(EnumWillType type, ItemStack stack, double drainAmount, boolean doDrain);

    /**
     * Fills the gem with will.
     *
     * @param type       - The type of Will
     * @param stack      - The gem ItemStack
     * @param fillAmount - The amount to fill
     * @param doFill     - If true, actually fill. If false, simulate.
     * @return The amount filled (or that would be filled)
     */
    double fillWill(EnumWillType type, ItemStack stack, double fillAmount, boolean doFill);
}
