package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;

@OnlyIn(Dist.CLIENT)
public class EntityMeteorRenderer extends EntityRenderer<EntityMeteor, EntityMeteorRenderer.State> {

    private static final Identifier METEOR_TEXTURE = NeoVitae.rl("textures/entity/meteor.png");

    public EntityMeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
    }

    public static class State extends EntityRenderState {
        public float rotationDegrees;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EntityMeteor entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.rotationDegrees = entity.tickCount * 10.0F;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        float scale = 2.0F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotationDegrees));

        int light = state.lightCoords;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(METEOR_TEXTURE), (pose, consumer) -> {
            Matrix4f matrix = pose.pose();
            float size = 0.5F;
            vertex(consumer, matrix, pose, -size, -size, 0, 0, 1, light);
            vertex(consumer, matrix, pose, size, -size, 0, 1, 1, light);
            vertex(consumer, matrix, pose, size, size, 0, 1, 0, light);
            vertex(consumer, matrix, pose, -size, size, 0, 0, 0, light);
        });

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose pose,
                               float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(matrix, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
