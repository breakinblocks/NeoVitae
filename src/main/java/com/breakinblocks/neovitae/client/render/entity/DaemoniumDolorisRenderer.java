package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumDolorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumDolorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumDolorisRenderer extends GeoEntityRenderer<DaemoniumDolorisEntity> {

    public DaemoniumDolorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumDolorisModel());
    }
}
