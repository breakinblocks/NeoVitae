package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumIgnisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumIgnisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumIgnisRenderer extends GeoEntityRenderer<DaemoniumIgnisEntity> {

    public DaemoniumIgnisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumIgnisModel());
        addRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
