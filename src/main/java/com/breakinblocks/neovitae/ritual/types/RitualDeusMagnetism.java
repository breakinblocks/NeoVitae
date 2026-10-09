package com.breakinblocks.neovitae.ritual.types;

import com.breakinblocks.neovitae.ritual.EnumMiningMode;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusMagnetism extends RitualMagnetism {

    public static final String NAME = "deus_magnetism";

    public RitualDeusMagnetism() {
        super(NAME, 2, 20000);
    }

    @Override
    protected int getMaxOresPerRefresh() {
        return super.getMaxOresPerRefresh() * 4;
    }

    @Override
    protected int getScanMultiplier() {
        return 4;
    }

    @Override
    protected boolean usesFortune(IMasterRitualStone master) {
        return master.getMiningMode() == EnumMiningMode.FORTUNE;
    }

    @Override
    public boolean usesMiningMode() {
        return true;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusMagnetism();
    }
}
