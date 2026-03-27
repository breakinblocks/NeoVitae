package com.breakinblocks.neovitae.client.render.stream;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import org.joml.Matrix4f;

/**
 * Renders tube geometry from path points, replacing Thaumcraft's CoreGLE glePolyCone().
 * <p>
 * For each pair of adjacent path points, generates a ring of vertices (8-sided polygon)
 * and connects adjacent rings with quads. Uses parallel transport for consistent
 * frame orientation along curved paths to avoid twisting artifacts.
 */
public class StreamRenderer {

    public static final RenderType STREAM_RENDER_TYPE = RenderType.entityTranslucent(
            NeoVitae.rl("textures/misc/stream.png")
    );

    private static final int TUBE_SEGMENTS = 8;
    private static final float TWO_PI = (float) (Math.PI * 2.0);
    private static final int FULL_BRIGHT = 0xF000F0;

    /**
     * Render a single stream's tube geometry.
     *
     * @param stream      the active stream with computed render data
     * @param poseStack   current pose stack (camera-relative)
     * @param buffer      vertex consumer from the stream render type
     * @param partialTick partial tick for interpolation
     */
    public static void render(ActiveStream stream, PoseStack poseStack,
                              VertexConsumer buffer, float partialTick) {
        double[][] points = stream.getPositions();
        float[][] streamColors = stream.getColors();
        float[] streamRadii = stream.getRadii();

        if (points == null || points.length < 3) return;

        int numPoints = points.length;

        poseStack.pushPose();

        // Translate to stream origin (positions are relative to startX/Y/Z)
        poseStack.translate(stream.getStartX(), stream.getStartY(), stream.getStartZ());

        Matrix4f matrix = poseStack.last().pose();

        // Compute direction vectors at each point
        Vec3[] directions = computeDirections(points, numPoints);

        // Compute perpendicular frames using parallel transport
        Vec3[] normals = new Vec3[numPoints];
        Vec3[] binormals = new Vec3[numPoints];
        computeFrames(directions, normals, binormals, numPoints);

        // UV scroll offset for animation
        float vOffset = (stream.getAge() + partialTick) * 0.05f;

        // Generate tube geometry: quads between adjacent rings
        for (int i = 0; i < numPoints - 1; i++) {
            float r0 = streamRadii[i];
            float r1 = streamRadii[i + 1];

            // Skip degenerate segments
            if (r0 <= 0 && r1 <= 0) continue;

            float v0 = (float) i / numPoints + vOffset;
            float v1 = (float) (i + 1) / numPoints + vOffset;

            // Colors at each ring
            float cr0 = streamColors[i][0], cg0 = streamColors[i][1], cb0 = streamColors[i][2], ca0 = streamColors[i][3];
            float cr1 = streamColors[i + 1][0], cg1 = streamColors[i + 1][1], cb1 = streamColors[i + 1][2], ca1 = streamColors[i + 1][3];

            for (int j = 0; j < TUBE_SEGMENTS; j++) {
                int j1 = (j + 1) % TUBE_SEGMENTS;

                float angle0 = (float) j / TUBE_SEGMENTS * TWO_PI;
                float angle1 = (float) j1 / TUBE_SEGMENTS * TWO_PI;

                float cos0 = (float) Math.cos(angle0);
                float sin0 = (float) Math.sin(angle0);
                float cos1 = (float) Math.cos(angle1);
                float sin1 = (float) Math.sin(angle1);

                float u0 = (float) j / TUBE_SEGMENTS;
                float u1 = (float) (j + 1) / TUBE_SEGMENTS;

                // Ring i, segment j
                Vec3 offset0j = normals[i].scale(cos0).add(binormals[i].scale(sin0));
                float x0j = (float) (points[i][0] + offset0j.x * r0);
                float y0j = (float) (points[i][1] + offset0j.y * r0);
                float z0j = (float) (points[i][2] + offset0j.z * r0);

                // Ring i, segment j+1
                Vec3 offset0j1 = normals[i].scale(cos1).add(binormals[i].scale(sin1));
                float x0j1 = (float) (points[i][0] + offset0j1.x * r0);
                float y0j1 = (float) (points[i][1] + offset0j1.y * r0);
                float z0j1 = (float) (points[i][2] + offset0j1.z * r0);

                // Ring i+1, segment j
                Vec3 offset1j = normals[i + 1].scale(cos0).add(binormals[i + 1].scale(sin0));
                float x1j = (float) (points[i + 1][0] + offset1j.x * r1);
                float y1j = (float) (points[i + 1][1] + offset1j.y * r1);
                float z1j = (float) (points[i + 1][2] + offset1j.z * r1);

                // Ring i+1, segment j+1
                Vec3 offset1j1 = normals[i + 1].scale(cos1).add(binormals[i + 1].scale(sin1));
                float x1j1 = (float) (points[i + 1][0] + offset1j1.x * r1);
                float y1j1 = (float) (points[i + 1][1] + offset1j1.y * r1);
                float z1j1 = (float) (points[i + 1][2] + offset1j1.z * r1);

                // Outward-facing normals
                Vec3 n0j = offset0j.normalize();
                Vec3 n0j1 = offset0j1.normalize();
                Vec3 n1j = offset1j.normalize();
                Vec3 n1j1 = offset1j1.normalize();

                // Emit quad: v0 -> v1 -> v2 -> v3 (CCW from outside)
                emitVertex(buffer, matrix, x0j, y0j, z0j, u0, v0, cr0, cg0, cb0, ca0, n0j);
                emitVertex(buffer, matrix, x1j, y1j, z1j, u0, v1, cr1, cg1, cb1, ca1, n1j);
                emitVertex(buffer, matrix, x1j1, y1j1, z1j1, u1, v1, cr1, cg1, cb1, ca1, n1j1);
                emitVertex(buffer, matrix, x0j1, y0j1, z0j1, u1, v0, cr0, cg0, cb0, ca0, n0j1);
            }
        }

        poseStack.popPose();
    }

    private static void emitVertex(VertexConsumer buffer, Matrix4f matrix,
                                   float x, float y, float z,
                                   float u, float v,
                                   float r, float g, float b, float a,
                                   Vec3 normal) {
        buffer.addVertex(matrix, x, y, z)
                .setColor((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255))
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal((float) normal.x, (float) normal.y, (float) normal.z);
    }

    /**
     * Compute direction vectors between adjacent points.
     */
    private static Vec3[] computeDirections(double[][] points, int n) {
        Vec3[] dirs = new Vec3[n];
        for (int i = 0; i < n - 1; i++) {
            Vec3 dir = new Vec3(
                    points[i + 1][0] - points[i][0],
                    points[i + 1][1] - points[i][1],
                    points[i + 1][2] - points[i][2]
            );
            double len = dir.length();
            dirs[i] = len > 1e-6 ? dir.scale(1.0 / len) : new Vec3(0, 1, 0);
        }
        dirs[n - 1] = dirs[n - 2];
        return dirs;
    }

    /**
     * Compute perpendicular frames using parallel transport.
     * Avoids twisting that occurs with naive cross-product methods.
     */
    private static void computeFrames(Vec3[] dirs, Vec3[] normals, Vec3[] binormals, int n) {
        // Initial frame: cross first direction with a reference vector
        Vec3 ref = Math.abs(dirs[0].y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        normals[0] = dirs[0].cross(ref).normalize();
        binormals[0] = dirs[0].cross(normals[0]).normalize();

        // Parallel transport along the path
        for (int i = 1; i < n; i++) {
            Vec3 prevDir = dirs[i - 1];
            Vec3 currDir = dirs[i];

            Vec3 rotAxis = prevDir.cross(currDir);
            double rotAxisLen = rotAxis.length();

            if (rotAxisLen < 1e-6) {
                // Directions are nearly parallel, keep previous frame
                normals[i] = normals[i - 1];
                binormals[i] = binormals[i - 1];
            } else {
                // Rotate previous normal by the angle between directions
                double dot = prevDir.dot(currDir);
                dot = Math.max(-1.0, Math.min(1.0, dot));
                double angle = Math.acos(dot);

                rotAxis = rotAxis.scale(1.0 / rotAxisLen); // normalize
                normals[i] = rotateVector(normals[i - 1], rotAxis, angle);
                binormals[i] = currDir.cross(normals[i]).normalize();
            }
        }
    }

    /**
     * Rodrigues' rotation formula: rotate vector v around unit axis k by angle theta.
     */
    private static Vec3 rotateVector(Vec3 v, Vec3 k, double theta) {
        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);
        double dot = k.dot(v);
        Vec3 cross = k.cross(v);
        return new Vec3(
                v.x * cosT + cross.x * sinT + k.x * dot * (1 - cosT),
                v.y * cosT + cross.y * sinT + k.y * dot * (1 - cosT),
                v.z * cosT + cross.z * sinT + k.z * dot * (1 - cosT)
        );
    }
}
