package com.breakinblocks.neovitae.client.render.entity;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class NVEmissiveGeoLayer<T extends Entity & GeoAnimatable, R extends EntityRenderState> extends AutoGlowingGeoLayer<T, Void, R> {

    public NVEmissiveGeoLayer(GeoRenderer<T, Void, R> renderer) {
        super(renderer);
    }

    @Override
    protected RenderType getRenderType(R renderState) {
        Identifier base = getRenderer().getTextureLocation(renderState);
        Identifier emissive = base.withPath(p -> p.replaceFirst("\\.png$", "_e.png"));
        return RenderTypes.entityTranslucentEmissive(emissive);
    }
}
