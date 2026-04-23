package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;

public class BloodTankRenderer implements BlockEntityRenderer<BloodTankBlockEntity, BloodTankRenderer.State> {

    private static final float minHeight = 1F / 16F + 0.01F;
    private static final float maxHeight = 11F / 16F;
    private static final float start = 4F / 16F;
    private static final float end = 12F / 16F;

    public BloodTankRenderer(BlockEntityRendererProvider.Context context) {}

    public static class State extends BlockEntityRenderState {
        public boolean hasFluid;
        public float fillFraction;
        public int tintColor = 0xFFFFFFFF;
        public float u0 = 0f;
        public float u1 = 1f;
        public float v0 = 0f;
        public float v1 = 1f;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BloodTankBlockEntity be, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(be, s, partialTick, cameraPos, crumbling);
        FluidStack stack = be.getFluidContained();
        s.hasFluid = !stack.isEmpty();
        if (!s.hasFluid) {
            s.fillFraction = 0f;
            return;
        }
        int cap = be.getCapacity();
        s.fillFraction = cap > 0 ? Math.min(1f, (float) stack.getAmount() / (float) cap) : 0f;

        Fluid fluid = stack.getFluid();
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
        if (model != null) {
            TextureAtlasSprite sprite = model.stillMaterial().sprite();
            s.u0 = sprite.getU0();
            s.u1 = sprite.getU1();
            s.v0 = sprite.getV0();
            s.v1 = sprite.getV1();
            BlockTintSource tintSource = model.tintSource();
            if (tintSource != null) {
                int rgb = tintSource.color(fluid.defaultFluidState().createLegacyBlock()) & 0xFFFFFF;
                s.tintColor = 0xFF000000 | rgb;
            } else {
                s.tintColor = 0xFFFFFFFF;
            }
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!s.hasFluid || s.fillFraction <= 0f) return;

        float height = (maxHeight - minHeight) * s.fillFraction;
        int light = s.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;
        int color = s.tintColor;
        float u0 = s.u0, u1 = s.u1, v0 = s.v0, v1 = s.v1;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buf) -> {
            drawCube(pose, buf, color, light, overlay, start, minHeight, start, end, minHeight + height, end, u0, u1, v0, v1);
        });
    }

    private static void drawCube(PoseStack.Pose pose, VertexConsumer buf, int color, int light, int overlay,
                                 float x0, float y0, float z0, float x1, float y1, float z1,
                                 float u0, float u1, float v0, float v1) {
        Matrix4f matrix = pose.pose();
        Vector3f norm = new Vector3f();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >>> 24) & 0xFF;

        pose.transformNormal(-1, 0, 0, norm);
        quad(buf, matrix, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
        pose.transformNormal(1, 0, 0, norm);
        quad(buf, matrix, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
        pose.transformNormal(0, -1, 0, norm);
        quad(buf, matrix, x1, y0, z0, x0, y0, z0, x0, y0, z1, x1, y0, z1, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
        pose.transformNormal(0, 1, 0, norm);
        quad(buf, matrix, x1, y1, z1, x1, y1, z0, x0, y1, z0, x0, y1, z1, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
        pose.transformNormal(0, 0, -1, norm);
        quad(buf, matrix, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
        pose.transformNormal(0, 0, 1, norm);
        quad(buf, matrix, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, r, g, b, a, light, overlay, norm, u0, u1, v0, v1);
    }

    private static void quad(VertexConsumer buf, Matrix4f matrix,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz,
                             int r, int g, int b, int a, int light, int overlay, Vector3f norm,
                             float u0, float u1, float v0, float v1) {
        buf.addVertex(matrix, ax, ay, az).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
        buf.addVertex(matrix, bx, by, bz).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
        buf.addVertex(matrix, cx, cy, cz).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
        buf.addVertex(matrix, dx, dy, dz).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(norm.x, norm.y, norm.z);
    }
}
