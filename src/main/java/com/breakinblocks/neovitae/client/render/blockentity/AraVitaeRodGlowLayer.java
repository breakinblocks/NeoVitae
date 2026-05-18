package com.breakinblocks.neovitae.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;

import java.util.Set;

public class AraVitaeRodGlowLayer extends GeoRenderLayer<AraVitaeTile> {

    private static final ResourceLocation GLOW_TEXTURE = NeoVitae.rl("textures/block/altar_glow.png");
    private static final Set<String> ROD_BONES = Set.of("rod", "rod1", "rod2", "rod3");

    public AraVitaeRodGlowLayer(GeoRenderer<AraVitaeTile> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(PoseStack poseStack, AraVitaeTile animatable, GeoBone bone, RenderType renderType,
                              MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                              int packedLight, int packedOverlay) {
        if (!animatable.isVisuallyActive()) return;
        if (!ROD_BONES.contains(bone.getName())) return;

        VertexConsumer glowBuf = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
        getRenderer().renderCubesOfBone(poseStack, bone, glowBuf, packedLight, packedOverlay, -1);
    }
}
