package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumCruorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCruorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumCruorisRenderer extends GeoEntityRenderer<DaemoniumCruorisEntity> {

    public DaemoniumCruorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumCruorisModel());
        addRenderLayer(new NVEmissiveGeoLayer<>(this));
    }
}
