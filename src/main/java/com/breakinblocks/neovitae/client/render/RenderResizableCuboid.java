package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.breakinblocks.neovitae.client.render.NeoVitaeRenderer.Model3D;

import java.util.Arrays;

/**
 * Renders resizable textured cuboids.
 * Adapted from BuildCraft for Blood Magic ritual rendering.
 */
public class RenderResizableCuboid {

    public static final RenderResizableCuboid INSTANCE = new RenderResizableCuboid();

    private static final Vector3f VEC_ZERO = new Vector3f(0, 0, 0);
    private static final int U_MIN = 0;
    private static final int U_MAX = 1;
    private static final int V_MIN = 2;
    private static final int V_MAX = 3;

    protected EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();

    private static Vector3f withValue(Vector3f vector, Axis axis, float value) {
        if (axis == Axis.X) {
            return new Vector3f(value, vector.y(), vector.z());
        } else if (axis == Axis.Y) {
            return new Vector3f(vector.x(), value, vector.z());
        } else if (axis == Axis.Z) {
            return new Vector3f(vector.x(), vector.y(), value);
        }
        throw new RuntimeException("Was given a null axis! (Vector = " + vector + ")");
    }

    public static double getValue(Vec3 vector, Axis axis) {
        if (axis == Axis.X) {
            return vector.x;
        } else if (axis == Axis.Y) {
            return vector.y;
        } else if (axis == Axis.Z) {
            return vector.z;
        }
        throw new RuntimeException("Was given a null axis! (Vector = " + vector + ")");
    }

    public void renderCube(Model3D cube, PoseStack matrix, VertexConsumer buffer, int argb, int light, int overlay) {
        float red = NeoVitaeRenderer.getRed(argb);
        float green = NeoVitaeRenderer.getGreen(argb);
        float blue = NeoVitaeRenderer.getBlue(argb);
        float alpha = NeoVitaeRenderer.getAlpha(argb);

        Vec3 size = new Vec3(cube.sizeX(), cube.sizeY(), cube.sizeZ());
        matrix.pushPose();
        matrix.translate(cube.minX, cube.minY, cube.minZ);

        PoseStack.Pose lastMatrix = matrix.last();
        Matrix4f matrix4f = lastMatrix.pose();

        for (Direction face : Direction.values()) {
            if (cube.shouldSideRender(face)) {
                int ordinal = face.ordinal();
                TextureAtlasSprite sprite = cube.textures[ordinal];
                if (sprite != null) {
                    Axis u = face.getAxis() == Axis.X ? Axis.Z : Axis.X;
                    Axis v = face.getAxis() == Axis.Y ? Axis.Z : Axis.Y;
                    float other = face.getAxisDirection() == AxisDirection.POSITIVE
                            ? (float) getValue(size, face.getAxis())
                            : 0;

                    // Swap the face if positive - renderer returns indexes for negative face
                    face = face.getAxisDirection() == AxisDirection.NEGATIVE ? face : face.getOpposite();
                    Direction opposite = face.getOpposite();

                    float minU = sprite.getU0();
                    float maxU = sprite.getU1();
                    // Flip the v
                    float minV = sprite.getV1();
                    float maxV = sprite.getV0();
                    double sizeU = getValue(size, u);
                    double sizeV = getValue(size, v);

                    for (int uIndex = 0; uIndex < sizeU; uIndex++) {
                        float[] baseUV = new float[]{minU, maxU, minV, maxV};
                        double addU = 1;
                        if (uIndex + addU > sizeU) {
                            addU = sizeU - uIndex;
                            baseUV[U_MAX] = baseUV[U_MIN] + (baseUV[U_MAX] - baseUV[U_MIN]) * (float) addU;
                        }

                        for (int vIndex = 0; vIndex < sizeV; vIndex++) {
                            float[] uv = Arrays.copyOf(baseUV, 4);
                            double addV = 1;
                            if (vIndex + addV > sizeV) {
                                addV = sizeV - vIndex;
                                uv[V_MAX] = uv[V_MIN] + (uv[V_MAX] - uv[V_MIN]) * (float) addV;
                            }
                            float[] xyz = new float[]{uIndex, (float) (uIndex + addU), vIndex, (float) (vIndex + addV)};

                            renderPoint(matrix4f, buffer, face, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, face, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, face, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, face, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);

                            renderPoint(matrix4f, buffer, opposite, u, v, other, uv, xyz, false, false, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, opposite, u, v, other, uv, xyz, false, true, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, opposite, u, v, other, uv, xyz, true, true, red, green, blue, alpha, light, overlay);
                            renderPoint(matrix4f, buffer, opposite, u, v, other, uv, xyz, true, false, red, green, blue, alpha, light, overlay);
                        }
                    }
                }
            }
        }
        matrix.popPose();
    }

    private void renderPoint(Matrix4f matrix4f, VertexConsumer buffer, Direction face,
                              Axis u, Axis v, float other, float[] uv, float[] xyz,
                              boolean minU, boolean minV, float red, float green, float blue, float alpha,
                              int light, int overlay) {
        int U_ARRAY = minU ? U_MIN : U_MAX;
        int V_ARRAY = minV ? V_MIN : V_MAX;

        Vector3f vertex = withValue(VEC_ZERO, u, xyz[U_ARRAY]);
        vertex = withValue(vertex, v, xyz[V_ARRAY]);
        vertex = withValue(vertex, face.getAxis(), other);

        Vec3i normalForFace = face.getNormal();
        // Adjustment for brightness
        float adjustment = 2.5F;
        Vector3f norm = new Vector3f(
                normalForFace.getX() + adjustment,
                normalForFace.getY() + adjustment,
                normalForFace.getZ() + adjustment
        );
        norm.normalize();

        buffer.addVertex(matrix4f, vertex.x(), vertex.y(), vertex.z())
                .setColor(red, green, blue, alpha)
                .setUv(uv[U_ARRAY], uv[V_ARRAY])
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(norm.x(), norm.y(), norm.z());
    }
}
