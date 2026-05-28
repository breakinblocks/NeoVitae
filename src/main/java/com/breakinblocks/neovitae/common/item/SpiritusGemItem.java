package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.spiritus.ISpiritus;
import com.breakinblocks.neovitae.spiritus.ISpiritusGem;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;

import java.util.List;

public class SpiritusGemItem extends Item implements ISpiritusGem {

    public SpiritusGemItem() {
        super(new Properties()
                .stacksTo(1)
                .component(NVDataComponents.SPIRITUS_AMOUNT, 0.0)
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        SpiritusType type = SpiritusHelper.getCurrentType(stack);
        double drain = Math.min(SpiritusHelper.getSpiritus(stack, type), SpiritusHelper.resolveMaxSpiritus(stack, type) / 10.0);

        double filled = PlayerSpiritusHandler.addSpiritus(type, player, drain, stack);
        SpiritusHelper.drainSpiritus(stack, type, filled, true);

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        SpiritusType type = SpiritusHelper.getCurrentType(stack);
        double amount = SpiritusHelper.getSpiritus(stack, type);
        ResourceLocation loc = stack.getItemHolder().getKey().location();

        tooltip.add(Component.translatable("tooltip.neovitae.spiritus_gem." + loc.getPath()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(amount)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.current_type." + type.getSerializedName()).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }

    @Override
    public ItemStack fillSpiritusGem(ItemStack soulGemStack, ItemStack soulStack) {
        if (soulStack != null && !soulStack.isEmpty() && soulStack.getItem() instanceof ISpiritus soul) {
            SpiritusType thisType = SpiritusHelper.getCurrentType(soulGemStack);
            SpiritusType soulType = soul.getType(soulStack);

            if (thisType != soulType && SpiritusHelper.getSpiritus(soulGemStack, thisType) > 0) {
                return soulStack;
            }

            double soulsLeft = SpiritusHelper.getSpiritus(soulGemStack, thisType);
            double maxSpiritus = SpiritusHelper.resolveMaxSpiritus(soulGemStack, thisType);

            if (soulsLeft < maxSpiritus) {
                double soulSpiritus = soul.getSpiritus(soulType, soulStack);
                double newSoulsLeft = Math.min(soulsLeft + soulSpiritus, maxSpiritus);
                double drained = newSoulsLeft - soulsLeft;

                soul.drainSpiritus(soulType, soulStack, drained);
                SpiritusHelper.setSpiritus(soulGemStack, soulType, newSoulsLeft);

                if (soul.getSpiritus(soulType, soulStack) <= 0) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return soulStack;
    }

    @Override
    public double getSpiritus(SpiritusType type, ItemStack stack) {
        return SpiritusHelper.getSpiritus(stack, type);
    }

    @Override
    public void setSpiritus(SpiritusType type, ItemStack stack, double souls) {
        SpiritusHelper.setSpiritus(stack, type, souls);
    }

    @Override
    public int getMaxSpiritus(SpiritusType type, ItemStack stack) {
        return (int) SpiritusHelper.resolveMaxSpiritus(stack, type);
    }

    @Override
    public double drainSpiritus(SpiritusType type, ItemStack stack, double drainAmount, boolean doDrain) {
        return SpiritusHelper.drainSpiritus(stack, type, drainAmount, doDrain);
    }

    @Override
    public double fillSpiritus(SpiritusType type, ItemStack stack, double fillAmount, boolean doFill) {
        return SpiritusHelper.fillSpiritus(stack, type, fillAmount, doFill);
    }
}
