package com.breakinblocks.neovitae.client.render.blockentity;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class AraVitaeModel extends GeoModel<AraVitaeTile> {

    private static final Identifier MODEL = NeoVitae.rl("geo/block/ara_vitae.geo.json");
    private static final Identifier TEXTURE = NeoVitae.rl("textures/block/altar.png");
    private static final Identifier ANIM = NeoVitae.rl("animations/block/ara_vitae.animation.json");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(AraVitaeTile animatable) {
        return ANIM;
    }
}
