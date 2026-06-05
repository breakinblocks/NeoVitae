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
import com.breakinblocks.neovitae.common.blockentity.OrbFillingLinkBlockEntity;

public class OrbFillingLinkRenderer implements BlockEntityRenderer<OrbFillingLinkBlockEntity, OrbFillingLinkRenderer.State> {

    private static final float ITEM_Y = 0.55F;

    private final ItemModelResolver itemModelResolver;

    public OrbFillingLinkRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public boolean present;
        public float itemRotation;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(OrbFillingLinkBlockEntity link, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(link, s, partialTick, cameraPos, crumbling);
        Level level = link.getLevel();
        ItemStack stack = link.inv.getStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT);
        s.present = !stack.isEmpty();
        if (s.present) {
            this.itemModelResolver.updateForTopItem(s.item, stack, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            s.item.clear();
        }
        s.itemRotation = 720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL;
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!s.present) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, ITEM_Y, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(s.itemRotation));
        poseStack.scale(0.4F, 0.4F, 0.4F);
        s.item.submit(poseStack, collector, s.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
