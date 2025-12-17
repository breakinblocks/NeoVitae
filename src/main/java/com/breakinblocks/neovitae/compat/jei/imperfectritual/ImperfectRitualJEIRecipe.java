package com.breakinblocks.neovitae.compat.jei.imperfectritual;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Data holder for displaying imperfect rituals in JEI.
 *
 * @param ritualId        The ritual's registry ID
 * @param catalystBlock   The block required above the ritual stone (as ItemStack for display)
 * @param activationCost  LP cost to activate
 * @param description     Description of what the ritual does
 * @param consumesBlock   Whether the catalyst block is consumed
 */
public record ImperfectRitualJEIRecipe(
        ResourceLocation ritualId,
        List<ItemStack> catalystBlock,
        int activationCost,
        Component description,
        boolean consumesBlock
) {
    /**
     * Gets the ritual name component for display.
     */
    public Component getRitualName() {
        return Component.translatable("ritual.neovitae.imperfect." + ritualId.getPath());
    }
}
