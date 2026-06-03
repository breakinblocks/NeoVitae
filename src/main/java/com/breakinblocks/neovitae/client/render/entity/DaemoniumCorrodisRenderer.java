package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumCorrodisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCorrodisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumCorrodisRenderer extends GeoEntityRenderer<DaemoniumCorrodisEntity, EntityRenderState> {

    public DaemoniumCorrodisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumCorrodisModel());
        withRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
