package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumFervidisEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaemoniumFervidisModel extends DefaultedEntityGeoModel<DaemoniumFervidisEntity> {

    public DaemoniumFervidisModel() {
        super(NeoVitae.rl("daemonium_fervidis"));
    }
}
