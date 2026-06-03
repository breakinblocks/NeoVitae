package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.resources.Identifier;

public class DaemoniumAnimarisRenderer extends VexRenderer {
    private static final Identifier TEXTURE = NeoVitae.rl("textures/entity/daemonium_animaris.png");
    private static final Identifier TEXTURE_CHARGING = NeoVitae.rl("textures/entity/daemonium_animaris_charging.png");

    public DaemoniumAnimarisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        addLayer(new DaemoniumAnimarisEyesLayer(this));
    }

    @Override
    public Identifier getTextureLocation(VexRenderState state) {
        return state.isCharging ? TEXTURE_CHARGING : TEXTURE;
    }
}
