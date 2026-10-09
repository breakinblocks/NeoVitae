package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusFelling extends RitualFelling {

    public static final String NAME = "deus_felling";

    public RitualDeusFelling() {
        super(NAME, 2, 8000);
        addBlockRange(FELL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-20, 0, -20), 41, 40, 41));
        setMaximumVolumeAndDistanceOfRange(FELL_RANGE, 70000, 40, 40);
    }

    @Override
    protected int getMaxBlocksPerOperation() {
        return super.getMaxBlocksPerOperation() * 4;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusFelling();
    }
}
