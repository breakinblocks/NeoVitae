package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodShieldModel extends GeoModel<BloodShieldEntity> {

    private static final ResourceLocation MODEL = NeoVitae.rl("geo/entity/blood_shield.geo.json");
    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/entity/blood_shield_overlay.png");

    @Override
    public ResourceLocation getModelResource(BloodShieldEntity entity) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BloodShieldEntity entity) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BloodShieldEntity entity) {
        return null;
    }
}
