package com.breakinblocks.neovitae.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;
import com.breakinblocks.neovitae.NeoVitae;

public class SlimeVitaeRenderer extends SlimeRenderer {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/entity/slime_vitae.png");

    public SlimeVitaeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return TEXTURE;
    }
}
