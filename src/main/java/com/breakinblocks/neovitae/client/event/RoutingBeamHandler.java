package com.breakinblocks.neovitae.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Driven from SubmitCustomGeometryEvent rather than a BlockEntityRenderer so beams
// aren't subject to chunk-section frustum culling.
@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class RoutingBeamHandler {

    private static final Set<BlockEntity> NODES = ConcurrentHashMap.newKeySet();

    private static final Identifier BEAM_TEXTURE = NeoVitae.rl("textures/misc/stream.png");
    private static final RenderType BEAM_OPAQUE = RenderTypes.beaconBeam(BEAM_TEXTURE, false);
    private static final RenderType BEAM_TRANSLUCENT = RenderTypes.beaconBeam(BEAM_TEXTURE, true);

    private static final int FULL_BRIGHT = 0xF000F0;
    private static final int BEAM_COLOR = 0xB8DDE0;

    private static final float INNER_RADIUS = 0.04f;
    private static final float OUTER_RADIUS = 0.10f;

    private RoutingBeamHandler() {}

    public static void register(BlockEntity be) {
        if (be instanceof IRoutingNode) {
            NODES.add(be);
        }
    }

    public static void unregister(BlockEntity be) {
        NODES.remove(be);
    }

    @SubscribeEvent
    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        if (NODES.isEmpty()) return;

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

        for (BlockEntity be : NODES) {
            if (be.isRemoved()) continue;
            IRoutingNode node = (IRoutingNode) be;
            List<BlockPos> connections = node.getConnected();
            if (connections.isEmpty()) continue;

            BlockPos nodePos = be.getBlockPos();
            for (BlockPos targetPos : connections) {
                if (targetPos.compareTo(nodePos) <= 0) continue;
                submitBeam(poseStack, collector, nodePos, targetPos, r, g, b, gameTime, partialTick);
            }
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        NODES.clear();
    }

    private static void submitBeam(PoseStack poseStack, SubmitNodeCollector collector,
                                   BlockPos source, BlockPos target,
                                   float r, float g, float bl,
                                   long gameTime, float partialTick) {
        int dx = target.getX() - source.getX();
        int dy = target.getY() - source.getY();
        int dz = target.getZ() - source.getZ();

        double subLength = Mth.sqrt(dx * dx + dz * dz);
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001f) return;

        float rotYaw = -((float) (Math.atan2(dx, dz) * 180.0D / Math.PI));
        float rotPitch = (float) (Math.atan2(dy, subLength) * 180.0D / Math.PI);

        poseStack.pushPose();
        poseStack.translate(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotYaw));
        poseStack.mulPose(Axis.XN.rotationDegrees(rotPitch - 90f));

        float f = (float) Math.floorMod(gameTime, 40L) + partialTick;
        float scrollDir = -f;
        float v0base = Mth.frac(scrollDir * 0.2F - Mth.floor(scrollDir * 0.1F));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float v1Inner = -1.0F + v0base;
        float v2Inner = distance * (0.5F / INNER_RADIUS) + v1Inner;
        collector.submitCustomGeometry(poseStack, BEAM_OPAQUE, (pose, buf) ->
                renderBeamFaces(pose.pose(), buf, r, g, bl, 1.0F, distance, INNER_RADIUS, 0F, 1F, v2Inner, v1Inner));
        poseStack.popPose();

        float v1Outer = -1.0F + v0base;
        float v2Outer = distance + v1Outer;
        collector.submitCustomGeometry(poseStack, BEAM_TRANSLUCENT, (pose, buf) ->
                renderBeamFaces(pose.pose(), buf, r, g, bl, 0.18F, distance, OUTER_RADIUS, 0F, 1F, v2Outer, v1Outer));

        poseStack.popPose();
    }

    private static void renderBeamFaces(Matrix4f matrix, VertexConsumer buffer,
                                        float r, float g, float b, float a,
                                        float height, float radius,
                                        float u1, float u2, float v1, float v2) {
        float yMin = 0F;
        float yMax = height;
        addQuad(matrix, buffer, r, g, b, a, yMin, yMax, -radius, radius, -radius, -radius, u1, u2, v1, v2);
        addQuad(matrix, buffer, r, g, b, a, yMin, yMax, -radius, -radius, radius, -radius, u1, u2, v1, v2);
        addQuad(matrix, buffer, r, g, b, a, yMin, yMax, radius, -radius, radius, radius, u1, u2, v1, v2);
        addQuad(matrix, buffer, r, g, b, a, yMin, yMax, radius, radius, -radius, radius, u1, u2, v1, v2);
    }

    private static void addQuad(Matrix4f matrix, VertexConsumer buffer,
                                float r, float g, float b, float a,
                                float yMin, float yMax,
                                float x1, float z1, float x2, float z2,
                                float u1, float u2, float v1, float v2) {
        addVertex(matrix, buffer, r, g, b, a, yMax, x1, z1, u2, v1);
        addVertex(matrix, buffer, r, g, b, a, yMin, x1, z1, u2, v2);
        addVertex(matrix, buffer, r, g, b, a, yMin, x2, z2, u1, v2);
        addVertex(matrix, buffer, r, g, b, a, yMax, x2, z2, u1, v1);
    }

    private static void addVertex(Matrix4f matrix, VertexConsumer buffer,
                                  float r, float g, float b, float a,
                                  float y, float x, float z, float u, float v) {
        buffer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(0F, 1F, 0F);
    }
}
