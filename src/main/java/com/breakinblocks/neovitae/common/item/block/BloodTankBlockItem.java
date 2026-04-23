package com.breakinblocks.neovitae.common.item.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.function.Consumer;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

public class BloodTankBlockItem extends BlockItem {
    public BloodTankBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        int tier = stack.getOrDefault(NVDataComponents.CONTAINER_TIER, 0);
        if (tier == 0) {
            tooltip.accept(Component.translatable("tooltip.neovitae.container_tier_missing")
                    .withStyle(net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD));
        } else {
            tooltip.accept(BlockEntityHelper.translatableHover("tooltip.neovitae.container_tier", tier));
        }

        FluidStack fluidStack = stack.getOrDefault(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        if (fluidStack.isEmpty()) {
            tooltip.accept(BlockEntityHelper.translatableHover("tooltip.neovitae.fluid_content_empty"));
        } else {
            tooltip.accept(BlockEntityHelper.translatableHover("tooltip.neovitae.fluid_content", fluidStack.getAmount(), fluidStack.getHoverName()));
        }
    }
}
