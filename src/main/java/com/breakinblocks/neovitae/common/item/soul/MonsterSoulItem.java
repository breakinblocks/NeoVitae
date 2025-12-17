package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.will.IDemonWill;

import java.util.List;

/**
 * Monster Soul item - dropped by mobs when killed with sentient weapons.
 * Each type corresponds to a demon will type.
 */
public class MonsterSoulItem extends Item implements IDemonWill {

    private final EnumWillType willType;

    public MonsterSoulItem(EnumWillType willType) {
        super(new Properties()
                .stacksTo(1)
                .component(BMDataComponents.DEMON_WILL_AMOUNT, 0.0)
                .component(BMDataComponents.DEMON_WILL_TYPE, willType));
        this.willType = willType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        double will = getWill(willType, stack);
        if (will > 0) {
            tooltip.add(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(will))
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public EnumWillType getType(ItemStack stack) {
        return willType;
    }

    @Override
    public double getWill(EnumWillType type, ItemStack willStack) {
        if (type != willType) {
            return 0;
        }
        return willStack.getOrDefault(BMDataComponents.DEMON_WILL_AMOUNT, 0.0);
    }

    @Override
    public boolean setWill(EnumWillType type, ItemStack willStack, double will) {
        if (type != willType) {
            return false;
        }
        willStack.set(BMDataComponents.DEMON_WILL_AMOUNT, will);
        return true;
    }

    @Override
    public double drainWill(EnumWillType type, ItemStack willStack, double drainAmount) {
        double souls = getWill(type, willStack);
        double soulsDrained = Math.min(drainAmount, souls);
        setWill(type, willStack, souls - soulsDrained);
        return soulsDrained;
    }

    @Override
    public ItemStack createWill(double number) {
        ItemStack soulStack = new ItemStack(this);
        setWill(willType, soulStack, number);
        return soulStack;
    }
}
