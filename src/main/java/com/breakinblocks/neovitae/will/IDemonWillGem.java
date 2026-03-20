package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

/**
 * Interface for Items that store Will (like Tartaric Gems).
 */
public interface IDemonWillGem {
    /**
     * Absorbs will from a will item stack into this gem.
     * @return The remainder willStack (empty if fully absorbed)
     */
    ItemStack fillDemonWillGem(ItemStack willGemStack, ItemStack willStack);

    double getWill(EnumWillType type, ItemStack willGemStack);

    void setWill(EnumWillType type, ItemStack willGemStack, double amount);

    int getMaxWill(EnumWillType type, ItemStack willGemStack);

    double drainWill(EnumWillType type, ItemStack stack, double drainAmount, boolean doDrain);

    double fillWill(EnumWillType type, ItemStack stack, double fillAmount, boolean doFill);
}
