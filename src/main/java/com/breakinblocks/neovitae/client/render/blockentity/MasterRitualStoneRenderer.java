package com.breakinblocks.neovitae.client.render.blockentity;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.types.RitualTormentNexus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MasterRitualStoneRenderer implements BlockEntityRenderer<MasterRitualStoneBlockEntity, MasterRitualStoneRenderer.State> {

    private static final Identifier RITUAL_TEXTURE = NeoVitae.rl("textures/particle/ritual.png");
    private static final float HALF = 1.682f;
    private static final int FULLBRIGHT = 0xF000F0;

    public MasterRitualStoneRenderer(BlockEntityRendererProvider.Context context) {}

    public static class State extends BlockEntityRenderState {
        public boolean tormentNexusActive;
        public float animationTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MasterRitualStoneBlockEntity tile, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderState.extractBase(tile, s, crumbling);
        s.tormentNexusActive = tile.isActive() && tile.getCurrentRitual() instanceof RitualTormentNexus;
        s.animationTicks = (tile.getLevel() != null ? tile.getLevel().getGameTime() : 0L) + partialTick;
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!s.tormentNexusActive) return;

        float rotation = (s.animationTicks * 0.5f) % 360f;
        int packedOverlay = OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        poseStack.translate(0.5, -1.99, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(RITUAL_TEXTURE), (pose, buf) -> {
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
        });

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
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
