package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;

import java.util.List;

public class ArrayEffectItem extends Item {

    private final AlchemyArrayEffectType effectType;

    public ArrayEffectItem(AlchemyArrayEffectType effectType) {
        super(new Properties());
        this.effectType = effectType;
    }

    public AlchemyArrayEffectType getEffectType() {
        return effectType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.array_effect." + effectType.getSerializedName())
                .withStyle(ChatFormatting.GRAY));
    }
}
