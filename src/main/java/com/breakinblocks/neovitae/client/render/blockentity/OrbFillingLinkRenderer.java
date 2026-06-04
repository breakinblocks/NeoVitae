package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.blockentity.OrbFillingLinkBlockEntity;

public class OrbFillingLinkRenderer implements BlockEntityRenderer<OrbFillingLinkBlockEntity> {

    private static final float ITEM_Y = 0.55F;

    public OrbFillingLinkRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(OrbFillingLinkBlockEntity link, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = link.inv.getStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT);
        if (stack.isEmpty()) {
            return;
        }
        Level level = link.getLevel();
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        poseStack.translate(0.5F, ITEM_Y, 0.5F);
        float rotation = 720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.scale(0.4F, 0.4F, 0.4F);
        BakedModel bakedModel = renderer.getModel(stack, level, null, 1);
        renderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
        poseStack.popPose();
    }
}
