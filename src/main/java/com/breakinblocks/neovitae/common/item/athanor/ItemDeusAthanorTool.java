package com.breakinblocks.neovitae.common.item.athanor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.function.Consumer;

public class ItemDeusAthanorTool extends Item implements IAthanorTool, IBindable {

    public ItemDeusAthanorTool(Item.Properties props, double craftingMultiplier, double additionalOutputChance, SpiritusType type) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.ARC_SPEED.get(), craftingMultiplier)
                .component(NVDataComponents.ARC_CHANCE.get(), additionalOutputChance)
                .component(NVDataComponents.SPIRITUS_TYPE.get(), type)
                .component(NVDataComponents.BINDING.get(), Binding.EMPTY)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    public static int evPerUse() {
        return NeoVitae.SERVER_CONFIG.ATHANOR_DEUS_TOOL_EV_PER_USE.get();
    }

    @Override
    public Readiness getReadiness(ItemStack stack) {
        return IAthanorTool.networkReadiness(stack, evPerUse());
    }

    @Override
    public boolean consumeUse(ItemStack stack) {
        return IAthanorTool.syphonNetwork(stack, evPerUse());
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

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(stack.is(NVTags.Items.CUTTING_FLUIDS)
                ? "tooltip.neovitae.arctool.usage.cutting_fluid"
                : "tooltip.neovitae.arctool.usage").withStyle(ChatFormatting.GRAY));

        tooltip.accept(Component.translatable("tooltip.neovitae.arctool.deus_cost", evPerUse()).withStyle(ChatFormatting.GRAY));

        if (getCraftingSpeedMultiplier(stack) != 1)
            tooltip.accept(Component.translatable("tooltip.neovitae.arctool.craftspeed", ChatUtil.DECIMAL_FORMAT.format(getCraftingSpeedMultiplier(stack))).withStyle(ChatFormatting.GRAY));

        if (getAdditionalOutputChanceMultiplier(stack) != 1)
            tooltip.accept(Component.translatable("tooltip.neovitae.arctool.additionaldrops", ChatUtil.DECIMAL_FORMAT.format(getAdditionalOutputChanceMultiplier(stack))).withStyle(ChatFormatting.GRAY));
    }
}
