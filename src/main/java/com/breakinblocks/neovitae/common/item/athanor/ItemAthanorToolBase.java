package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemAthanorToolBase extends Item implements IAthanorTool {

    public ItemAthanorToolBase(Item.Properties props, int maxDamage, double craftingMultiplier) {
        this(props, maxDamage, craftingMultiplier, 1, SpiritusType.DEFAULT);
    }

    public ItemAthanorToolBase(Item.Properties props, int maxDamage, double craftingMultiplier, SpiritusType type) {
        this(props, maxDamage, craftingMultiplier, 1, type);
    }

    public ItemAthanorToolBase(Item.Properties props, int maxDamage, double craftingMultiplier, double additionalOutputChance) {
        this(props, maxDamage, craftingMultiplier, additionalOutputChance, SpiritusType.DEFAULT);
    }

    public ItemAthanorToolBase(Item.Properties props, int maxDamage, double craftingMultiplier, double additionalOutputChance, SpiritusType type) {
        super(props
                .stacksTo(1)
                .durability(maxDamage)
                .component(NVDataComponents.ARC_SPEED.get(), craftingMultiplier)
                .component(NVDataComponents.ARC_CHANCE.get(), additionalOutputChance)
                .component(NVDataComponents.SPIRITUS_TYPE.get(), type));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.arctool.uses", stack.getMaxDamage() - stack.getDamageValue()).withStyle(ChatFormatting.GRAY));

        if (getCraftingSpeedMultiplier(stack) != 1)
            tooltip.accept(Component.translatable("tooltip.neovitae.arctool.craftspeed", ChatUtil.DECIMAL_FORMAT.format(getCraftingSpeedMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        if (getAdditionalOutputChanceMultiplier(stack) != 1)
            tooltip.accept(Component.translatable("tooltip.neovitae.arctool.additionaldrops", ChatUtil.DECIMAL_FORMAT.format(getAdditionalOutputChanceMultiplier(stack))).withStyle(ChatFormatting.GRAY));}

    @Override
    public double getCraftingSpeedMultiplier(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.ARC_SPEED.get(), 1.0);
    }

    @Override
    public double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.ARC_CHANCE.get(), 1.0);
    }

    @Override
    public SpiritusType getDominantWillType(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE.get(), SpiritusType.DEFAULT);
    }
}
