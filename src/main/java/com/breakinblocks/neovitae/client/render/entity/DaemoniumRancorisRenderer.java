package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.client.render.entity.model.DaemoniumRancorisModel;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumRancorisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaemoniumRancorisRenderer extends GeoEntityRenderer<DaemoniumRancorisEntity> {

    public DaemoniumRancorisRenderer(EntityRendererProvider.Context context) {
        super(context, new DaemoniumRancorisModel());
    }
}
