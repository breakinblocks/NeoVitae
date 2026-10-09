package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusWellOfSuffering extends RitualWellOfSuffering {

    public static final String NAME = "deus_well_of_suffering";

    public RitualDeusWellOfSuffering() {
        super(NAME, 2, 200000);
        addBlockRange(DAMAGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -10, -10), 21, 21, 21));
        setMaximumVolumeAndDistanceOfRange(DAMAGE_RANGE, 0, 32, 32);
    }

    @Override
    protected int getEvMultiplier() {
        return 2;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusWellOfSuffering();
    }
}
