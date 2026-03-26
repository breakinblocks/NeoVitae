package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Interface for Items that store Will (like Spiritus Gems).
 */
public interface ISpiritusGem {
    /**
     * Absorbs will from a will item stack into this gem.
     * @return The remainder willStack (empty if fully absorbed)
     */
    ItemStack fillSpiritusGem(ItemStack willGemStack, ItemStack willStack);

    double getWill(SpiritusType type, ItemStack willGemStack);

    void setWill(SpiritusType type, ItemStack willGemStack, double amount);

    int getMaxWill(SpiritusType type, ItemStack willGemStack);

    double drainWill(SpiritusType type, ItemStack stack, double drainAmount, boolean doDrain);

    double fillWill(SpiritusType type, ItemStack stack, double fillAmount, boolean doFill);
}
