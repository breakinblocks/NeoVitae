package com.breakinblocks.neovitae.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.List;

/**
 * Demon Will Aura Gauge - displays a HUD overlay showing will levels in the current chunk.
 * Simply having this item in the inventory enables the HUD display.
 * Also syncs demon will data to the client every 50 ticks (2.5 seconds) like 1.20.1.
 */
public class DemonWillGaugeItem extends Item {

    public DemonWillGaugeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.neovitae.demon_will_gauge"));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Sync demon will to client every 50 ticks (2.5 seconds) like 1.20.1
        if (!level.isClientSide() && entity instanceof Player player && entity.tickCount % 50 == 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                WorldDemonWillHandler.sendPlayerDemonWillAura(serverPlayer);
            }
        }
    }
}
