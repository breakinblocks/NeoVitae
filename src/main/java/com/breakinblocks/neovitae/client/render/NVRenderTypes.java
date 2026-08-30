package com.breakinblocks.neovitae.client.render;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import com.breakinblocks.neovitae.NeoVitae;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class NVRenderTypes {

    public static final RenderPipeline LINES_SEE_THROUGH_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(NeoVitae.rl("pipeline/lines_see_through"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build();

    public static final RenderType LINES_SEE_THROUGH = RenderType.create(
            "neovitae_lines_see_through",
            RenderSetup.builder(LINES_SEE_THROUGH_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup());

    @SubscribeEvent
    static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(LINES_SEE_THROUGH_PIPELINE);
    }

    private NVRenderTypes() {}
}
