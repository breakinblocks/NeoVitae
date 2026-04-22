package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumIgnisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumIgnisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumIgnisRenderer extends GeoEntityRenderer<DaemoniumIgnisEntity, EntityRenderState> {

    public DaemoniumIgnisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumIgnisModel());
    }
}
