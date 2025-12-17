package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

/**
 * Renderer for blood light projectiles.
 * Extends ThrownItemRenderer to render as a thrown item.
 */
public class BloodLightRenderer<T extends Entity & ItemSupplier> extends ThrownItemRenderer<T> {

    public BloodLightRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
