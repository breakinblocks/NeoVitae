package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;

import javax.annotation.Nullable;

public interface IBindable {
    @Nullable
    default Binding getBinding(ItemStack stack) {
        if (stack.isEmpty()) return null;
        Binding binding = stack.get(NVDataComponents.BINDING.get());
        return binding != null && !binding.isEmpty() ? binding : null;
    }

    default boolean onBind(Player player, ItemStack stack) {
        return true;
    }

    default void bind(Player player, ItemStack stack) {
        if (onBind(player, stack)) {
            Binding binding = new Binding(player.getUUID(), player.getName().getString());
            stack.set(NVDataComponents.BINDING.get(), binding);
        }
    }
}
