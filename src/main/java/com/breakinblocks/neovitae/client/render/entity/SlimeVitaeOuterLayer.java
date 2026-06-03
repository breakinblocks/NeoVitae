package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;

public class SlimeVitaeOuterLayer extends RenderLayer<SlimeRenderState, SlimeModel> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/slime_vitae.png");

    private final SlimeModel model;

    public SlimeVitaeOuterLayer(RenderLayerParent<SlimeRenderState, SlimeModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new SlimeModel(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, SlimeRenderState state, float yRot, float xRot) {
        boolean glowing = state.appearsGlowing() && state.isInvisible;
        if (!state.isInvisible || glowing) {
            int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            collector.order(1)
                    .submitModel(
                            this.model,
                            state,
                            poseStack,
                            glowing ? RenderTypes.outline(TEXTURE) : RenderTypes.entityTranslucent(TEXTURE),
                            packedLight,
                            overlay,
                            -1,
                            null,
                            state.outlineColor,
                            null
                    );
        }
    }
}
