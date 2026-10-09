package com.breakinblocks.neovitae.ritual.types;

import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusTormentNexus extends RitualTormentNexus {

    public static final String NAME = "deus_torment_nexus";

    public RitualDeusTormentNexus() {
        super(NAME, 2, 100000);
        setMaximumVolumeAndDistanceOfRange(EFFECT_RANGE, 0, 64, 64);
    }

    @Override
    protected int getEvPerKill() {
        return (int) Math.round(super.getEvPerKill() * 35 / 75.0);
    }

    @Override
    protected int getMaxLootRolls() {
        return super.getMaxLootRolls() * 3;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusTormentNexus();
    }
}
