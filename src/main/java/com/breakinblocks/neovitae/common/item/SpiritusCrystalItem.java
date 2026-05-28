package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class SpiritusCrystalItem extends Item {
    private final SpiritusType spiritusType;
    private final double spiritusPerCrystal;

    public SpiritusCrystalItem(Item.Properties props, SpiritusType spiritusType) {
        this(props, spiritusType, 50.0);
    }

    public SpiritusCrystalItem(Item.Properties props, SpiritusType spiritusType, double spiritusPerCrystal) {
        super(props);
        this.spiritusType = spiritusType;
        this.spiritusPerCrystal = spiritusPerCrystal;
    }

    public SpiritusType getSpiritusType() {
        return spiritusType;
    }

    public double getSpiritus(ItemStack stack) {
        return spiritusPerCrystal * stack.getCount();
    }

    public double getSpiritusPerCrystal() {
        return spiritusPerCrystal;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.current_type." + spiritusType.getSerializedName()).withStyle(ChatFormatting.GRAY));}
}
