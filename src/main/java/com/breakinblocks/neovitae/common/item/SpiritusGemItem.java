package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.will.ISpiritus;
import com.breakinblocks.neovitae.will.ISpiritusGem;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;
import com.breakinblocks.neovitae.will.SpiritusHelper;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class SpiritusGemItem extends Item implements ISpiritusGem {

    public SpiritusGemItem(Item.Properties props) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.SPIRITUS_AMOUNT, 0.0)
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SpiritusType type = SpiritusHelper.getCurrentType(stack);
        double drain = Math.min(SpiritusHelper.getWill(stack, type), SpiritusHelper.resolveMaxWill(stack, type) / 10.0);

        double filled = PlayerSpiritusHandler.addSpiritus(type, player, drain, stack);
        SpiritusHelper.drainWill(stack, type, filled, true);

        return InteractionResult.PASS;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        SpiritusType type = SpiritusHelper.getCurrentType(stack);
        double amount = SpiritusHelper.getWill(stack, type);
        Identifier loc = stack.typeHolder().getKey().identifier();

        tooltip.accept(Component.translatable("tooltip.neovitae.spiritus_gem." + loc.getPath()).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(amount)).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.current_type." + type.getSerializedName()).withStyle(ChatFormatting.GRAY));}

    @Override
    public ItemStack fillSpiritusGem(ItemStack soulGemStack, ItemStack soulStack) {
        if (soulStack != null && !soulStack.isEmpty() && soulStack.getItem() instanceof ISpiritus soul) {
            SpiritusType thisType = SpiritusHelper.getCurrentType(soulGemStack);
            SpiritusType soulType = soul.getType(soulStack);

            if (thisType != soulType && SpiritusHelper.getWill(soulGemStack, thisType) > 0) {
                return soulStack;
            }

            double soulsLeft = SpiritusHelper.getWill(soulGemStack, thisType);
            double maxWill = SpiritusHelper.resolveMaxWill(soulGemStack, thisType);

            if (soulsLeft < maxWill) {
                double soulWill = soul.getWill(soulType, soulStack);
                double newSoulsLeft = Math.min(soulsLeft + soulWill, maxWill);
                double drained = newSoulsLeft - soulsLeft;

                soul.drainWill(soulType, soulStack, drained);
                SpiritusHelper.setWill(soulGemStack, soulType, newSoulsLeft);

                if (soul.getWill(soulType, soulStack) <= 0) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return soulStack;
    }

    @Override
    public double getWill(SpiritusType type, ItemStack stack) {
        return SpiritusHelper.getWill(stack, type);
    }

    @Override
    public void setWill(SpiritusType type, ItemStack stack, double souls) {
        SpiritusHelper.setWill(stack, type, souls);
    }

    @Override
    public int getMaxWill(SpiritusType type, ItemStack stack) {
        return (int) SpiritusHelper.resolveMaxWill(stack, type);
    }

    @Override
    public double drainWill(SpiritusType type, ItemStack stack, double drainAmount, boolean doDrain) {
        return SpiritusHelper.drainWill(stack, type, drainAmount, doDrain);
    }

    @Override
    public double fillWill(SpiritusType type, ItemStack stack, double fillAmount, boolean doFill) {
        return SpiritusHelper.fillWill(stack, type, fillAmount, doFill);
    }
}
