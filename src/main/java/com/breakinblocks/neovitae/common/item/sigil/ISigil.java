package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * Used for all ItemSigils <b>EXCEPT</b> for Sigils of Holding.
 */
public interface ISigil {

    /**
     * Called when the sigil is used within an alchemy array.
     *
     * @param world The world
     * @param pos   The position of the array
     * @return Whether the effect was performed
     */
    default boolean performArrayEffect(Level world, BlockPos pos) {
        return false;
    }

    /**
     * @return Whether this sigil has an array effect
     */
    default boolean hasArrayEffect() {
        return false;
    }

    /**
     * Interface for sigils that can hold other sigils (Sigil of Holding).
     */
    interface Holding {
        @Nonnull
        ItemStack getHeldItem(ItemStack holdingStack, Player player);
    }
}
