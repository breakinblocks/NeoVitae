package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.List;

public class ItemAthanorToolBase extends Item implements IAthanorTool {

    public ItemAthanorToolBase(int maxDamage, double craftingMultiplier) {
        this(maxDamage, craftingMultiplier, 1, SpiritusType.RAW);
    }

    public ItemAthanorToolBase(int maxDamage, double craftingMultiplier, SpiritusType type) {
        this(maxDamage, craftingMultiplier, 1, type);
    }

    public ItemAthanorToolBase(int maxDamage, double craftingMultiplier, double additionalOutputChance) {
        this(maxDamage, craftingMultiplier, additionalOutputChance, SpiritusType.RAW);
    }

    public ItemAthanorToolBase(int maxDamage, double craftingMultiplier, double additionalOutputChance, SpiritusType type) {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(maxDamage)
                .component(NVDataComponents.ARC_SPEED.get(), craftingMultiplier)
                .component(NVDataComponents.ARC_CHANCE.get(), additionalOutputChance)
                .component(NVDataComponents.SPIRITUS_TYPE.get(), type));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(stack.is(NVTags.Items.CUTTING_FLUIDS)
                ? "tooltip.neovitae.arctool.usage.cutting_fluid"
                : "tooltip.neovitae.arctool.usage").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("tooltip.neovitae.arctool.uses", stack.getMaxDamage() - stack.getDamageValue()).withStyle(ChatFormatting.GRAY));

        if (getCraftingSpeedMultiplier(stack) != 1)
            tooltip.add(Component.translatable("tooltip.neovitae.arctool.craftspeed", ChatUtil.DECIMAL_FORMAT.format(getCraftingSpeedMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        if (getAdditionalOutputChanceMultiplier(stack) != 1)
            tooltip.add(Component.translatable("tooltip.neovitae.arctool.additionaldrops", ChatUtil.DECIMAL_FORMAT.format(getAdditionalOutputChanceMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public double getCraftingSpeedMultiplier(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.ARC_SPEED.get(), 1.0);
    }

    @Override
    public double getAdditionalOutputChanceMultiplier(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.ARC_CHANCE.get(), 1.0);
    }

    @Override
    public SpiritusType getDominantSpiritusType(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE.get(), SpiritusType.RAW);
    }
}
