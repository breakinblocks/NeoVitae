package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class RawSpiritusItem extends Item {

    public RawSpiritusItem(Item.Properties props) {
        super(props.stacksTo(1).component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).component(NVDataComponents.SPIRITUS_AMOUNT, 5D));
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SpiritusType type = stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        double amount = stack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);

        tooltipComponents.accept(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(amount)).withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("tooltip.neovitae.current_type." + type.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));}
}