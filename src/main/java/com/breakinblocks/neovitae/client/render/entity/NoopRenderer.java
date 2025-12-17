package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * A no-op renderer for entities that handle their own visual representation
 * (e.g., through particles) and don't need standard rendering.
 */
@OnlyIn(Dist.CLIENT)
public class NoopRenderer<T extends Entity> extends EntityRenderer<T> {

    public NoopRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        // No rendering - entity handles its own visuals via particles
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null; // No texture needed
    }
}
