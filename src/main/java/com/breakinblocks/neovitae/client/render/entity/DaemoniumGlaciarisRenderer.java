package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumGlaciarisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumGlaciarisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumGlaciarisRenderer extends GeoEntityRenderer<DaemoniumGlaciarisEntity, EntityRenderState> {

    public DaemoniumGlaciarisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumGlaciarisModel());
    }
}
