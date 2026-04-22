package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;

public class HellfireForgeRenderer implements BlockEntityRenderer<HellfireForgeBlockEntity, HellfireForgeRenderer.State> {

    private static final float CORNER_OFFSET = 1 + 3 / 16F;
    private static final int SLOT_COUNT = 6;

    private final ItemModelResolver itemModelResolver;

    public HellfireForgeRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[SLOT_COUNT];
        public final boolean[] present = new boolean[SLOT_COUNT];
        public float itemRotation;

        public State() {
            for (int i = 0; i < SLOT_COUNT; i++) items[i] = new ItemStackRenderState();
        }
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(HellfireForgeBlockEntity forge, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(forge, s, partialTick, cameraPos, crumbling);
        Level level = forge.getLevel();
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = forge.inv.getStackInSlot(i);
            s.present[i] = !stack.isEmpty();
            if (s.present[i]) {
                this.itemModelResolver.updateForTopItem(s.items[i], stack, ItemDisplayContext.FIXED, level, null, i);
            } else {
                s.items[i].clear();
            }
        }
        s.itemRotation = 720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL;
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submitSlot(s, HellfireForgeBlockEntity.SOUTH, 1 / 16F, CORNER_OFFSET, 15 / 16F, poseStack, collector);
        submitSlot(s, HellfireForgeBlockEntity.WEST, 1 / 16F, CORNER_OFFSET, 1 / 16F, poseStack, collector);
        submitSlot(s, HellfireForgeBlockEntity.NORTH, 15 / 16F, CORNER_OFFSET, 1 / 16F, poseStack, collector);
        submitSlot(s, HellfireForgeBlockEntity.EAST, 15 / 16F, CORNER_OFFSET, 15 / 16F, poseStack, collector);
        submitSlot(s, HellfireForgeBlockEntity.GEM_SLOT, 0.5F, 1, 0.5F, poseStack, collector);
        submitSlot(s, HellfireForgeBlockEntity.OUTPUT_SLOT, 0.5F, 1.5F, 0.5F, poseStack, collector);
    }

    private void submitSlot(State s, int slotIndex, float x, float y, float z, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!s.present[slotIndex]) return;
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(s.itemRotation));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        s.items[slotIndex].submit(poseStack, collector, s.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
