package com.breakinblocks.neovitae.client.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.stream.StreamManager;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class StreamEventHandler {

    @SubscribeEvent
    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        StreamManager mgr = StreamManager.getInstance();
        if (!mgr.hasActiveStreams()) return;
        mgr.submitAll(
                event.getPoseStack(),
                event.getSubmitNodeCollector(),
                event.getLevelRenderState().cameraRenderState.pos,
                net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide()) return;
        StreamManager.getInstance().tick();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        StreamManager.getInstance().clear();
    }
}
