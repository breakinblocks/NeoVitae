package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumDolorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumDolorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumDolorisRenderer extends GeoEntityRenderer<DaemoniumDolorisEntity, EntityRenderState> {

    public DaemoniumDolorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumDolorisModel());
        withRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
