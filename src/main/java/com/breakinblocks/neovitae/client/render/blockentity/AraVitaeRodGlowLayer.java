package com.breakinblocks.neovitae.client.render.blockentity;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;

public class AraVitaeRodGlowLayer extends GeoRenderLayer<AraVitaeTile, Void, AraVitaeRenderer.State> {

    private static final Identifier GLOW_TEXTURE = NeoVitae.rl("textures/block/altar_glow.png");

    public AraVitaeRodGlowLayer(GeoRenderer<AraVitaeTile, Void, AraVitaeRenderer.State> renderer) {
        super(renderer);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<AraVitaeRenderer.State> info, SubmitNodeCollector collector) {
        if (!info.willRender()) return;
        if (!info.renderState().active) return;
        RenderType glowType = RenderTypes.entityTranslucentEmissive(GLOW_TEXTURE);
        getRenderer().submitRenderTasks(info, collector.order(1), glowType);
    }
}
