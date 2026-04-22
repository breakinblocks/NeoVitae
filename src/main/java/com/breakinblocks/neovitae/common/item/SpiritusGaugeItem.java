package com.breakinblocks.neovitae.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class SpiritusGaugeItem extends Item {

    public SpiritusGaugeItem(Item.Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {tooltip.accept(Component.translatable("tooltip.neovitae.spiritus_gauge"));
    }

    // @Override (removed: not an override in 26.1)
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player && entity.tickCount % 50 == 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                WorldSpiritusHandler.sendPlayerSpiritusAura(serverPlayer);
            }
        }
    }
}
