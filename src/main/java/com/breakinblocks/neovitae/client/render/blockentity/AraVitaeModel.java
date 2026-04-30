package com.breakinblocks.neovitae.client.render.blockentity;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class AraVitaeModel extends DefaultedBlockGeoModel<AraVitaeTile> {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/block/altar.png");

    public AraVitaeModel() {
        super(NeoVitae.rl("ara_vitae"));
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }
}
