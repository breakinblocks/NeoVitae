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
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

public class AlchemyArrayRenderer implements BlockEntityRenderer<AlchemyArrayTile> {

    private static final ResourceLocation DEFAULT_TEXTURE = NeoVitae.rl("textures/models/alchemyarrays/basearray.png");

    public AlchemyArrayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AlchemyArrayTile tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (tile.getLevel() == null) {
            return;
        }

        ResourceLocation texture = getTextureForTile(tile);
        if (texture == null) {
            return;
        }

        poseStack.pushPose();

        // Move to center of block
        poseStack.translate(0.5, 0.01, 0.5);

        // Apply rotation based on tile's rotation
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

        // Add subtle rotation animation when active
        if (tile.isActive) {
            float rotationSpeed = 0.5f;
            float time = (tile.getLevel().getGameTime() + partialTick) * rotationSpeed;
            poseStack.mulPose(Axis.YP.rotationDegrees(time % 360));
        }

        poseStack.translate(-0.5, 0, -0.5);

        // Render the array texture as a flat quad on the ground
        renderArrayTexture(texture, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    private ResourceLocation getTextureForTile(AlchemyArrayTile tile) {
        ItemStack base = tile.getItem(0);
        ItemStack added = tile.getItem(1);

        // If no items at all, show the base array texture (the alchemy array was just placed)
        if (base.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        // If only base item is present, show default array texture
        if (added.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        // Both items present - look up recipe for specific texture
        AlchemyArrayInput input = new AlchemyArrayInput(base, added);

        // Look up recipe
        return tile.getLevel().getRecipeManager()
                .getRecipeFor(BMRecipes.ALCHEMY_ARRAY_TYPE.get(), input, tile.getLevel())
                .map(holder -> holder.value().getTexture())
                .orElse(DEFAULT_TEXTURE);
    }

    private void renderArrayTexture(ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // Use translucent render type for proper alpha handling
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(texture));

        Matrix4f matrix = poseStack.last().pose();

        // Render a 1x1 quad on the ground
        float minX = 0.0f;
        float maxX = 1.0f;
        float minZ = 0.0f;
        float maxZ = 1.0f;
        float y = 0.0f;

        // Full brightness for the array
        int light = LightTexture.FULL_BRIGHT;

        // UV coordinates for the full texture
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        // Normal facing up
        float nx = 0, ny = 1, nz = 0;

        // Render quad facing up (counter-clockwise winding for proper face culling)
        buffer.addVertex(matrix, minX, y, minZ).setColor(255, 255, 255, 255).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, minX, y, maxZ).setColor(255, 255, 255, 255).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, maxZ).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
        buffer.addVertex(matrix, maxX, y, minZ).setColor(255, 255, 255, 255).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz);
    }
}
