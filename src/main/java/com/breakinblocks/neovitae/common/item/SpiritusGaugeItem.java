package com.breakinblocks.neovitae.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class SpiritusGaugeItem extends Item {

    public SpiritusGaugeItem(Item.Properties props) {
        super(props.stacksTo(1));
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {tooltip.accept(Component.translatable("tooltip.neovitae.spiritus_gauge"));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof ServerPlayer serverPlayer && entity.tickCount % 50 == 0) {
            WorldSpiritusHandler.sendPlayerSpiritusAura(serverPlayer);
        }
    }
}
