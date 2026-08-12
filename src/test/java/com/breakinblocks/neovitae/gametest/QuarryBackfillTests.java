package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.ritual.types.RitualMagnetism;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class QuarryBackfillTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void backfillMatchesSurroundingStone(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos hole = new BlockPos(2, 1, 2);
        BlockPos abs = helper.absolutePos(hole);

        helper.setBlock(hole.north(), Blocks.DEEPSLATE);
        helper.setBlock(hole.south(), Blocks.DEEPSLATE);

        BlockState filler = RitualMagnetism.fillerFor(level, abs);
        if (!filler.is(Blocks.DEEPSLATE)) {
            helper.fail("Backfill picked " + filler + " instead of the surrounding deepslate");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void backfillNeverCopiesPlayerBlocks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos hole = new BlockPos(2, 1, 2);
        BlockPos abs = helper.absolutePos(hole);

        // Valuables and built blocks must never be duplicated into the hole.
        helper.setBlock(hole.north(), Blocks.DIAMOND_BLOCK);
        helper.setBlock(hole.south(), Blocks.CHEST);
        helper.setBlock(hole.east(), Blocks.OAK_PLANKS);

        BlockState filler = RitualMagnetism.fillerFor(level, abs);
        if (!filler.is(Blocks.STONE)) {
            helper.fail("Backfill copied a non-natural neighbour: " + filler);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void backfillFallsBackToStoneInOpenAir(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockState filler = RitualMagnetism.fillerFor(level, helper.absolutePos(new BlockPos(2, 3, 2)));
        if (!filler.is(Blocks.STONE)) {
            helper.fail("Expected a plain stone fallback, got " + filler);
            return;
        }
        helper.succeed();
    }
}
