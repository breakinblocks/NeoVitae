package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumVoraxisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumVoraxisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumVoraxisRenderer extends GeoEntityRenderer<DaemoniumVoraxisEntity> {

    public DaemoniumVoraxisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumVoraxisModel());
    }
}
