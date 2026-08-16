package com.breakinblocks.neovitae.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import com.breakinblocks.neovitae.client.render.blockentity.SpiritAccumulatorRenderer;

import java.util.function.Consumer;

public class SpiritAccumulatorSpecialRenderer implements NoDataSpecialModelRenderer {

    private static final float SCALE = 1.35f;

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords,
                       boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(SCALE, SCALE, SCALE);
        SpiritAccumulatorRenderer.submitCrystal(poseStack, collector, lightCoords, null, 0f, 0f);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        float w = SpiritAccumulatorRenderer.halfWidth() * SCALE;
        float top = 0.5f + SpiritAccumulatorRenderer.topHeight() * SCALE;
        float bottom = 0.5f - SpiritAccumulatorRenderer.bottomHeight() * SCALE;
        for (float x : new float[]{0.5f - w, 0.5f + w}) {
            for (float z : new float[]{0.5f - w, 0.5f + w}) {
                output.accept(new Vector3f(x, bottom, z));
                output.accept(new Vector3f(x, top, z));
            }
        }
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpiritAccumulatorSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new SpiritAccumulatorSpecialRenderer();
        }
    }
}
