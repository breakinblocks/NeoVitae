package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public final class RitualBoxRenderer {

    private static final float LINE_WIDTH = 2.0f;

    private RitualBoxRenderer() {}

    public static void drawLineBox(Matrix4f matrix, VertexConsumer c, AABB box, float r, float g, float b, float a) {
        float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
        float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
        edge(matrix, c, x0, y0, z0, x1, y0, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z0, x1, y0, z1, r, g, b, a);
        edge(matrix, c, x1, y0, z1, x0, y0, z1, r, g, b, a);
        edge(matrix, c, x0, y0, z1, x0, y0, z0, r, g, b, a);
        edge(matrix, c, x0, y1, z0, x1, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y1, z0, x1, y1, z1, r, g, b, a);
        edge(matrix, c, x1, y1, z1, x0, y1, z1, r, g, b, a);
        edge(matrix, c, x0, y1, z1, x0, y1, z0, r, g, b, a);
        edge(matrix, c, x0, y0, z0, x0, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z0, x1, y1, z0, r, g, b, a);
        edge(matrix, c, x1, y0, z1, x1, y1, z1, r, g, b, a);
        edge(matrix, c, x0, y0, z1, x0, y1, z1, r, g, b, a);
    }

    public static void drawLineBox(Matrix4f matrix, VertexConsumer c, AABB box, int rgb, float alpha) {
        drawLineBox(matrix, c, box,
                ((rgb >> 16) & 0xFF) / 255f,
                ((rgb >> 8) & 0xFF) / 255f,
                (rgb & 0xFF) / 255f,
                alpha);
    }

    private static void edge(Matrix4f matrix, VertexConsumer c, float ax, float ay, float az,
                             float bx, float by, float bz, float r, float g, float b, float a) {
        float nx = bx - ax, ny = by - ay, nz = bz - az;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len != 0) { nx /= len; ny /= len; nz /= len; }
        c.addVertex(matrix, ax, ay, az).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(LINE_WIDTH);
        c.addVertex(matrix, bx, by, bz).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(LINE_WIDTH);
    }
}
