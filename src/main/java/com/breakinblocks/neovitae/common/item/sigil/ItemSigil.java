// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.SigilStats;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemSigil extends Item implements IBindable, ISigil {
    private final int defaultLpUsed;

    public ItemSigil(Properties prop, int lpUsed) {
        super(prop);
        this.defaultLpUsed = lpUsed;
    }

    public boolean isUnusable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(NVDataComponents.SIGIL_UNUSABLE.get(), false);
    }

    public ItemStack setUnusable(ItemStack stack, boolean unusable) {
        if (!stack.isEmpty()) {
            stack.set(NVDataComponents.SIGIL_UNUSABLE.get(), unusable);
        }
        return stack;
    }

    /**
     * Gets the EV cost for this sigil, checking the datamap first.
     * Falls back to the default value if not in datamap.
     */
    public int getLpUsed() {
        SigilStats stats = BuiltInRegistries.ITEM.wrapAsHolder(this).getData(NVDataMaps.SIGIL_STATS);
        if (stats != null) {
            return stats.lpCost();
        }
        return defaultLpUsed;
    }

    public SigilStats getSigilStats() {
        return BuiltInRegistries.ITEM.wrapAsHolder(this).getData(NVDataMaps.SIGIL_STATS);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Binding binding = getBinding(stack);
        if (binding != null) {
            tooltip.accept(Component.translatable("tooltip.neovitae.currentOwner", binding.name())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Nullable
    protected ItemStack resolveStackForUse(Player player, InteractionHand hand) {
        return ISigil.resolveForUse(player, hand);
    }
}
