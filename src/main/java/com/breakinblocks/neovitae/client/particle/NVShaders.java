package com.breakinblocks.neovitae.client.particle;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import com.breakinblocks.neovitae.NeoVitae;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NVShaders {

    private static ShaderInstance additiveParticle;

    private NVShaders() {}

    public static ShaderInstance additiveParticle() {
        return additiveParticle;
    }

    @SubscribeEvent
    static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "additive_particle"),
                        DefaultVertexFormat.PARTICLE
                ),
                shader -> additiveParticle = shader
        );
    }
}
