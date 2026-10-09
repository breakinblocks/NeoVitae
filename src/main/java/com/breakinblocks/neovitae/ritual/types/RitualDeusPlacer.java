package com.breakinblocks.neovitae.ritual.types;

import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusPlacer extends RitualPlacer {

    public static final String NAME = "deus_placer";

    public RitualDeusPlacer() {
        super(NAME, 2, 20000);
    }

    @Override
    protected int getTierVolume(int tier) {
        return super.getTierVolume(tier) * 4;
    }

    @Override
    protected int getTierRadius(int tier) {
        return super.getTierRadius(tier) * 2;
    }

    @Override
    protected int getBlockMultiplier() {
        return 8;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusPlacer();
    }
}
