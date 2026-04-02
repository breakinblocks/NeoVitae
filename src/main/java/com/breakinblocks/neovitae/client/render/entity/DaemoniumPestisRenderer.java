package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumPestisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumPestisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumPestisRenderer extends GeoEntityRenderer<DaemoniumPestisEntity> {

    public DaemoniumPestisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumPestisModel());
    }
}
