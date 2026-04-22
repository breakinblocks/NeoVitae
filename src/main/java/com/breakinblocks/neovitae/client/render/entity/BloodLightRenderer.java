package com.breakinblocks.neovitae.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class BloodLightRenderer<T extends Entity & ItemSupplier> extends ThrownItemRenderer<T> {

    public BloodLightRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
