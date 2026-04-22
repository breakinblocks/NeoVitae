package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemSigilToggleableBase extends ItemSigilToggleable {
    protected final String tooltipBase;

    public ItemSigilToggleableBase(Item.Properties props, String name, int lpUsed) {
        super(props.stacksTo(1), lpUsed);
        this.tooltipBase = "tooltip.neovitae.sigil." + name + ".";
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {String stateKey = getActivated(stack) ? "tooltip.neovitae.activated" : "tooltip.neovitae.deactivated";
        tooltip.accept(Component.translatable(stateKey).withStyle(ChatFormatting.GRAY));
    }
}
