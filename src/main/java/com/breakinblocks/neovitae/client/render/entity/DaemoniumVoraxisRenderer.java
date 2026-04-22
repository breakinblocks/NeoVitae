package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumVoraxisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumVoraxisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class DaemoniumVoraxisRenderer extends GeoEntityRenderer<DaemoniumVoraxisEntity, EntityRenderState> {

    public DaemoniumVoraxisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumVoraxisModel());
    }
}
