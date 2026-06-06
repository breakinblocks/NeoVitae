// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;

@OnlyIn(Dist.CLIENT)
public class EntityMeteorRenderer extends EntityRenderer<EntityMeteor> {

    private static final ResourceLocation METEOR_TEXTURE = NeoVitae.rl("textures/entity/meteor.png");

    public EntityMeteorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
    }

    @Override
    public void render(EntityMeteor entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float scale = 2.0F;
        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.YP.rotationDegrees(entity.tickCount * 10.0F));

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();

        float size = 0.5F;
        vertex(vertexConsumer, matrix4f, pose, -size, -size, 0, 0, 1, packedLight);
        vertex(vertexConsumer, matrix4f, pose, size, -size, 0, 1, 1, packedLight);
        vertex(vertexConsumer, matrix4f, pose, size, size, 0, 1, 0, packedLight);
        vertex(vertexConsumer, matrix4f, pose, -size, size, 0, 0, 0, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose pose,
                               float x, float y, float z, float u, float v, int light) {
        consumer.addVertex(matrix4f, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMeteor entity) {
        return METEOR_TEXTURE;
    }
}
