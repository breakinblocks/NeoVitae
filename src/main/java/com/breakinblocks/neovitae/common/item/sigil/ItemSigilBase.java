// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemSigilBase extends ItemSigil {
    protected final String tooltipBase;

    public ItemSigilBase(String name, int lpUsed) {
        super(new Item.Properties().stacksTo(1), lpUsed);
        this.tooltipBase = "tooltip.neovitae.sigil." + name + ".";
    }

    public ItemSigilBase(String name) {
        this(name, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipBase + "desc")
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
