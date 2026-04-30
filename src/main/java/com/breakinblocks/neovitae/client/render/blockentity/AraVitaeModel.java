package com.breakinblocks.neovitae.client.render.blockentity;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AraVitaeModel extends GeoModel<AraVitaeTile> {

    private static final ResourceLocation MODEL = NeoVitae.rl("geo/block/ara_vitae.geo.json");
    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/block/altar.png");
    private static final ResourceLocation ANIM = NeoVitae.rl("animations/block/ara_vitae.animation.json");

    @Override
    public ResourceLocation getModelResource(AraVitaeTile animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AraVitaeTile animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(AraVitaeTile animatable) {
        return ANIM;
    }
}
