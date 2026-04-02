package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumPestisEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaemoniumPestisModel extends DefaultedEntityGeoModel<DaemoniumPestisEntity> {

    public DaemoniumPestisModel() {
        super(NeoVitae.rl("daemonium_pestis"));
    }
}
