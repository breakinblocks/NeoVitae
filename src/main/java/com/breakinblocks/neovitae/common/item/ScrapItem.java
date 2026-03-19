package com.breakinblocks.neovitae.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

import java.util.List;

public class ScrapItem extends Item {
    public ScrapItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int scrap = stack.getOrDefault(NVDataComponents.UPGRADE_SCRAP, 0);
        tooltipComponents.add(Component.translatable("tooltip.neovitae.scrap", scrap));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
