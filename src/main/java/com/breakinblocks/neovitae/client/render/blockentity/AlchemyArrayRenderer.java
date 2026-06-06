// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.util.helper.ColorHelper;

public class AlchemyArrayRenderer implements BlockEntityRenderer<AlchemyArrayBlockEntity, AlchemyArrayRenderer.State> {

    private static final Identifier DEFAULT_TEXTURE = NeoVitae.rl("textures/models/alchemyarrays/basearray.png");

    public AlchemyArrayRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class State extends BlockEntityRenderState {
        public boolean visible;
        public Identifier texture = DEFAULT_TEXTURE;
        public @Nullable Direction rotation;
        public boolean active;
        public float animationTicks;
        public int r = 255, g = 255, b = 255;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(AlchemyArrayBlockEntity tile, State s, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(tile, s, partialTick, cameraPos, crumbling);

        if (tile.getLevel() == null) {
            s.visible = false;
            return;
        }
        Identifier texture = getTextureForTile(tile);
        if (texture == null) {
            s.visible = false;
            return;
        }

        s.visible = true;
        s.texture = texture;
        s.rotation = tile.getRotation();
        s.active = tile.isActive;
        s.animationTicks = tile.getLevel().getGameTime() + partialTick;

        DyeColor dyeColor = tile.getArrayColor();
        if (dyeColor != null) {
            int rgb = ColorHelper.fromDye(dyeColor);
            s.r = (rgb >> 16) & 0xFF;
            s.g = (rgb >> 8) & 0xFF;
            s.b = rgb & 0xFF;
        } else {
            s.r = s.g = s.b = 255;
        }
    }

    @Override
    public void submit(State s, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!s.visible) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.01, 0.5);

        if (s.rotation != null) {
            float angle = switch (s.rotation) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }

        if (s.active) {
            float time = s.animationTicks * 0.5f;
            poseStack.mulPose(Axis.YP.rotationDegrees(time % 360));
        }

        poseStack.translate(-0.5, 0, -0.5);

        submitArrayQuad(s.texture, poseStack, collector, s.r, s.g, s.b, 255);

        poseStack.popPose();
    }

    private Identifier getTextureForTile(AlchemyArrayBlockEntity tile) {
        ItemStack base = tile.getItem(0);
        ItemStack added = tile.getItem(1);

        if (base.isEmpty() || added.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        Identifier cached = tile.getCachedTexture();
        return cached != null ? cached : DEFAULT_TEXTURE;
    }

    public static void submitArrayQuad(Identifier texture, PoseStack poseStack, SubmitNodeCollector collector,
                                       int r, int g, int b, int a) {
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture),
                (pose, consumer) -> drawArrayQuad(pose.pose(), consumer, r, g, b, a));
    }

    private static void drawArrayQuad(Matrix4f matrix, VertexConsumer buffer, int r, int g, int b, int a) {
        float minX = 0.0f, maxX = 1.0f, minZ = 0.0f, maxZ = 1.0f, y = 0.0f;
        int light = LightCoordsUtil.FULL_BRIGHT;
        float u0 = 0.0f, u1 = 1.0f, v0 = 0.0f, v1 = 1.0f;
        float nx = 0, ny = 1, nz = 0;

        buffer.addVertex(matrix, minX, y, minZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, minX, y, maxZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, maxZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, minZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }
}
