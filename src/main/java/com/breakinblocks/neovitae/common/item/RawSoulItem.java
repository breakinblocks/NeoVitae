package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.List;
import java.util.Locale;

public class RawSoulItem extends Item {

    public RawSoulItem() {
        super(new Properties().stacksTo(1).component(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).component(BMDataComponents.DEMON_WILL_AMOUNT, 5D));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        EnumWillType type = stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT);
        double amount = stack.getOrDefault(BMDataComponents.DEMON_WILL_AMOUNT, 0D);

        tooltipComponents.add(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(amount)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.neovitae.current_type." + type.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}