package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.fluid.NVFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class EssentiaVitaeFluidTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void essentiaVitaeHydratesFarmland(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos farmland = new BlockPos(2, 1, 2);
        BlockPos fluid = new BlockPos(3, 1, 2);

        helper.setBlock(farmland, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0));
        helper.setBlock(fluid, NVFluids.ESSENTIA_VITAE_BLOCK.get());

        helper.runAfterDelay(2, () -> {
            BlockPos abs = helper.absolutePos(farmland);
            BlockState state = level.getBlockState(abs);
            if (!state.is(Blocks.FARMLAND)) {
                helper.fail("Farmland turned to " + state + " next to essentia vitae");
                return;
            }
            state.randomTick(level, abs, level.getRandom());
            int moisture = level.getBlockState(abs).getValue(FarmBlock.MOISTURE);
            if (moisture != 7) {
                helper.fail("Farmland next to essentia vitae has moisture " + moisture + " instead of 7");
                return;
            }
            helper.succeed();
        });
    }
}
