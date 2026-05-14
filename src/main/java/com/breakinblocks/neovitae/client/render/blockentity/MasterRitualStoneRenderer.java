package com.breakinblocks.neovitae.client.render.blockentity;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.types.RitualTormentNexus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MasterRitualStoneRenderer implements BlockEntityRenderer<MasterRitualStoneBlockEntity> {

    private static final ResourceLocation RITUAL_TEXTURE = NeoVitae.rl("textures/particle/ritual.png");
    private static final float HALF = 1.682f;
    private static final int FULLBRIGHT = 0xF000F0;

    public MasterRitualStoneRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MasterRitualStoneBlockEntity tile, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!tile.isActive()) return;
        if (!(tile.getCurrentRitual() instanceof RitualTormentNexus)) return;
        if (tile.getLevel() == null) return;

        float ticks = tile.getLevel().getGameTime() + partialTick;
        float rotation = (ticks * 0.5f) % 360f;

        poseStack.pushPose();
        poseStack.translate(0.5, -1.99, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));

        VertexConsumer buf = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(RITUAL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        Vector3f normUp = new Vector3f();
        pose.transformNormal(0, 1, 0, normUp);
        Vector3f normDown = new Vector3f();
        pose.transformNormal(0, -1, 0, normDown);

        buf.addVertex(matrix, -HALF, 0, -HALF).setColor(180, 60, 240, 96).setUv(0, 0).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, -HALF, 0, HALF).setColor(180, 60, 240, 96).setUv(0, 1).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, HALF, 0, HALF).setColor(180, 60, 240, 96).setUv(1, 1).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normUp.x, normUp.y, normUp.z);
        buf.addVertex(matrix, HALF, 0, -HALF).setColor(180, 60, 240, 96).setUv(1, 0).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normUp.x, normUp.y, normUp.z);

        buf.addVertex(matrix, HALF, 0, -HALF).setColor(180, 60, 240, 96).setUv(1, 0).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, HALF, 0, HALF).setColor(180, 60, 240, 96).setUv(1, 1).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, -HALF, 0, HALF).setColor(180, 60, 240, 96).setUv(0, 1).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normDown.x, normDown.y, normDown.z);
        buf.addVertex(matrix, -HALF, 0, -HALF).setColor(180, 60, 240, 96).setUv(0, 0).setOverlay(packedOverlay).setLight(FULLBRIGHT).setNormal(normDown.x, normDown.y, normDown.z);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(MasterRitualStoneBlockEntity be) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(MasterRitualStoneBlockEntity be) {
        BlockPos pos = be.getBlockPos();
        return new AABB(
                pos.getX() + 0.5 - HALF, pos.getY() - 2.0, pos.getZ() + 0.5 - HALF,
                pos.getX() + 0.5 + HALF, pos.getY() + 1.0, pos.getZ() + 0.5 + HALF);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
