package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.api.routing.*;

import java.util.ArrayList;
import java.util.List;

public class RoutingNodeRenderer<T extends BlockEntity & IRoutingNode>
        implements BlockEntityRenderer<T, RoutingNodeRenderer.State> {

    private static final int COLOR_INPUT = 0xFF4488FF;
    private static final int COLOR_OUTPUT = 0xFFFF8844;
    private static final int COLOR_MASTER = 0xFFFFD700;
    private static final int COLOR_GENERAL = 0xFFAAAAAA;

    public RoutingNodeRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        public int color = COLOR_GENERAL;
        public final List<BlockPos> outgoingBeams = new ArrayList<>();
        public BlockPos sourcePos = BlockPos.ZERO;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public void extractRenderState(T node, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(node, s, partialTick, cameraPos, crumbling);
        s.color = getColorForNode(node);
        s.sourcePos = node.getCurrentBlockPos();
        s.outgoingBeams.clear();
        for (BlockPos target : node.getConnected()) {
            if (target.compareTo(s.sourcePos) > 0) {
                s.outgoingBeams.add(target);
            }
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (s.outgoingBeams.isEmpty()) return;

        collector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, buffer) -> {
            Matrix4f matrix = pose.pose();
            float r = ((s.color >> 16) & 0xFF) / 255f;
            float g = ((s.color >> 8) & 0xFF) / 255f;
            float b = (s.color & 0xFF) / 255f;
            float a = ((s.color >>> 24) & 0xFF) / 255f;

            for (BlockPos target : s.outgoingBeams) {
                float dx = target.getX() - s.sourcePos.getX();
                float dy = target.getY() - s.sourcePos.getY();
                float dz = target.getZ() - s.sourcePos.getZ();

                float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (length < 0.001f) continue;

                float nx = dx / length;
                float ny = dy / length;
                float nz = dz / length;

                buffer.addVertex(matrix, 0.5f, 0.5f, 0.5f)
                        .setColor(r, g, b, a)
                        .setNormal(pose, nx, ny, nz);
                buffer.addVertex(matrix, dx + 0.5f, dy + 0.5f, dz + 0.5f)
                        .setColor(r, g, b, a)
                        .setNormal(pose, nx, ny, nz);
            }
        });
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
}
