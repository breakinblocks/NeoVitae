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
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class VitaeLinkRenderer implements BlockEntityRenderer<VitaeLinkBlockEntity> {

    private static final float ITEM_Y = 0.55F;
    private static final int DRIP_COLOR = 0x990011;

    public VitaeLinkRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(VitaeLinkBlockEntity link, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        boolean crafting = link.isClientCrafting();
        ItemStack stack = crafting
                ? link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT)
                : link.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
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

        if (crafting) {
            emitDrips(link, level);
        }
    }

    private void emitDrips(VitaeLinkBlockEntity link, Level level) {
        if (level == null || (level.getGameTime() & 1L) != 0L) {
            return;
        }
        var rng = level.random;
        var pos = link.getBlockPos();
        double x = pos.getX() + 0.5 + (rng.nextDouble() - 0.5) * 0.25;
        double y = pos.getY() + ITEM_Y - 0.1;
        double z = pos.getZ() + 0.5 + (rng.nextDouble() - 0.5) * 0.25;
        level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), DRIP_COLOR),
                x, y, z, 0.0, -0.04, 0.0);
    }
}
