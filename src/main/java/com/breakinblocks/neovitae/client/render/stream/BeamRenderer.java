package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;

public final class BeamRenderer {

    public static final ResourceLocation BEAM_TEXTURE = NeoVitae.rl("textures/misc/stream.png");

    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float INNER_RADIUS = 0.04f;
    private static final float OUTER_RADIUS = 0.10f;

    private BeamRenderer() {}

    public static void endBatch(MultiBufferSource.BufferSource bufferSource) {
        bufferSource.endBatch(RenderType.beaconBeam(BEAM_TEXTURE, false));
        bufferSource.endBatch(RenderType.beaconBeam(BEAM_TEXTURE, true));
    }

    public static void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                  BlockPos source, BlockPos target,
                                  float r, float g, float bl,
                                  long gameTime, float partialTick) {
        renderBeam(poseStack, bufferSource, Vec3.atCenterOf(source), Vec3.atCenterOf(target),
                r, g, bl, gameTime, partialTick);
    }

    public static void renderBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                  Vec3 source, Vec3 target,
                                  float r, float g, float bl,
                                  long gameTime, float partialTick) {
        double dx = target.x - source.x;
        double dy = target.y - source.y;
        double dz = target.z - source.z;

        double subLength = Math.sqrt(dx * dx + dz * dz);
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001f) return;

        float rotYaw = -((float) (Math.atan2(dx, dz) * 180.0D / Math.PI));
        float rotPitch = (float) (Math.atan2(dy, subLength) * 180.0D / Math.PI);

        poseStack.pushPose();
        poseStack.translate(source.x, source.y, source.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotYaw));
        poseStack.mulPose(Axis.XN.rotationDegrees(rotPitch - 90f));

        float f = (float) Math.floorMod(gameTime, 40L) + partialTick;
        float scrollDir = distance < 0 ? f : -f;
        float v0base = Mth.frac(scrollDir * 0.2F - Mth.floor(scrollDir * 0.1F));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float v1Inner = -1.0F + v0base;
        float v2Inner = distance * (0.5F / INNER_RADIUS) + v1Inner;
        renderBeamFaces(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, false)),
                r, g, bl, 1.0F, distance, INNER_RADIUS, 0F, 1F, v2Inner, v1Inner);
        poseStack.popPose();

        float v1Outer = -1.0F + v0base;
        float v2Outer = distance + v1Outer;
        renderBeamFaces(poseStack, bufferSource.getBuffer(RenderType.beaconBeam(BEAM_TEXTURE, true)),
                r, g, bl, 0.18F, distance, OUTER_RADIUS, 0F, 1F, v2Outer, v1Outer);

        poseStack.popPose();
    }

    private static void renderBeamFaces(PoseStack poseStack, VertexConsumer buffer,
                                        float r, float g, float b, float a,
                                        float height, float radius,
                                        float u1, float u2, float v1, float v2) {
        Matrix4f matrix = poseStack.last().pose();
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
