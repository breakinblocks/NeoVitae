package com.breakinblocks.neovitae.common.item.routing;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeStats;

import java.util.List;

public class RoutingUpgradeItem extends Item {

    private static final String TOOLTIP_BASE = "tooltip.neovitae.routing_upgrade.";

    private final boolean speed;

    public RoutingUpgradeItem(boolean speed) {
        super(new Item.Properties());
        this.speed = speed;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RoutingNodeStats stats = RoutingNodeHelper.getMasterStats(NVBlocks.MASTER_ROUTING_NODE.block().get());

        tooltip.add(Component.translatable(TOOLTIP_BASE + "slot").withStyle(ChatFormatting.GRAY));

        if (speed) {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "speed.effect").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(TOOLTIP_BASE + "speed.limits",
                            stats.getBaseTickRate(), stats.getMaxSpeedUpgrades())
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "stack.effect",
                            stats.getItemTransferPerUpgrade(),
                            stats.getFluidTransferPerUpgrade(),
                            stats.getEnergyTransferPerUpgrade())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(TOOLTIP_BASE + "stack.limits",
                            stats.getBaseItemTransfer(),
                            stats.getBaseFluidTransfer(),
                            stats.getBaseEnergyTransfer(),
                            stats.getMaxStackUpgrades())
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
