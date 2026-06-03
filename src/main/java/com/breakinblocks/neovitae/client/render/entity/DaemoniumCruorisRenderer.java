package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumCruorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCruorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumCruorisRenderer extends GeoEntityRenderer<DaemoniumCruorisEntity, EntityRenderState> {

    public DaemoniumCruorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumCruorisModel());
        withRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
