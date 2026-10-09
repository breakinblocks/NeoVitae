package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;

import java.util.function.Consumer;

public class RitualDeusCrystallumFractura extends RitualCrystallumFractura {

    public static final String NAME = "deus_crystallum_fractura";

    public RitualDeusCrystallumFractura() {
        super(NAME, 2, 400000);
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -5, -15), 31, 11, 31));
        addBlockRange(AURA_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -5, -15), 31, 11, 31));
        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 16000, 32, 32);
        setMaximumVolumeAndDistanceOfRange(AURA_RANGE, 16000, 32, 32);
    }

    @Override
    protected double getGrowthMultiplier() {
        return super.getGrowthMultiplier() * 2;
    }

    @Override
    protected boolean fortuneConsumesSpiritus() {
        return false;
    }

    @Override
    protected int computeFortuneLevel(ServerLevel level, BlockPos pos, SpiritusType type) {
        return 3;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusCrystallumFractura();
    }
}
