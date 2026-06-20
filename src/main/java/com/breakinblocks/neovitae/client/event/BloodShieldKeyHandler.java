package com.breakinblocks.neovitae.client.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.network.ShieldKeyPayload;

@EventBusSubscriber(modid = NeoVitae.MODID, value = Dist.CLIENT)
public final class BloodShieldKeyHandler {

    private static boolean lastActive = false;

    private BloodShieldKeyHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        boolean active = ClientModEventHandler.BLOOD_SHIELD.isDown()
                && mc.player.getOffhandItem().getItem() instanceof BloodOrbItem;
        if (active != lastActive) {
            ClientPacketDistributor.sendToServer(new ShieldKeyPayload(active));
            lastActive = active;
        }
    }
}
