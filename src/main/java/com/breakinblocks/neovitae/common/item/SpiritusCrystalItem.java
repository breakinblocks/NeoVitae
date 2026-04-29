package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.List;

public class SpiritusCrystalItem extends Item {
    private final SpiritusType spiritusType;
    private final double willPerCrystal;

    public SpiritusCrystalItem(SpiritusType spiritusType) {
        this(spiritusType, 50.0);
    }

    public SpiritusCrystalItem(SpiritusType spiritusType, double willPerCrystal) {
        super(new Properties());
        this.spiritusType = spiritusType;
        this.willPerCrystal = willPerCrystal;
    }

    public SpiritusType getWillType() {
        return spiritusType;
    }

    public double getSpiritus(ItemStack stack) {
        return willPerCrystal * stack.getCount();
    }

    public double getWillPerCrystal() {
        return willPerCrystal;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.current_type." + spiritusType.getSerializedName()).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
