package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class DaemoniumAnimarisEyesLayer extends EyesLayer<VexRenderState, VexModel> {

    private static final RenderType EYES = RenderTypes.entityTranslucentEmissive(NeoVitae.rl("textures/entity/daemonium_animaris_e.png"));

    public DaemoniumAnimarisEyesLayer(RenderLayerParent<VexRenderState, VexModel> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
