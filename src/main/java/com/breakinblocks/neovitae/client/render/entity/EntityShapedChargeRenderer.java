package com.breakinblocks.neovitae.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import com.breakinblocks.neovitae.common.entity.projectile.EntityShapedCharge;

/**
 * Renderer for shaped charge projectiles.
 * Renders the block state of the charge as it flies through the air.
 */
@OnlyIn(Dist.CLIENT)
public class EntityShapedChargeRenderer extends EntityRenderer<EntityShapedCharge> {

    public EntityShapedChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(EntityShapedCharge entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlockState();
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level();
            if (blockState != level.getBlockState(entity.blockPosition()) &&
                    blockState.getRenderShape() != RenderShape.INVISIBLE) {

                poseStack.pushPose();

                BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
                poseStack.translate(-0.5D, 0.0D, -0.5D);

                BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
                blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());

                poseStack.popPose();
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityShapedCharge entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
