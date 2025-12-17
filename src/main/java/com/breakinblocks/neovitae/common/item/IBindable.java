package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;

import javax.annotation.Nullable;

/**
 * Implement this interface on any Item that can be bound to a player.
 */
public interface IBindable {
    /**
     * Gets an object that stores who this item is bound to.
     * If the item is not bound, this will be null.
     *
     * @param stack - The owned ItemStack
     * @return - The binding object
     */
    @Nullable
    default Binding getBinding(ItemStack stack) {
        if (stack.isEmpty()) return null;
        Binding binding = stack.get(BMDataComponents.BINDING.get());
        return binding != null && !binding.isEmpty() ? binding : null;
    }

    /**
     * Called when the player attempts to bind the item.
     *
     * @param player - The Player attempting to bind the item
     * @param stack  - The ItemStack to attempt binding
     * @return If binding was successful.
     */
    default boolean onBind(Player player, ItemStack stack) {
        return true;
    }

    /**
     * Bind the item to the given player.
     *
     * @param player - The player to bind to
     * @param stack  - The ItemStack to bind
     */
    default void bind(Player player, ItemStack stack) {
        if (onBind(player, stack)) {
            Binding binding = new Binding(player.getUUID(), player.getName().getString());
            stack.set(BMDataComponents.BINDING.get(), binding);
        }
    }
}
