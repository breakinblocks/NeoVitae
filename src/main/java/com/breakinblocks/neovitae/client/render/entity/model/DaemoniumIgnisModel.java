package com.breakinblocks.neovitae.client.render.entity.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumIgnisEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaemoniumIgnisModel extends DefaultedEntityGeoModel<DaemoniumIgnisEntity> {

    public DaemoniumIgnisModel() {
        super(NeoVitae.rl("daemonium_ignis"));
    }
}
