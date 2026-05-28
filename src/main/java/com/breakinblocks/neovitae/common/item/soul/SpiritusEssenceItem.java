package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
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
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;

import java.util.List;

/**
 * Monster Soul item - dropped by mobs when killed with sentient weapons.
 * Each type corresponds to a spiritus type.
 */
public class SpiritusEssenceItem extends Item implements ISpiritus {

    private final SpiritusType spiritusType;

    public SpiritusEssenceItem(SpiritusType spiritusType) {
        super(new Properties()
                .stacksTo(1)
                .component(NVDataComponents.SPIRITUS_AMOUNT, 0.0)
                .component(NVDataComponents.SPIRITUS_TYPE, spiritusType));
        this.spiritusType = spiritusType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        double will = getSpiritus(spiritusType, stack);
        if (will > 0) {
            tooltip.add(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(will))
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        double will = getSpiritus(spiritusType, stack);
        if (will <= 0) return InteractionResultHolder.fail(stack);

        ItemStack remaining = PlayerSpiritusHandler.addSpiritus(player, stack);
        if (remaining.isEmpty() || getSpiritus(spiritusType, remaining) < will) {
            player.setItemInHand(hand, remaining);
            return InteractionResultHolder.consume(remaining);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public SpiritusType getType(ItemStack stack) {
        return spiritusType;
    }

    @Override
    public double getSpiritus(SpiritusType type, ItemStack spiritusStack) {
        if (type != spiritusType) {
            return 0;
        }
        return spiritusStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
    }

    @Override
    public boolean setSpiritus(SpiritusType type, ItemStack spiritusStack, double will) {
        if (type != spiritusType) {
            return false;
        }
        spiritusStack.set(NVDataComponents.SPIRITUS_AMOUNT, will);
        return true;
    }

    @Override
    public double drainSpiritus(SpiritusType type, ItemStack spiritusStack, double drainAmount) {
        double souls = getSpiritus(type, spiritusStack);
        double soulsDrained = Math.min(drainAmount, souls);
        setSpiritus(type, spiritusStack, souls - soulsDrained);
        return soulsDrained;
    }

    @Override
    public ItemStack createSpiritus(double number) {
        ItemStack soulStack = new ItemStack(this);
        setSpiritus(spiritusType, soulStack, number);
        return soulStack;
    }
}
