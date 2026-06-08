package com.breakinblocks.neovitae.client.render.entity.shield;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.entity.model.BloodShieldModel;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class BloodShieldRenderer extends GeoEntityRenderer<BloodShieldEntity, BloodShieldRenderer.State> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/blood_shield_overlay.png");
    private static final float SHIELD_SCALE = 5.0f * 0.65f * 0.8f;

    public BloodShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodShieldModel());
        this.withScale(SHIELD_SCALE);
    }

    public static class State extends EntityRenderState {
    }

    @Override
    public State createRenderState(BloodShieldEntity entity, Void v) {
        return new State();
    }

    @Override
    public RenderType getRenderType(State state, Identifier texture) {
        float time = (System.currentTimeMillis() % 10000L) / 5000F;
        return RenderTypes.energySwirl(TEXTURE, time, time);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<State> info) {
        super.adjustRenderPose(info);
        info.poseStack().translate(0.0, 0.35, 0.0);
    }
}
