package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumFervidisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumFervidisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumFervidisRenderer extends GeoEntityRenderer<DaemoniumFervidisEntity, EntityRenderState> {

    public DaemoniumFervidisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumFervidisModel());
    }
}
