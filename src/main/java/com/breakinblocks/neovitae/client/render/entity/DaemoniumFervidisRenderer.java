package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumFervidisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumFervidisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumFervidisRenderer extends GeoEntityRenderer<DaemoniumFervidisEntity> {

    public DaemoniumFervidisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumFervidisModel());
    }
}
