package com.breakinblocks.neovitae.client.render.entity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class NVEmissiveGeoLayer<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T> {

    public NVEmissiveGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    protected RenderType getRenderType(T animatable, MultiBufferSource bufferSource) {
        ResourceLocation base = getTextureResource(animatable);
        ResourceLocation emissive = base.withPath(p -> p.replaceFirst("\\.png$", "_e.png"));
        return RenderType.eyes(emissive);
    }
}
