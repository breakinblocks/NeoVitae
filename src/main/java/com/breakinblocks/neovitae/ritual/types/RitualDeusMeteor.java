package com.breakinblocks.neovitae.ritual.types;

import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusMeteor extends RitualMeteor {

    public static final String NAME = "deus_meteor";

    public RitualDeusMeteor() {
        super(NAME, 2, 1000000);
    }

    @Override
    protected boolean spawnsOresOnly() {
        return true;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusMeteor();
    }
}
