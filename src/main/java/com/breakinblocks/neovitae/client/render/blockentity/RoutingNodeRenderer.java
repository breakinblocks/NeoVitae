package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeBlockEntity;
import com.breakinblocks.neovitae.api.routing.*;

import java.util.List;

public class RoutingNodeRenderer<T extends BlockEntity & IRoutingNode> implements BlockEntityRenderer<T> {

    private static final int COLOR_INPUT = 0xFF4488FF;
    private static final int COLOR_OUTPUT = 0xFFFF8844;
    private static final int COLOR_MASTER = 0xFFFFD700;
    private static final int COLOR_GENERAL = 0xFFAAAAAA;

    private static final float BEAM_WIDTH = 0.03f;

    public RoutingNodeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T node, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = node.getLevel();
        if (level == null) return;

        List<BlockPos> connections = node.getConnected();
        if (connections.isEmpty()) return;

        BlockPos nodePos = node.getCurrentBlockPos();
        int color = getColorForNode(node);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);

        for (BlockPos targetPos : connections) {
            // Only render beams to positions with lower coordinates to avoid double-rendering
            // This ensures each beam is drawn only once between two connected nodes
            if (targetPos.compareTo(nodePos) > 0) {
                renderBeam(poseStack, buffer, nodePos, targetPos, color, packedLight);
            }
        }
    }

    private void renderBeam(PoseStack poseStack, VertexConsumer buffer, BlockPos source, BlockPos target, int color, int packedLight) {
        poseStack.pushPose();

        float dx = target.getX() - source.getX();
        float dy = target.getY() - source.getY();
        float dz = target.getZ() - source.getZ();

        float startX = 0.5f;
        float startY = 0.5f;
        float startZ = 0.5f;

        float endX = dx + 0.5f;
        float endY = dy + 0.5f;
        float endZ = dz + 0.5f;

        Matrix4f matrix = poseStack.last().pose();

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.001f) {
            poseStack.popPose();
            return;
        }

        float nx = dx / length;
        float ny = dy / length;
        float nz = dz / length;

        buffer.addVertex(matrix, startX, startY, startZ)
                .setColor(r, g, b, a)
                .setNormal(poseStack.last(), nx, ny, nz);
        buffer.addVertex(matrix, endX, endY, endZ)
                .setColor(r, g, b, a)
                .setNormal(poseStack.last(), nx, ny, nz);

        poseStack.popPose();
    }

    private int getColorForNode(T node) {
        if (node instanceof MasterRoutingNodeBlockEntity) {
            return COLOR_MASTER;
        } else if (node instanceof InputRoutingNodeBlockEntity) {
            return COLOR_INPUT;
        } else if (node instanceof OutputRoutingNodeBlockEntity) {
            return COLOR_OUTPUT;
        }
        return COLOR_GENERAL;
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
