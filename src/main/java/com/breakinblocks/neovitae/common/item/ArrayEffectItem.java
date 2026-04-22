package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ArrayEffectItem extends Item {

    private final AlchemyArrayEffectType effectType;

    public ArrayEffectItem(Item.Properties props, AlchemyArrayEffectType effectType) {
        super(props);
        this.effectType = effectType;
    }

    public AlchemyArrayEffectType getEffectType() {
        return effectType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.array_effect." + effectType.getSerializedName())
                .withStyle(ChatFormatting.GRAY));
    }
}
