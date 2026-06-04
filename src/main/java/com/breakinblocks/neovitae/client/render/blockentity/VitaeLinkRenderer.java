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
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class VitaeLinkRenderer implements BlockEntityRenderer<VitaeLinkBlockEntity, VitaeLinkRenderer.State> {

    private static final float ITEM_Y = 0.55F;
    private static final int DRIP_COLOR = 0x990011;

    private final ItemModelResolver itemModelResolver;

    public VitaeLinkRenderer(BlockEntityRendererProvider.Context context) {
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
    public void extractRenderState(VitaeLinkBlockEntity link, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(link, s, partialTick, cameraPos, crumbling);
        Level level = link.getLevel();
        boolean crafting = link.isClientCrafting();
        ItemStack stack = crafting
                ? link.inv.getStackInSlot(VitaeLinkBlockEntity.INPUT_SLOT)
                : link.inv.getStackInSlot(VitaeLinkBlockEntity.OUTPUT_SLOT);
        s.present = !stack.isEmpty();
        if (s.present) {
            this.itemModelResolver.updateForTopItem(s.item, stack, ItemDisplayContext.FIXED, level, null, 0);
        } else {
            s.item.clear();
        }
        s.itemRotation = 720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL;

        if (crafting && level != null && (level.getGameTime() & 1L) == 0L) {
            emitDrips(link, level);
        }
    }

    private void emitDrips(VitaeLinkBlockEntity link, Level level) {
        var rng = level.getRandom();
        var pos = link.getBlockPos();
        double x = pos.getX() + 0.5 + (rng.nextDouble() - 0.5) * 0.25;
        double y = pos.getY() + ITEM_Y - 0.1;
        double z = pos.getZ() + 0.5 + (rng.nextDouble() - 0.5) * 0.25;
        level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), DRIP_COLOR),
                x, y, z, 0.0, -0.04, 0.0);
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
