package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import org.jetbrains.annotations.Nullable;

public class TestEnergyBlock extends Block implements EntityBlock {

    public TestEnergyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestEnergyBlockEntity(pos, state);
    }

    public static class TestEnergyBlockEntity extends BlockEntity {
        public final SimpleEnergyHandler storage = new SimpleEnergyHandler(100_000, 100_000, 100_000);

        public TestEnergyBlockEntity(BlockPos pos, BlockState state) {
            super(NVGameTestSetup.TEST_ENERGY_BE_TYPE.get(), pos, state);
        }

        public static @Nullable EnergyHandler getEnergyHandler(
                TestEnergyBlockEntity tile, @Nullable Direction direction) {
            return tile.storage;
        }
    }
}
