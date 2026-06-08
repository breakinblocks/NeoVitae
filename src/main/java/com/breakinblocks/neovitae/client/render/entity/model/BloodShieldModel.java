package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class BloodShieldModel extends GeoModel<BloodShieldEntity> {

    private static final Identifier MODEL = NeoVitae.rl("geckolib/models/entity/blood_shield.geo.json");
    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/blood_shield_overlay.png");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(BloodShieldEntity entity) {
        return null;
    }
}
