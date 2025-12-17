package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.will.IDemonWill;
import com.breakinblocks.neovitae.will.IDemonWillGem;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

import java.util.List;

public class SoulGemItem extends Item implements IDemonWillGem {

    public SoulGemItem() {
        super(new Properties()
                .stacksTo(1)
                .component(BMDataComponents.DEMON_WILL_AMOUNT, 0.0)
                .component(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        EnumWillType type = getCurrentType(stack);
        double drain = Math.min(getWill(type, stack), getMaxWill(type, stack) / 10.0);

        double filled = PlayerDemonWillHandler.addDemonWill(type, player, drain, stack);
        drainWill(type, stack, filled, true);

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getItemHolder().getData(BMDataMaps.TARTARIC_GEM_MAX_AMOUNTS) != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        EnumWillType type = getCurrentType(stack);
        double currentWill = getWill(type, stack);
        double maxWill = getMaxWill(type, stack);
        if (maxWill <= 0) {
            return 0;
        }
        return (int) (currentWill / maxWill * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        EnumWillType type = getCurrentType(stack);
        double currentWill = getWill(type, stack);
        double maxWill = getMaxWill(type, stack);
        if (maxWill <= 0) {
            return 1;
        }
        return Mth.hsvToRgb(Math.max(0.0F, (float) (currentWill / maxWill)) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        EnumWillType type = getCurrentType(stack);
        double amount = getWill(type, stack);
        ResourceLocation loc = stack.getItemHolder().getKey().location();

        tooltip.add(Component.translatable("tooltip.neovitae.soul_gem." + loc.getPath()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(amount)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.current_type." + type.getSerializedName()).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }

    // IDemonWillGem implementation

    public EnumWillType getCurrentType(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT);
    }

    public void setCurrentType(EnumWillType type, ItemStack stack) {
        stack.set(BMDataComponents.DEMON_WILL_TYPE, type);
    }

    @Override
    public ItemStack fillDemonWillGem(ItemStack soulGemStack, ItemStack soulStack) {
        if (soulStack != null && !soulStack.isEmpty() && soulStack.getItem() instanceof IDemonWill soul) {
            EnumWillType thisType = getCurrentType(soulGemStack);
            EnumWillType soulType = soul.getType(soulStack);

            // If types don't match and we already have will, reject
            if (thisType != soulType && getWill(thisType, soulGemStack) > 0) {
                return soulStack;
            }

            double soulsLeft = getWill(thisType, soulGemStack);
            int maxWill = getMaxWill(thisType, soulGemStack);

            if (soulsLeft < maxWill) {
                double soulWill = soul.getWill(soulType, soulStack);
                double newSoulsLeft = Math.min(soulsLeft + soulWill, maxWill);
                double drained = newSoulsLeft - soulsLeft;

                soul.drainWill(soulType, soulStack, drained);
                setWill(soulType, soulGemStack, newSoulsLeft);

                if (soul.getWill(soulType, soulStack) <= 0) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return soulStack;
    }

    @Override
    public double getWill(EnumWillType type, ItemStack soulGemStack) {
        EnumWillType currentType = getCurrentType(soulGemStack);
        // Only return will if the type matches exactly
        // A DEFAULT gem only has DEFAULT will, not other types
        if (!type.equals(currentType)) {
            return 0;
        }
        return soulGemStack.getOrDefault(BMDataComponents.DEMON_WILL_AMOUNT, 0.0);
    }

    @Override
    public void setWill(EnumWillType type, ItemStack soulGemStack, double souls) {
        setCurrentType(type, soulGemStack);
        soulGemStack.set(BMDataComponents.DEMON_WILL_AMOUNT, souls);
    }

    @Override
    public int getMaxWill(EnumWillType type, ItemStack soulGemStack) {
        EnumWillType currentType = getCurrentType(soulGemStack);
        // If gem has will stored, only allow max for that type
        // If gem is empty (no will), allow any type
        double currentWill = soulGemStack.getOrDefault(BMDataComponents.DEMON_WILL_AMOUNT, 0.0);
        if (currentWill > 0 && !type.equals(currentType)) {
            return 0;
        }

        Holder<Item> holder = soulGemStack.getItemHolder();
        Double maxWill = holder.getData(BMDataMaps.TARTARIC_GEM_MAX_AMOUNTS);
        return maxWill != null ? maxWill.intValue() : 0;
    }

    @Override
    public double drainWill(EnumWillType type, ItemStack soulGemStack, double drainAmount, boolean doDrain) {
        EnumWillType currentType = getCurrentType(soulGemStack);
        // Only allow draining if the type matches exactly
        // A DEFAULT gem can only be drained as DEFAULT, not as other types
        if (currentType != type) {
            return 0;
        }

        double souls = getWill(type, soulGemStack);
        double soulsDrained = Math.min(drainAmount, souls);

        if (doDrain) {
            // Use currentType to preserve the gem's type
            setWill(currentType, soulGemStack, souls - soulsDrained);
        }

        return soulsDrained;
    }

    @Override
    public double fillWill(EnumWillType type, ItemStack stack, double fillAmount, boolean doFill) {
        EnumWillType currentType = getCurrentType(stack);
        double currentWill = stack.getOrDefault(BMDataComponents.DEMON_WILL_AMOUNT, 0.0);

        // If gem has will stored, only allow filling with that same type
        if (currentWill > 0 && !type.equals(currentType)) {
            return 0;
        }

        double maxWill = getMaxWill(type, stack);
        double filled = Math.min(fillAmount, maxWill - currentWill);

        if (doFill && filled > 0) {
            // IMPORTANT: When gem has will, preserve current type; otherwise use the new type
            EnumWillType typeToSet = currentWill > 0 ? currentType : type;
            setWill(typeToSet, stack, filled + currentWill);
        }

        return filled;
    }
}
