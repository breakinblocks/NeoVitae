// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ItemBindEvent extends Event implements ICancellableEvent {
    private final Player player;
    private final ItemStack itemStack;

    public ItemBindEvent(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public Player getNewOwner() {
        return player;
    }

    public ItemStack getBindingStack() {
        return itemStack;
    }
}
