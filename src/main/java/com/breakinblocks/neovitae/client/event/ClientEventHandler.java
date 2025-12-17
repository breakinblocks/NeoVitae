package com.breakinblocks.neovitae.client.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.network.BMPayloads;
import com.breakinblocks.neovitae.common.network.RitualDivinerCyclePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class ClientEventHandler {

    /**
     * Handles left-click in air with ritual diviner to cycle rituals backwards.
     * This event only fires on the client, so we send a packet to the server.
     *
     * Control scheme (matching 1.20.1):
     * - Left-click in air: cycle rituals backwards
     * - Shift+Right-click in air: cycle rituals forward
     * - Right-click in air: cycle direction
     */
    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemRitualDiviner) {
            // Send packet to server to cycle ritual backwards (reverse=true)
            BMPayloads.sendToServer(new RitualDivinerCyclePayload(true));
        }
    }

    @SubscribeEvent
    public static void onHoverText(ItemTooltipEvent event) {
        List<Component> tooltip = event.getToolTip();

        ItemStack stack = event.getItemStack();
        Item.TooltipContext context = event.getContext();
        TooltipFlag flags = event.getFlags();
        List<Component> toAdd = new ArrayList<>();

        addToTooltip(BMDataComponents.BINDING.get(), context, toAdd::add, flags, stack);
        int max = stack.getOrDefault(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS, 0);
        if (max > 0) {
            int current = stack.getOrDefault(BMDataComponents.CURRENT_UPGRADE_POINTS, 0);
            toAdd.add(Component.translatable("tooltip.neovitae.upgrade_points", current, max).withStyle(ChatFormatting.GOLD));
        }
        addToTooltip(BMDataComponents.UPGRADES.get(), context, toAdd::add, flags, stack);

        // add after name. idgaf
        tooltip.addAll(1, toAdd);
    }


    public static <T extends TooltipProvider> void addToTooltip(
            DataComponentType<T> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, ItemStack stack
    ) {
        T tooltipProvider = stack.get(component);
        if (tooltipProvider != null) {
            tooltipProvider.addToTooltip(context, tooltipAdder, tooltipFlag);
        }
    }
}
