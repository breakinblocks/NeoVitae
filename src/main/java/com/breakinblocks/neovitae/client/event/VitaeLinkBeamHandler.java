package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.stream.BeamRenderer;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class VitaeLinkBeamHandler {

    private static final Set<VitaeLinkBlockEntity> LINKS = ConcurrentHashMap.newKeySet();

    private static final int BEAM_COLOR = 0xCC0011;

    private VitaeLinkBeamHandler() {}

    public static void register(BlockEntity be) {
        if (be instanceof VitaeLinkBlockEntity link) {
            LINKS.add(link);
        }
    }

    public static void unregister(BlockEntity be) {
        LINKS.remove(be);
    }

    @SubscribeEvent
    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        if (LINKS.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Vec3 cameraPos = event.getLevelRenderState().cameraRenderState.pos;
        long gameTime = mc.level.getGameTime();
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);

        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        PoseStack poseStack = event.getPoseStack();

        float r = ((BEAM_COLOR >> 16) & 0xFF) / 255f;
        float g = ((BEAM_COLOR >> 8) & 0xFF) / 255f;
        float b = (BEAM_COLOR & 0xFF) / 255f;

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (VitaeLinkBlockEntity link : LINKS) {
            if (link.isRemoved() || !link.isClientCrafting()) continue;
            BlockPos altarPos = link.getAltarPos();
            if (altarPos == null) continue;
            BeamRenderer.submitBeam(poseStack, collector, link.getBlockPos(), altarPos, r, g, b, gameTime, partialTick);
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        LINKS.clear();
    }
}
