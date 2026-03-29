package com.breakinblocks.neovitae.client.render.entity.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.entity.model.BloodShieldModel;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodShieldRenderer extends GeoEntityRenderer<BloodShieldEntity> {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/entity/blood_shield_overlay.png");
    private static final float SHIELD_SCALE = 5.0f * 0.65f * 0.8f;

    public BloodShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodShieldModel());

    }

    @Override
    public void preRender(PoseStack poseStack, BloodShieldEntity entity, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            poseStack.translate(0, 0.35, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            poseStack.scale(SHIELD_SCALE, SHIELD_SCALE, SHIELD_SCALE);
        }
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public RenderType getRenderType(BloodShieldEntity entity, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        float f = (entity.tickCount + partialTick) * 0.02f;
        float u = swirlNoise(1.2f * f);
        float v = swirlNoise(f + 456);
        return RenderType.energySwirl(TEXTURE, u, v);
    }

    @Override
    public int getPackedOverlay(BloodShieldEntity entity, float u, float partialTick) {
        return 0;
    }

    private static float swirlNoise(float f) {
        return (float) (Math.sin(f / 4) + 2 * Math.sin(f / 3) + 3 * Math.sin(f / 2) + 4 * Math.sin(f)) * 0.25f;
    }
}
