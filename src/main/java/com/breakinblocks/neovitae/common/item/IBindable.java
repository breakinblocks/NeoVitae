// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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
        Binding binding = stack.getOrDefault(NVDataComponents.BINDING.get(), Binding.EMPTY);
        return binding.isEmpty() ? null : binding;
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
