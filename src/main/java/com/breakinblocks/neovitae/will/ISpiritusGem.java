package com.breakinblocks.neovitae.will;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Interface for Items that store Will (like Spiritus Gems).
 */
public interface ISpiritusGem {
    /**
     * Absorbs will from a will item stack into this gem.
     * @return The remainder spiritusStack (empty if fully absorbed)
     */
    ItemStack fillSpiritusGem(ItemStack willGemStack, ItemStack spiritusStack);

    double getSpiritus(SpiritusType type, ItemStack willGemStack);

    void setSpiritus(SpiritusType type, ItemStack willGemStack, double amount);

    int getMaxSpiritus(SpiritusType type, ItemStack willGemStack);

    double drainSpiritus(SpiritusType type, ItemStack stack, double drainAmount, boolean doDrain);

    double fillSpiritus(SpiritusType type, ItemStack stack, double fillAmount, boolean doFill);
}
