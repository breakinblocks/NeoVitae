package com.breakinblocks.neovitae.spiritus;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

/**
 * Interface for Items that store Spiritus (like Spiritus Gems).
 */
public interface ISpiritusGem {
    /**
     * Absorbs will from a will item stack into this gem.
     * @return The remainder spiritusStack (empty if fully absorbed)
     */
    ItemStack fillSpiritusGem(ItemStack spiritusGemStack, ItemStack spiritusStack);

    double getSpiritus(SpiritusType type, ItemStack spiritusGemStack);

    void setSpiritus(SpiritusType type, ItemStack spiritusGemStack, double amount);

    int getMaxSpiritus(SpiritusType type, ItemStack spiritusGemStack);

    double drainSpiritus(SpiritusType type, ItemStack stack, double drainAmount, boolean doDrain);

    double fillSpiritus(SpiritusType type, ItemStack stack, double fillAmount, boolean doFill);
}
