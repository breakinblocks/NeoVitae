package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.BlockyMode;
import org.joml.Matrix4f;

/**
 * Renders tube geometry from path points using the modern NeoForge rendering pipeline.
 * <p>
 * Supports two render types: standard translucent and emissive (glow).
 * Tube segment count is configurable per stream via {@link ActiveStream#getTubeSegments()}.
 */
public class StreamRenderer {

    private static final ResourceLocation STREAM_TEXTURE = NeoVitae.rl("textures/misc/stream.png");

    /** Standard translucent render type (affected by world lighting). */
    public static final RenderType STREAM_RENDER_TYPE = RenderType.entityTranslucent(STREAM_TEXTURE);

    /** Emissive render type (fullbright, ignores world lighting). */
    public static final RenderType STREAM_GLOW_TYPE = RenderType.entityTranslucentEmissive(STREAM_TEXTURE);

    private static final float TWO_PI = (float) (Math.PI * 2.0);
    private static final int FULL_BRIGHT = 0xF000F0;

    /** Outer halo radius multiplier — geometry-based silhouette softener. */
    private static final float HALO_RADIUS_MULT = 1.6f;
    /** Halo alpha multiplier — keeps the outer shell faint so it reads as a soft glow. */
    private static final float HALO_ALPHA_MULT = 0.28f;

    /**
     * Render a single stream using the appropriate geometry mode.
     * Tubes get a second outer pass at larger radius + reduced alpha to feather their silhouette.
     */
    public static void render(ActiveStream stream, PoseStack poseStack,
                              VertexConsumer buffer, float partialTick) {
        switch (stream.getBlockyMode()) {
            case BLOCKY_BEAM -> { renderBeam(stream, poseStack, buffer, partialTick); return; }
            case BLOCKY_BOX -> { renderBox(stream, poseStack, buffer, partialTick); return; }
            default -> {}
        }
        // Halo pass first so the inner core writes over it.
        renderTube(stream, poseStack, buffer, partialTick, HALO_RADIUS_MULT, HALO_ALPHA_MULT);
        renderTube(stream, poseStack, buffer, partialTick, 1.0f, 1.0f);
    }

    private static void renderTube(ActiveStream stream, PoseStack poseStack,
                                   VertexConsumer buffer, float partialTick,
                                   float radiusMult, float alphaMult) {
        double[][] points = stream.getPositions();
        float[][] streamColors = stream.getColors();
        float[] streamRadii = stream.getRadii();

        if (points == null || points.length < 3) return;

        int numPoints = points.length;
        int segments = stream.getTubeSegments();

        poseStack.pushPose();
        poseStack.translate(stream.getStartX(), stream.getStartY(), stream.getStartZ());

        Matrix4f matrix = poseStack.last().pose();

        Vec3[] directions = computeDirections(points, numPoints);
        Vec3[] normals = new Vec3[numPoints];
        Vec3[] binormals = new Vec3[numPoints];

        if (stream.getBlockyMode() == BlockyMode.BLOCKY_UNIFORM) {
            computeFixedFrames(directions, normals, binormals, numPoints);
        } else {
            computeFrames(directions, normals, binormals, numPoints);
        }

        float vOffset = (stream.getAge() + partialTick) * 0.05f;

        for (int i = 0; i < numPoints - 1; i++) {
            float r0 = streamRadii[i] * radiusMult;
            float r1 = streamRadii[i + 1] * radiusMult;
            if (r0 <= 0 && r1 <= 0) continue;

            float v0 = (float) i / numPoints + vOffset;
            float v1 = (float) (i + 1) / numPoints + vOffset;

            float cr0 = streamColors[i][0], cg0 = streamColors[i][1], cb0 = streamColors[i][2], ca0 = streamColors[i][3] * alphaMult;
            float cr1 = streamColors[i + 1][0], cg1 = streamColors[i + 1][1], cb1 = streamColors[i + 1][2], ca1 = streamColors[i + 1][3] * alphaMult;

            for (int j = 0; j < segments; j++) {
                int j1 = (j + 1) % segments;
                float angle0 = (float) j / segments * TWO_PI;
                float angle1 = (float) j1 / segments * TWO_PI;
                float cos0 = (float) Math.cos(angle0), sin0 = (float) Math.sin(angle0);
                float cos1 = (float) Math.cos(angle1), sin1 = (float) Math.sin(angle1);
                float u0 = (float) j / segments;
                float u1 = (float) (j + 1) / segments;

                Vec3 off0j = normals[i].scale(cos0).add(binormals[i].scale(sin0));
                Vec3 off0j1 = normals[i].scale(cos1).add(binormals[i].scale(sin1));
                Vec3 off1j = normals[i + 1].scale(cos0).add(binormals[i + 1].scale(sin0));
                Vec3 off1j1 = normals[i + 1].scale(cos1).add(binormals[i + 1].scale(sin1));

                emitVertex(buffer, matrix, points[i], off0j, r0, u0, v0, cr0, cg0, cb0, ca0);
                emitVertex(buffer, matrix, points[i + 1], off1j, r1, u0, v1, cr1, cg1, cb1, ca1);
                emitVertex(buffer, matrix, points[i + 1], off1j1, r1, u1, v1, cr1, cg1, cb1, ca1);
                emitVertex(buffer, matrix, points[i], off0j1, r0, u1, v0, cr0, cg0, cb0, ca0);
            }
        }

        poseStack.popPose();
    }

    private static void renderBeam(ActiveStream stream, PoseStack poseStack,
                                   VertexConsumer buffer, float partialTick) {
        double[][] points = stream.getPositions();
        float[][] streamColors = stream.getColors();
        float[] streamRadii = stream.getRadii();

        if (points == null || points.length < 3) return;

        int last = points.length - 1;
        float r = streamRadii[last / 2];
        if (r <= 0) r = stream.getEffect().scale;

        poseStack.pushPose();
        poseStack.translate(stream.getStartX(), stream.getStartY(), stream.getStartZ());
        Matrix4f matrix = poseStack.last().pose();

        float x0 = (float) points[0][0], y0 = (float) points[0][1], z0 = (float) points[0][2];
        float x1 = (float) points[last][0], y1 = (float) points[last][1], z1 = (float) points[last][2];

        float[] cTail = streamColors[0];
        float[] cHead = streamColors[last];
        int cr0 = (int)(cTail[0]*255), cg0 = (int)(cTail[1]*255), cb0 = (int)(cTail[2]*255), ca0 = (int)(cTail[3]*255);
        int cr1 = (int)(cHead[0]*255), cg1 = (int)(cHead[1]*255), cb1 = (int)(cHead[2]*255), ca1 = (int)(cHead[3]*255);

        // Top face (+Y)
        emitQuadBothSides(buffer, matrix,
                x0-r, y0+r, z0-r, x1-r, y1+r, z1-r,
                x1+r, y1+r, z1+r, x0+r, y0+r, z0+r,
                cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, 1, 0);

        // Bottom face (-Y)
        emitQuadBothSides(buffer, matrix,
                x0+r, y0-r, z0-r, x1+r, y1-r, z1-r,
                x1-r, y1-r, z1+r, x0-r, y0-r, z0+r,
                cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, -1, 0);

        // Right face (+X)
        emitQuadBothSides(buffer, matrix,
                x0+r, y0+r, z0-r, x1+r, y1+r, z1-r,
                x1+r, y1-r, z1-r, x0+r, y0-r, z0-r,
                cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 1, 0, 0);

        // Left face (-X)
        emitQuadBothSides(buffer, matrix,
                x0-r, y0-r, z0+r, x1-r, y1-r, z1+r,
                x1-r, y1+r, z1+r, x0-r, y0+r, z0+r,
                cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, -1, 0, 0);

        poseStack.popPose();
    }

    private static void renderBox(ActiveStream stream, PoseStack poseStack,
                                  VertexConsumer buffer, float partialTick) {
        double[][] points = stream.getPositions();
        float[][] streamColors = stream.getColors();
        float[] streamRadii = stream.getRadii();

        if (points == null || points.length < 3) return;

        int numPoints = points.length;

        poseStack.pushPose();
        poseStack.translate(stream.getStartX(), stream.getStartY(), stream.getStartZ());

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < numPoints - 1; i++) {
            float r0 = streamRadii[i];
            float r1 = streamRadii[i + 1];
            if (r0 <= 0 && r1 <= 0) continue;

            float[] c0 = streamColors[i];
            float[] c1 = streamColors[i + 1];
            int cr0 = (int)(c0[0]*255), cg0 = (int)(c0[1]*255), cb0 = (int)(c0[2]*255), ca0 = (int)(c0[3]*255);
            int cr1 = (int)(c1[0]*255), cg1 = (int)(c1[1]*255), cb1 = (int)(c1[2]*255), ca1 = (int)(c1[3]*255);

            float x0 = (float)points[i][0], y0 = (float)points[i][1], z0 = (float)points[i][2];
            float x1 = (float)points[i+1][0], y1 = (float)points[i+1][1], z1 = (float)points[i+1][2];

            // Top face (+Y)
            emitQuadBothSides(buffer, matrix,
                    x0-r0, y0+r0, z0-r0, x1-r1, y1+r1, z1-r1,
                    x1+r1, y1+r1, z1+r1, x0+r0, y0+r0, z0+r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, 1, 0);

            // Bottom face (-Y)
            emitQuadBothSides(buffer, matrix,
                    x0+r0, y0-r0, z0-r0, x1+r1, y1-r1, z1-r1,
                    x1-r1, y1-r1, z1+r1, x0-r0, y0-r0, z0+r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, -1, 0);

            // Right face (+X)
            emitQuadBothSides(buffer, matrix,
                    x0+r0, y0+r0, z0-r0, x1+r1, y1+r1, z1-r1,
                    x1+r1, y1-r1, z1-r1, x0+r0, y0-r0, z0-r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 1, 0, 0);

            // Left face (-X)
            emitQuadBothSides(buffer, matrix,
                    x0-r0, y0-r0, z0-r0, x1-r1, y1-r1, z1-r1,
                    x1-r1, y1+r1, z1-r1, x0-r0, y0+r0, z0-r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, -1, 0, 0);

            // Front face (+Z)
            emitQuadBothSides(buffer, matrix,
                    x0-r0, y0+r0, z0+r0, x1-r1, y1+r1, z1+r1,
                    x1-r1, y1-r1, z1+r1, x0-r0, y0-r0, z0+r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, 0, 1);

            // Back face (-Z)
            emitQuadBothSides(buffer, matrix,
                    x0+r0, y0+r0, z0-r0, x1+r1, y1+r1, z1-r1,
                    x1+r1, y1-r1, z1-r1, x0+r0, y0-r0, z0-r0,
                    cr0, cg0, cb0, ca0, cr1, cg1, cb1, ca1, 0, 0, -1);
        }

        poseStack.popPose();
    }

    private static void emitQuadBothSides(VertexConsumer buffer, Matrix4f matrix,
                                          float ax, float ay, float az,
                                          float bx, float by, float bz,
                                          float cx, float cy, float cz,
                                          float dx, float dy, float dz,
                                          int cr0, int cg0, int cb0, int ca0,
                                          int cr1, int cg1, int cb1, int ca1,
                                          float nx, float ny, float nz) {
        // Front face (A-B-C-D)
        buffer.addVertex(matrix, ax, ay, az).setColor(cr0, cg0, cb0, ca0).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, bx, by, bz).setColor(cr1, cg1, cb1, ca1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, cx, cy, cz).setColor(cr1, cg1, cb1, ca1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, dx, dy, dz).setColor(cr0, cg0, cb0, ca0).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(nx, ny, nz);

        // Back face (D-C-B-A) reversed winding
        buffer.addVertex(matrix, dx, dy, dz).setColor(cr0, cg0, cb0, ca0).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(-nx, -ny, -nz);
        buffer.addVertex(matrix, cx, cy, cz).setColor(cr1, cg1, cb1, ca1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(-nx, -ny, -nz);
        buffer.addVertex(matrix, bx, by, bz).setColor(cr1, cg1, cb1, ca1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(-nx, -ny, -nz);
        buffer.addVertex(matrix, ax, ay, az).setColor(cr0, cg0, cb0, ca0).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(-nx, -ny, -nz);
    }

    private static void emitVertex(VertexConsumer buffer, Matrix4f matrix,
                                   double[] center, Vec3 offset, float radius,
                                   float u, float v,
                                   float r, float g, float b, float a) {
        float x = (float) (center[0] + offset.x * radius);
        float y = (float) (center[1] + offset.y * radius);
        float z = (float) (center[2] + offset.z * radius);
        Vec3 normal = offset.normalize();
        buffer.addVertex(matrix, x, y, z)
                .setColor((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255))
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal((float) normal.x, (float) normal.y, (float) normal.z);
    }

    private static Vec3[] computeDirections(double[][] points, int n) {
        Vec3[] dirs = new Vec3[n];
        for (int i = 0; i < n - 1; i++) {
            Vec3 dir = new Vec3(
                    points[i + 1][0] - points[i][0],
                    points[i + 1][1] - points[i][1],
                    points[i + 1][2] - points[i][2]);
            double len = dir.length();
            dirs[i] = len > 1e-6 ? dir.scale(1.0 / len) : new Vec3(0, 1, 0);
        }
        dirs[n - 1] = dirs[n - 2];
        return dirs;
    }

    private static void computeFixedFrames(Vec3[] dirs, Vec3[] normals, Vec3[] binormals, int n) {
        Vec3 ref = Math.abs(dirs[0].y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 normal = dirs[0].cross(ref).normalize();
        Vec3 binormal = dirs[0].cross(normal).normalize();
        for (int i = 0; i < n; i++) {
            normals[i] = normal;
            binormals[i] = binormal;
        }
    }

    private static void computeFrames(Vec3[] dirs, Vec3[] normals, Vec3[] binormals, int n) {
        Vec3 ref = Math.abs(dirs[0].y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        normals[0] = dirs[0].cross(ref).normalize();
        binormals[0] = dirs[0].cross(normals[0]).normalize();

        for (int i = 1; i < n; i++) {
            Vec3 prevDir = dirs[i - 1];
            Vec3 currDir = dirs[i];
            Vec3 rotAxis = prevDir.cross(currDir);
            double rotLen = rotAxis.length();

            if (rotLen < 1e-6) {
                normals[i] = normals[i - 1];
                binormals[i] = binormals[i - 1];
            } else {
                double dot = Math.max(-1.0, Math.min(1.0, prevDir.dot(currDir)));
                double angle = Math.acos(dot);
                rotAxis = rotAxis.scale(1.0 / rotLen);
                normals[i] = rotateVector(normals[i - 1], rotAxis, angle);
                binormals[i] = currDir.cross(normals[i]).normalize();
            }
        }
    }

    private static Vec3 rotateVector(Vec3 v, Vec3 k, double theta) {
        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);
        double dot = k.dot(v);
        Vec3 cross = k.cross(v);
        return new Vec3(
                v.x * cosT + cross.x * sinT + k.x * dot * (1 - cosT),
                v.y * cosT + cross.y * sinT + k.y * dot * (1 - cosT),
                v.z * cosT + cross.z * sinT + k.z * dot * (1 - cosT));
    }
}
