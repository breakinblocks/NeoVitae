package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.common.blockentity.MimicBlockEntity;

@OnlyIn(Dist.CLIENT)
public class MimicRenderer implements BlockEntityRenderer<MimicBlockEntity> {

    public MimicRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MimicBlockEntity mimic, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState mimicState = mimic.getMimic();
        if (mimicState == null || mimicState.isAir()) {
            mimicState = Blocks.STONE.defaultBlockState();
        }

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                mimicState, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
