package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.mob.SlimeVitaeEntity;

public class SlimeVitaeRenderer extends MobRenderer<SlimeVitaeEntity, SlimeRenderState, SlimeModel> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/slime_vitae.png");

    public SlimeVitaeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new SlimeVitaeOuterLayer(this, context.getModelSet()));
    }

    @Override
    protected float getShadowRadius(SlimeRenderState state) {
        return state.size * 0.25F;
    }

    @Override
    protected void scale(SlimeRenderState state, PoseStack pose) {
        pose.scale(0.999F, 0.999F, 0.999F);
        pose.translate(0.0F, 0.001F, 0.0F);
        float size = state.size;
        float squish = state.squish / (size * 0.5F + 1.0F);
        float f = 1.0F / (squish + 1.0F);
        pose.scale(f * size, 1.0F / f * size, f * size);
    }

    @Override
    public Identifier getTextureLocation(SlimeRenderState state) {
        return TEXTURE;
    }

    @Override
    public SlimeRenderState createRenderState() {
        return new SlimeRenderState();
    }

    @Override
    public void extractRenderState(SlimeVitaeEntity entity, SlimeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.squish = Mth.lerp(partialTick, entity.oSquish, entity.squish);
        state.size = entity.getSize();
    }
}
