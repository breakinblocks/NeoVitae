package com.breakinblocks.neovitae.client.particle;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import com.breakinblocks.neovitae.NeoVitae;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class NVRenderPipelines {
    // Custom shaders drop vanilla's alpha < 0.1 discard so soft-alpha edges blend
    // instead of clipping to squares. writeDepth is off so overlapping additive
    // particles don't z-fight against each other.
    private static final DepthStencilState NO_DEPTH_WRITE =
            new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false);

    public static final RenderPipeline ADDITIVE_PARTICLE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "pipeline/additive_particle"))
            .withVertexShader(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "core/additive_particle"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "core/additive_particle"))
            .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
            .withDepthStencilState(NO_DEPTH_WRITE)
            .build();

    public static final SingleQuadParticle.Layer ADDITIVE_LAYER = new SingleQuadParticle.Layer(
            true, TextureAtlas.LOCATION_PARTICLES, ADDITIVE_PARTICLE);

    @SubscribeEvent
    static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(ADDITIVE_PARTICLE);
    }

    private NVRenderPipelines() {}
}
