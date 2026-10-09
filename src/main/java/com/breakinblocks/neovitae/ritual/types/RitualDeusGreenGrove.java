package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusGreenGrove extends RitualGreenGrove {

    public static final String NAME = "deus_green_grove";

    public RitualDeusGreenGrove() {
        super(NAME, 2, 4000);
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-7, 1, -7), 15, 9, 15));
        addBlockRange(LEECH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-9, 0, -9), 19, 3, 19));
        addBlockRange(HYDRATE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-6, 0, -6), 13, 1, 13));
        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 3000, 16, 10);
        setMaximumVolumeAndDistanceOfRange(LEECH_RANGE, 4500, 24, 10);
        setMaximumVolumeAndDistanceOfRange(HYDRATE_RANGE, 1500, 16, 5);
    }

    @Override
    protected int getRefreshDivisor() {
        return 2;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusGreenGrove();
    }
}
