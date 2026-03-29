package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.util.helper.RenderHelper;

public class AraVitaeRenderer implements BlockEntityRenderer<AraVitaeTile> {

    private static final ResourceLocation RITUAL_TEXTURE = NeoVitae.rl("textures/particle/ritual.png");

    public AraVitaeRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(AraVitaeTile tileAltar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack inputStack = tileAltar.inv.getStackInSlot(0);
        this.renderItem(inputStack, tileAltar.getLevel(), poseStack, bufferSource, packedLight, packedOverlay);

        if (tileAltar.isVisuallyActive()) {
            renderRitualCircle(tileAltar, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            if (tileAltar.getTier() >= 4) {
                renderHellforgedBeams(tileAltar, partialTick, poseStack, bufferSource);
            }
        }

        float level = (float) tileAltar.getMainTank() / (float) tileAltar.getMainCapacity();
        if (level == 0) {
            return;
        }
        this.renderFluid(level, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderRitualCircle(AraVitaeTile altar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float ticks = altar.getTicks() + partialTick;
        float rotation = (ticks * 0.5f) % 360f;

        poseStack.pushPose();
        poseStack.translate(0.5, -0.99, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        float half = 1.125f;
        VertexConsumer buf = bufferSource.getBuffer(RenderType.entityTranslucent(RITUAL_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Vector3f norm = new Vector3f();
        pose.transformNormal(0, 1, 0, norm);

        int color = 0xCCFFFFFF;
        int fullbright = 0xF000F0;

        RenderHelper.addVertex(buf, matrix, -half, 0, -half, 0, 0, color, fullbright, packedOverlay, norm);
        RenderHelper.addVertex(buf, matrix, -half, 0, half, 0, 1, color, fullbright, packedOverlay, norm);
        RenderHelper.addVertex(buf, matrix, half, 0, half, 1, 1, color, fullbright, packedOverlay, norm);
        RenderHelper.addVertex(buf, matrix, half, 0, -half, 1, 0, color, fullbright, packedOverlay, norm);

        poseStack.popPose();
    }

    private void renderItem(ItemStack inputStack, Level level , PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        //inputStack = new ItemStack(Blocks.DIAMOND_BLOCK);
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (!inputStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1, 0.5);
            float rotation = (float) (720.0F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            BakedModel bakedModel = itemRenderer.getModel(inputStack, level, (LivingEntity) null, 1);
            itemRenderer.render(inputStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
            poseStack.popPose();
        }
    }

    private static final int[][] T4_CAPS = {{8, -4, 8}, {8, -4, -8}, {-8, -4, 8}, {-8, -4, -8}};
    private static final int BEAM_COLOR = 0xFFFFFF;

    private void renderHellforgedBeams(AraVitaeTile altar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        long gameTime = altar.getLevel().getGameTime();
        for (int[] cap : T4_CAPS) {
            poseStack.pushPose();
            poseStack.translate(cap[0], cap[1], cap[2]);
            BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, BeaconRenderer.BEAM_LOCATION,
                    partialTick, 0.2f, gameTime, 0, 1024, BEAM_COLOR, 0.15f, 0.25f);
            poseStack.popPose();
        }
    }

    private void renderFluid(float fluidLevel, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        IClientFluidTypeExtensions fluidClientInfo = IClientFluidTypeExtensions.of(NVFluids.ESSENTIA_VITAE_TYPE.get());
        RenderType blockRenderType = ItemBlockRenderTypes.getRenderLayer(NVFluids.ESSENTIA_VITAE_SOURCE.get().defaultFluidState());
        TextureAtlasSprite texture = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidClientInfo.getStillTexture());
        int tintColour = fluidClientInfo.getTintColor();

        VertexConsumer buf = bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(blockRenderType, false));
        float minHeight = 8F/16F;
        float maxHeight = 12F/16F;
        float start = 3F/16F;
        float end = 13F/16F;
        float height = minHeight + fluidLevel * (maxHeight - minHeight);

        Vector3f norm = new Vector3f();
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float u0 = texture.getU0();
        float u1 = texture.getU1();

        float v0 = texture.getV0();
        float v1 = texture.getV1();

        pose.transformNormal(0, 1, 0, norm);
        RenderHelper.addVertex(buf, matrix, end, height, end, u0, v0, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, end, height, start, u0, v1, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, start, height, start, u1, v1, tintColour, light, overlay, norm);
        RenderHelper.addVertex(buf, matrix, start, height, end, u1, v0, tintColour, light, overlay, norm);
    }
}
