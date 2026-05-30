package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

public class DaemoniumAnimarisRenderer extends VexRenderer {
    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/entity/daemonium_animaris.png");
    private static final ResourceLocation TEXTURE_CHARGING = NeoVitae.rl("textures/entity/daemonium_animaris_charging.png");

    public DaemoniumAnimarisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        addLayer(new DaemoniumAnimarisEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Vex entity) {
        return entity.isCharging() ? TEXTURE_CHARGING : TEXTURE;
    }
}
