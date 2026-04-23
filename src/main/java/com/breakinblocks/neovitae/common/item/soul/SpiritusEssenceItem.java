package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Monster Soul item - dropped by mobs when killed with sentient weapons.
 * Each type corresponds to a spiritus type.
 */
public class SpiritusEssenceItem extends Item implements ISpiritus {

    private final SpiritusType willType;

    public SpiritusEssenceItem(Item.Properties props, SpiritusType willType) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.SPIRITUS_AMOUNT, 0.0)
                .component(NVDataComponents.SPIRITUS_TYPE, willType));
        this.willType = willType;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        double will = getWill(willType, stack);
        if (will > 0) {
            tooltip.accept(Component.translatable("tooltip.neovitae.will", ChatUtil.DECIMAL_FORMAT.format(will))
                    .withStyle(ChatFormatting.GRAY));
        }}

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.SUCCESS.heldItemTransformedTo(stack);

        double will = getWill(willType, stack);
        if (will <= 0) return InteractionResult.FAIL;

        ItemStack remaining = PlayerSpiritusHandler.addSpiritus(player, stack);
        if (remaining.isEmpty() || getWill(willType, remaining) < will) {
            player.setItemInHand(hand, remaining);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
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
