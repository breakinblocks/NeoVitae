package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumCorrodisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCorrodisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumCorrodisRenderer extends GeoEntityRenderer<DaemoniumCorrodisEntity> {

    public DaemoniumCorrodisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumCorrodisModel());
    }
}
