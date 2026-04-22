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
    private final SpiritusType willType;
    private final double willPerCrystal;

    public SpiritusCrystalItem(Item.Properties props, SpiritusType willType) {
        this(props, willType, 50.0);
    }

    public SpiritusCrystalItem(Item.Properties props, SpiritusType willType, double willPerCrystal) {
        super(props);
        this.willType = willType;
        this.willPerCrystal = willPerCrystal;
    }

    public SpiritusType getWillType() {
        return willType;
    }

    public double getWill(ItemStack stack) {
        return willPerCrystal * stack.getCount();
    }

    public double getWillPerCrystal() {
        return willPerCrystal;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.current_type." + willType.getSerializedName()).withStyle(ChatFormatting.GRAY));}
}
