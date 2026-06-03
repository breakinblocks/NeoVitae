package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumPestisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumPestisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumPestisRenderer extends GeoEntityRenderer<DaemoniumPestisEntity, EntityRenderState> {

    public DaemoniumPestisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumPestisModel());
        withRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
