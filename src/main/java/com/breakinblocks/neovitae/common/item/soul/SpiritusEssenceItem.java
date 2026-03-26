package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.will.ISpiritus;

import java.util.List;

/**
 * Monster Soul item - dropped by mobs when killed with sentient weapons.
 * Each type corresponds to a spiritus type.
 */
public class SpiritusEssenceItem extends Item implements ISpiritus {

    private final SpiritusType willType;

    public SpiritusEssenceItem(SpiritusType willType) {
        super(new Properties()
                .stacksTo(1)
                .component(NVDataComponents.SPIRITUS_AMOUNT, 0.0)
                .component(NVDataComponents.SPIRITUS_TYPE, willType));
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
    public SpiritusType getType(ItemStack stack) {
        return willType;
    }

    @Override
    public double getWill(SpiritusType type, ItemStack willStack) {
        if (type != willType) {
            return 0;
        }
        return willStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
    }

    @Override
    public boolean setWill(SpiritusType type, ItemStack willStack, double will) {
        if (type != willType) {
            return false;
        }
        willStack.set(NVDataComponents.SPIRITUS_AMOUNT, will);
        return true;
    }

    @Override
    public double drainWill(SpiritusType type, ItemStack willStack, double drainAmount) {
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
