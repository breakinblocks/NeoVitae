package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Simple energy storage block for game tests.
 * Registered only in the test source set.
 */
public class TestEnergyBlock extends Block implements EntityBlock {

    public TestEnergyBlock() {
        super(BlockBehaviour.Properties.of().strength(1.0F));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestEnergyBlockEntity(pos, state);
    }

    public static class TestEnergyBlockEntity extends BlockEntity {
        public final EnergyStorage storage = new EnergyStorage(100000);

        public TestEnergyBlockEntity(BlockPos pos, BlockState state) {
            super(NVGameTestSetup.TEST_ENERGY_BE_TYPE.get(), pos, state);
        }

        public static @Nullable net.neoforged.neoforge.energy.IEnergyStorage getEnergyStorage(
                TestEnergyBlockEntity tile, @Nullable Direction direction) {
            return tile.storage;
        }
    }
}
