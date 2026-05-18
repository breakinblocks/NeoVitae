package com.breakinblocks.neovitae.client.render.blockentity;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.AraVitaeBlockItem;

public class AraVitaeBlockItemRenderer extends GeoItemRenderer<AraVitaeBlockItem> {

    private static final DataTicket<ItemDisplayContext> PERSPECTIVE =
            DataTicket.create("nv_ara_vitae_perspective", ItemDisplayContext.class);

    public AraVitaeBlockItemRenderer() {
        super(new Model());
    }

    @Override
    public void captureDefaultRenderState(AraVitaeBlockItem item, GeoItemRenderer.RenderData data,
                                           GeoRenderState state, float partialTick) {
        super.captureDefaultRenderState(item, data, state, partialTick);
        state.addGeckolibData(PERSPECTIVE, data.renderPerspective());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> info) {
        PoseStack poseStack = info.poseStack();
        ItemDisplayContext perspective = info.renderState().getOrDefaultGeckolibData(PERSPECTIVE, ItemDisplayContext.NONE);

        poseStack.translate(0.5f, 0.5f, 0.5f);

        switch (perspective) {
            case GUI -> {
                poseStack.translate(-0.4f, 0.0f, 0f);
                poseStack.mulPose(Axis.XP.rotationDegrees(30f));
                poseStack.mulPose(Axis.YP.rotationDegrees(225f));
                poseStack.scale(0.625f, 0.625f, 0.625f);
            }
            case GROUND -> {
                poseStack.translate(0f, 3f / 16f, 0f);
                poseStack.scale(0.25f, 0.25f, 0.25f);
            }
            case FIXED -> poseStack.scale(0.5f, 0.5f, 0.5f);
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(75f));
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
                poseStack.translate(0f, 2.5f / 16f, 0f);
                poseStack.scale(1.0f, 1.0f, 1.0f);
            }

            case FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(45f));
                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(225f));
                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            default -> {
            }
        }

        poseStack.translate(-0.5f, -0.5f, -0.5f);
    }

    private static class Model extends DefaultedBlockGeoModel<AraVitaeBlockItem> {
        private static final Identifier TEXTURE = NeoVitae.rl("textures/block/altar.png");

        Model() {
            super(NeoVitae.rl("ara_vitae"));
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return TEXTURE;
        }
    }
}
