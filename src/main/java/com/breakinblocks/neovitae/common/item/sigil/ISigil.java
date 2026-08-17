// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
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
     * Drops a binding that names the fake player now holding the sigil. Legacy stacks bound
     * before automation was blocked would otherwise own a network that can never be drained.
     */
    static void revokeFakeBinding(ItemStack stack, @Nullable Player player) {
        if (stack == null || stack.isEmpty() || player == null || !PlayerHelper.isFakePlayer(player)) return;
        Binding binding = stack.get(NVDataComponents.BINDING.get());
        if (binding != null && player.getUUID().equals(binding.uuid())) {
            stack.remove(NVDataComponents.BINDING.get());
        }
    }

    /**
     * A fake player pays only from the bound network; it cannot bleed for the shortfall,
     * so it must be refused outright once the owner's network runs dry.
     */
    static boolean canPayFor(@Nullable Binding binding, @Nullable Player player, int cost) {
        if (cost <= 0 || player == null || !PlayerHelper.isFakePlayer(player)) return true;
        if (binding == null) return false;
        Anima network = AnimaHelper.getAnima(binding);
        return network != null && network.getCurrentEV() >= cost;
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
