package com.breakinblocks.neovitae.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.client.render.blockentity.SpiritAccumulatorRenderer;

public class SpiritAccumulatorItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final float SCALE = 1.35f;

    public SpiritAccumulatorItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(SCALE, SCALE, SCALE);
        SpiritAccumulatorRenderer.renderCrystal(poseStack, bufferSource, packedLight, null, 0f, 0f);
        poseStack.popPose();
    }
}
