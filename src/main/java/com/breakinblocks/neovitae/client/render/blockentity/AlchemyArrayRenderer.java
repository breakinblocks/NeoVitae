package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.util.helper.ColorHelper;

public class AlchemyArrayRenderer implements BlockEntityRenderer<AlchemyArrayBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = NeoVitae.rl("textures/models/alchemyarrays/basearray.png");

    public AlchemyArrayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AlchemyArrayBlockEntity tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (tile.getLevel() == null) {
            return;
        }

        ResourceLocation texture = getTextureForTile(tile);
        if (texture == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0.5, 0.01, 0.5);

        Direction rotation = tile.getRotation();
        if (rotation != null) {
            float angle = switch (rotation) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }

        if (tile.isActive) {
            float rotationSpeed = 0.5f;
            float time = (tile.getLevel().getGameTime() + partialTick) * rotationSpeed;
            poseStack.mulPose(Axis.YP.rotationDegrees(time % 360));
        }

        poseStack.translate(-0.5, 0, -0.5);

        DyeColor dyeColor = tile.getArrayColor();
        int r = 255, g = 255, b = 255;
        if (dyeColor != null) {
            int rgb = ColorHelper.fromDye(dyeColor);
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;
        }

        renderArrayTexture(texture, poseStack, bufferSource, packedLight, r, g, b);

        poseStack.popPose();
    }

    private ResourceLocation getTextureForTile(AlchemyArrayBlockEntity tile) {
        ItemStack base = tile.getItem(0);
        ItemStack added = tile.getItem(1);

        if (base.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        if (added.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        AlchemyArrayInput input = new AlchemyArrayInput(base, added);

        return tile.getLevel().getRecipeManager()
                .getRecipeFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get(), input, tile.getLevel())
                .map(holder -> holder.value().getTexture())
                .orElse(DEFAULT_TEXTURE);
    }

    private void renderArrayTexture(ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int r, int g, int b) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));

        Matrix4f matrix = poseStack.last().pose();

        float minX = 0.0f;
        float maxX = 1.0f;
        float minZ = 0.0f;
        float maxZ = 1.0f;
        float y = 0.0f;

        int light = LightTexture.FULL_BRIGHT;

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        float nx = 0, ny = 1, nz = 0;

        buffer.addVertex(matrix, minX, y, minZ).setColor(r, g, b, 255).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, minX, y, maxZ).setColor(r, g, b, 255).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, maxZ).setColor(r, g, b, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, minZ).setColor(r, g, b, 255).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }
}
