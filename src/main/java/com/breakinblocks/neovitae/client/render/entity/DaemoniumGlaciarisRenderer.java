package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumGlaciarisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumGlaciarisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumGlaciarisRenderer extends GeoEntityRenderer<DaemoniumGlaciarisEntity> {

    public DaemoniumGlaciarisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumGlaciarisModel());
    }
}
