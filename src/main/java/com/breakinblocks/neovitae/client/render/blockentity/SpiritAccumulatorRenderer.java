package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;

public class SpiritAccumulatorRenderer implements BlockEntityRenderer<SpiritAccumulatorBlockEntity, SpiritAccumulatorRenderer.State> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/block/spirit_accumulator.png");

    private static final float W = 0.20f;
    private static final float HT = 0.30f;
    private static final float HB = 0.34f;
    private static final float CORE_SCALE = 0.72f;
    private static final float MIN_CORE_FILL = 0.08f;

    private static final float[][] DIRS = { {1, 0}, {0, 1}, {-1, 0}, {0, -1} };
    private static final float[] TOP_SHADE = { 1.0f, 0.9f, 0.82f, 0.9f };
    private static final float[] BOTTOM_SHADE = { 0.78f, 0.7f, 0.62f, 0.7f };

    public SpiritAccumulatorRenderer(BlockEntityRendererProvider.Context context) {}

    public static class State extends BlockEntityRenderState {
        public float bobY;
        public boolean hasCore;
        public int coreColor;
        public float fill;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SpiritAccumulatorBlockEntity be, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(be, s, partialTick, cameraPos, crumbling);
        BlockPos pos = be.getBlockPos();
        float phase = ((pos.getX() * 31 + pos.getY() * 13 + pos.getZ() * 17) & 0xFF) / 255f * Mth.TWO_PI;
        float angle = (System.currentTimeMillis() % 4000L) / 4000f * Mth.TWO_PI;
        s.bobY = Mth.sin(angle + phase) * 0.05f;

        SpiritusType type = be.getAttunedType();
        s.hasCore = type != null;
        if (s.hasCore) {
            s.coreColor = SpiritusTooltipHelper.spiritusColor(type);
            s.fill = Math.max(MIN_CORE_FILL, be.getFillFraction());
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.55 + s.bobY, 0.5);

        boolean hasCore = s.hasCore;
        int color = s.coreColor;
        float fill = s.fill;
        int light = s.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE), (pose, buf) -> {
            if (hasCore) {
                drawCore(pose, buf, color, fill);
            }
            drawShell(pose, buf, light);
        });

        poseStack.popPose();
    }

    private static void drawShell(PoseStack.Pose pose, VertexConsumer buf, int light) {
        for (int i = 0; i < 4; i++) {
            float[] a = DIRS[i];
            float[] b = DIRS[(i + 1) % 4];
            float shade = TOP_SHADE[i];
            tri(pose, buf,
                    0, HT, 0,
                    b[0] * W, 0, b[1] * W,
                    a[0] * W, 0, a[1] * W,
                    shade, shade, shade, 1f, light);
            shade = BOTTOM_SHADE[i];
            tri(pose, buf,
                    0, -HB, 0,
                    a[0] * W, 0, a[1] * W,
                    b[0] * W, 0, b[1] * W,
                    shade, shade, shade, 1f, light);
        }
    }

    private static void drawCore(PoseStack.Pose pose, VertexConsumer buf, int color, float fill) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = 0.95f;
        int light = LightCoordsUtil.FULL_BRIGHT;

        float w = W * CORE_SCALE;
        float ht = HT * CORE_SCALE;
        float hb = HB * CORE_SCALE;
        float yF = -hb + fill * (hb + ht);

        if (yF <= 0) {
            float s = w * (1 + yF / hb);
            for (int i = 0; i < 4; i++) {
                float[] da = DIRS[i];
                float[] db = DIRS[(i + 1) % 4];
                float shade = BOTTOM_SHADE[i];
                tri(pose, buf,
                        0, -hb, 0,
                        da[0] * s, yF, da[1] * s,
                        db[0] * s, yF, db[1] * s,
                        r * shade, g * shade, b * shade, a, light);
            }
            cap(pose, buf, s, yF, r, g, b, a, light);
        } else {
            float s = ht <= 0 ? 0 : w * (1 - yF / ht);
            for (int i = 0; i < 4; i++) {
                float[] da = DIRS[i];
                float[] db = DIRS[(i + 1) % 4];
                float shade = BOTTOM_SHADE[i];
                tri(pose, buf,
                        0, -hb, 0,
                        da[0] * w, 0, da[1] * w,
                        db[0] * w, 0, db[1] * w,
                        r * shade, g * shade, b * shade, a, light);
                shade = TOP_SHADE[i];
                quad(pose, buf,
                        db[0] * w, 0, db[1] * w,
                        da[0] * w, 0, da[1] * w,
                        da[0] * s, yF, da[1] * s,
                        db[0] * s, yF, db[1] * s,
                        r * shade, g * shade, b * shade, a, light);
            }
            if (s > 0.001f) {
                cap(pose, buf, s, yF, r, g, b, a, light);
            }
        }
    }

    private static void cap(PoseStack.Pose pose, VertexConsumer buf, float s, float y,
                            float r, float g, float b, float a, int light) {
        quad(pose, buf,
                DIRS[0][0] * s, y, DIRS[0][1] * s,
                DIRS[3][0] * s, y, DIRS[3][1] * s,
                DIRS[2][0] * s, y, DIRS[2][1] * s,
                DIRS[1][0] * s, y, DIRS[1][1] * s,
                r, g, b, a, light);
    }

    private static void tri(PoseStack.Pose pose, VertexConsumer buf,
                            float ax, float ay, float az,
                            float bx, float by, float bz,
                            float cx, float cy, float cz,
                            float r, float g, float b, float a, int light) {
        Vector3f norm = faceNormal(pose, ax, ay, az, bx, by, bz, cx, cy, cz);
        Matrix4f matrix = pose.pose();
        vertex(buf, matrix, ax, ay, az, 0.5f, 0f, r, g, b, a, light, norm);
        vertex(buf, matrix, bx, by, bz, 0f, 1f, r, g, b, a, light, norm);
        vertex(buf, matrix, cx, cy, cz, 1f, 1f, r, g, b, a, light, norm);
        vertex(buf, matrix, cx, cy, cz, 1f, 1f, r, g, b, a, light, norm);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer buf,
                             float ax, float ay, float az,
                             float bx, float by, float bz,
                             float cx, float cy, float cz,
                             float dx, float dy, float dz,
                             float r, float g, float b, float a, int light) {
        Vector3f norm = faceNormal(pose, ax, ay, az, bx, by, bz, cx, cy, cz);
        Matrix4f matrix = pose.pose();
        vertex(buf, matrix, ax, ay, az, 0f, 0f, r, g, b, a, light, norm);
        vertex(buf, matrix, bx, by, bz, 0f, 1f, r, g, b, a, light, norm);
        vertex(buf, matrix, cx, cy, cz, 1f, 1f, r, g, b, a, light, norm);
        vertex(buf, matrix, dx, dy, dz, 1f, 0f, r, g, b, a, light, norm);
    }

    private static Vector3f faceNormal(PoseStack.Pose pose,
                                       float ax, float ay, float az,
                                       float bx, float by, float bz,
                                       float cx, float cy, float cz) {
        Vector3f u = new Vector3f(bx - ax, by - ay, bz - az);
        Vector3f v = new Vector3f(cx - ax, cy - ay, cz - az);
        Vector3f norm = u.cross(v).normalize();
        Vector3f out = new Vector3f();
        pose.transformNormal(norm.x, norm.y, norm.z, out);
        return out;
    }

    private static void vertex(VertexConsumer buf, Matrix4f matrix,
                               float x, float y, float z, float u, float v,
                               float r, float g, float b, float a, int light, Vector3f norm) {
        buf.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(norm.x, norm.y, norm.z);
    }
}
