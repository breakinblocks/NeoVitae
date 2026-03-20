package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Used for all ItemSigils <b>EXCEPT</b> for Sigils of Holding.
 */
public interface ISigil {

    /**
     * Resolves the actual sigil stack from a potentially held-in-Holding stack.
     * If the stack is a Sigil of Holding, extracts the currently selected sigil.
     */
    static ItemStack resolveHeldStack(ItemStack stack, Player player) {
        if (stack.getItem() instanceof Holding holding) {
            return holding.getHeldItem(stack, player);
        }
        return stack;
    }

    /**
     * Resolves the sigil stack for use from the player's hand, handling Sigil of Holding.
     * Returns null if the player is a fake player (automation not allowed).
     */
    @Nullable
    static ItemStack resolveForUse(Player player, InteractionHand hand) {
        ItemStack stack = resolveHeldStack(player.getItemInHand(hand), player);
        return PlayerHelper.isFakePlayer(player) ? null : stack;
    }

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
