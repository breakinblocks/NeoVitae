package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumRancorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumRancorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumRancorisRenderer extends GeoEntityRenderer<DaemoniumRancorisEntity, EntityRenderState> {

    public DaemoniumRancorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumRancorisModel());
        withRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
