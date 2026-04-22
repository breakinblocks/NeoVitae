package com.breakinblocks.neovitae.client.render.entity.shield;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.entity.model.BloodShieldModel;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class BloodShieldRenderer extends GeoEntityRenderer<BloodShieldEntity, EntityRenderState> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/blood_shield_overlay.png");

    public BloodShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodShieldModel());
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        float time = (System.currentTimeMillis() % 10000L) / 5000F;
        return RenderTypes.energySwirl(TEXTURE, time, time);
    }
}
