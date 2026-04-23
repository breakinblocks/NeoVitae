package com.breakinblocks.neovitae.client.event;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.client.ClientHandler;
import com.breakinblocks.neovitae.client.ClientSpiritusCache;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.network.BloodLightCyclePayload;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.RitualDivinerCyclePayload;
import com.breakinblocks.neovitae.common.network.SigilHoldingCyclePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSpiritusCache.clear();
        ClientHandler.setRitualHoloToNull();
        ClientHandler.setRitualRangeHoloToNull();
        LoopSoundManager.clear();
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemRitualDiviner) {
            NVPayloads.sendToServer(new RitualDivinerCyclePayload(true));
        } else if (stack.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
            LocalPlayer player = Minecraft.getInstance().player;
            boolean reverse = player != null && player.isShiftKeyDown();
            NVPayloads.sendToServer(new BloodLightCyclePayload(reverse));
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || event.getScrollDeltaY() == 0 || !player.isShiftKeyDown()) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSigilHolding)) {
            return;
        }

        int direction = event.getScrollDeltaY() > 0 ? 1 : -1;

        ItemSigilHolding.cycleToNextSigil(stack, direction);

        ItemStack newSelected = ItemSigilHolding.getInternalInventory(stack)
                .get(ItemSigilHolding.getCurrentItemOrdinal(stack));
        player.sendOverlayMessage(
                newSelected.isEmpty() ? Component.literal("") : newSelected.getHoverName());

        NVPayloads.sendToServer(new SigilHoldingCyclePayload(player.getInventory().getSelectedSlot(), direction));

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onHoverText(ItemTooltipEvent event) {
        List<Component> tooltip = event.getToolTip();

        ItemStack stack = event.getItemStack();
        Item.TooltipContext context = event.getContext();
        TooltipFlag flags = event.getFlags();
        List<Component> toAdd = new ArrayList<>();

        addToTooltip(NVDataComponents.BINDING.get(), context, toAdd::add, flags, stack);
        int max = stack.getOrDefault(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, 0);
        if (max > 0) {
            int current = stack.getOrDefault(NVDataComponents.CURRENT_UPGRADE_POINTS, 0);
            toAdd.add(Component.translatable("tooltip.neovitae.upgrade_points", current, max).withStyle(ChatFormatting.GOLD));
        }
        addToTooltip(NVDataComponents.UPGRADES.get(), context, toAdd::add, flags, stack);

        // add after name. idgaf
        tooltip.addAll(1, toAdd);
    }


    public static <T extends TooltipProvider> void addToTooltip(
            DataComponentType<T> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, ItemStack stack
    ) {
        T tooltipProvider = stack.get(component);
        if (tooltipProvider != null) {
            tooltipProvider.addToTooltip(context, tooltipAdder, tooltipFlag, stack);
        }
    }
}
