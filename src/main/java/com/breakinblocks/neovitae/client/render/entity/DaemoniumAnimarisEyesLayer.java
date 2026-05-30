package com.breakinblocks.neovitae.client.render.entity;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.monster.Vex;

public class DaemoniumAnimarisEyesLayer extends EyesLayer<Vex, VexModel> {

    private static final RenderType EYES = RenderType.eyes(NeoVitae.rl("textures/entity/daemonium_animaris_e.png"));

    public DaemoniumAnimarisEyesLayer(RenderLayerParent<Vex, VexModel> parent) {
        super(parent);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
