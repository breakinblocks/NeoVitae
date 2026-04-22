package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumCorrodisEntity;
import com.geckolib.model.DefaultedEntityGeoModel;

public class DaemoniumCorrodisModel extends DefaultedEntityGeoModel<DaemoniumCorrodisEntity> {

    public DaemoniumCorrodisModel() {
        super(NeoVitae.rl("daemonium_corrodis"));
    }
}
