package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;

import java.util.function.Consumer;

public class RoutingUpgradeItem extends Item {

    private static final String TOOLTIP_BASE = "tooltip.neovitae.routing_upgrade.";

    private final boolean speed;

    public RoutingUpgradeItem(Item.Properties props, boolean speed) {
        super(props);
        this.speed = speed;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        RoutingNodeStats stats = RoutingNodeHelper.getMasterStats(NVBlocks.MASTER_ROUTING_NODE.block().get());

        tooltip.accept(Component.translatable(TOOLTIP_BASE + "slot").withStyle(ChatFormatting.GRAY));

        if (speed) {
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "speed.effect").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "speed.limits",
                            stats.getBaseTickRate(), stats.getMaxSpeedUpgrades())
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "stack.effect",
                            stats.getItemTransferPerUpgrade(),
                            stats.getFluidTransferPerUpgrade(),
                            stats.getEnergyTransferPerUpgrade())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "stack.limits",
                            stats.getBaseItemTransfer(),
                            stats.getBaseFluidTransfer(),
                            stats.getBaseEnergyTransfer(),
                            stats.getMaxStackUpgrades())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
