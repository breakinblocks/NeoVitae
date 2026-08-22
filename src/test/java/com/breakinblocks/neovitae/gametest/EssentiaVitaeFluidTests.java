package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class EssentiaVitaeFluidTests {

    private EssentiaVitaeFluidTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("essentia_vitae/hydrates_farmland", 120, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos farmland = new BlockPos(2, 1, 2);
            BlockPos fluid = new BlockPos(3, 1, 2);

            helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 0));
            helper.setBlock(fluid, NVFluids.ESSENTIA_VITAE_BLOCK.get());

            helper.runAfterDelay(2, () -> {
                BlockPos abs = helper.absolutePos(farmland);
                BlockState state = level.getBlockState(abs);
                if (!state.is(Blocks.FARMLAND)) {
                    helper.fail("Farmland turned to " + state + " next to essentia vitae");
                    return;
                }
                state.randomTick(level, abs, level.getRandom());
                int moisture = level.getBlockState(abs).getValue(FarmlandBlock.MOISTURE);
                if (moisture != 7) {
                    helper.fail("Farmland next to essentia vitae has moisture " + moisture + " instead of 7");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
