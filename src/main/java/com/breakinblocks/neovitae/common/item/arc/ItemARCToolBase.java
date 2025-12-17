package com.breakinblocks.neovitae.common.item.arc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.List;

public class ItemARCToolBase extends Item implements IARCTool {

    public ItemARCToolBase(int maxDamage, double craftingMultiplier) {
        this(maxDamage, craftingMultiplier, 1, EnumWillType.DEFAULT);
    }

    public ItemARCToolBase(int maxDamage, double craftingMultiplier, EnumWillType type) {
        this(maxDamage, craftingMultiplier, 1, type);
    }

    public ItemARCToolBase(int maxDamage, double craftingMultiplier, double additionalOutputChance) {
        this(maxDamage, craftingMultiplier, additionalOutputChance, EnumWillType.DEFAULT);
    }

    public ItemARCToolBase(int maxDamage, double craftingMultiplier, double additionalOutputChance, EnumWillType type) {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(maxDamage)
                .component(BMDataComponents.ARC_SPEED.get(), craftingMultiplier)
                .component(BMDataComponents.ARC_CHANCE.get(), additionalOutputChance)
                .component(BMDataComponents.DEMON_WILL_TYPE.get(), type));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.arctool.uses", stack.getMaxDamage() - stack.getDamageValue()).withStyle(ChatFormatting.GRAY));

        if (getCraftingSpeedMultiplier(stack) != 1)
            tooltip.add(Component.translatable("tooltip.neovitae.arctool.craftspeed", ChatUtil.DECIMAL_FORMAT.format(getCraftingSpeedMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        if (getAdditionalOutputChanceMultiplier(stack) != 1)
            tooltip.add(Component.translatable("tooltip.neovitae.arctool.additionaldrops", ChatUtil.DECIMAL_FORMAT.format(getAdditionalOutputChanceMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public double getCraftingSpeedMultiplier(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.ARC_SPEED.get(), 1.0);
    }

    @Override
    public double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.ARC_CHANCE.get(), 1.0);
    }

    @Override
    public EnumWillType getDominantWillType(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE.get(), EnumWillType.DEFAULT);
    }
}
