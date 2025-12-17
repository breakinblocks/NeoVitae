package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Base class for toggleable sigils with tooltip support.
 */
public class ItemSigilToggleableBase extends ItemSigilToggleable {
    protected final String tooltipBase;

    public ItemSigilToggleableBase(String name, int lpUsed) {
        super(new Item.Properties().stacksTo(1), lpUsed);
        this.tooltipBase = "tooltip.neovitae.sigil." + name + ".";
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        String stateKey = getActivated(stack) ? "tooltip.neovitae.activated" : "tooltip.neovitae.deactivated";
        tooltip.add(Component.translatable(stateKey).withStyle(ChatFormatting.GRAY));
    }
}
