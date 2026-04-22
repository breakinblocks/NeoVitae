package com.breakinblocks.neovitae.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;

public class SlimeVitaeRenderer extends SlimeRenderer {

    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/slime_vitae.png");

    public SlimeVitaeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(SlimeRenderState state) {
        return TEXTURE;
    }
}
