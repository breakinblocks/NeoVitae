package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;

/**
 * Renderer for throwing dagger projectiles.
 * Renders the item spinning as it flies through the air.
 */
@OnlyIn(Dist.CLIENT)
public class EntityThrowingDaggerRenderer<T extends AbstractEntityThrowingDagger> extends EntityRenderer<T> {

    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public EntityThrowingDaggerRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.fullBright = fullBright;
    }

    public EntityThrowingDaggerRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(this.scale, this.scale, this.scale);

        // Rotate based on entity's rotation
        // Yaw rotation (horizontal aiming)
        poseStack.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        // Pitch rotation (vertical aiming) - offset so tip points forward/down
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) - 75F));

        this.itemRenderer.renderStatic(
                entity.getItem(),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
