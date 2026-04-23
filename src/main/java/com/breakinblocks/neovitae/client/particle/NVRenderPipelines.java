package com.breakinblocks.neovitae.client.particle;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import com.breakinblocks.neovitae.NeoVitae;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public final class NVRenderPipelines {
    // BlendFunction.OVERLAY = (SRC_ALPHA, ONE, ONE, ZERO): the additive-blend recipe that
    // the pre-port BloodFlameRenderType used before the 26.1 ParticleRenderType API rewrite.
    public static final RenderPipeline ADDITIVE_PARTICLE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "pipeline/additive_particle"))
            .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
            .build();

    public static final SingleQuadParticle.Layer ADDITIVE_LAYER = new SingleQuadParticle.Layer(
            true, AtlasIds.PARTICLES, ADDITIVE_PARTICLE);

    @SubscribeEvent
    static void registerPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(ADDITIVE_PARTICLE);
    }

    private NVRenderPipelines() {}
}
