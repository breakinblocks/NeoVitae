// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2020-2022 Arcaratus <https://github.com/Arcaratus>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

import javax.annotation.Nonnull;

public interface IActivatable {

    default boolean getActivated(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(NVDataComponents.SIGIL_ACTIVATED.get(), false);
    }

    @Nonnull
    default ItemStack setActivatedState(ItemStack stack, boolean activated) {
        if (!stack.isEmpty()) {
            stack.set(NVDataComponents.SIGIL_ACTIVATED.get(), activated);
        }
        return stack;
    }
}
