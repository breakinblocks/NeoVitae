package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.stream.StreamManager;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class StreamEventHandler {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!StreamManager.getInstance().hasActiveStreams()) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        MultiBufferSource.BufferSource bufferSource =
                Minecraft.getInstance().renderBuffers().bufferSource();

        StreamManager.getInstance().renderAll(poseStack, bufferSource, cameraPos, partialTick);
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
