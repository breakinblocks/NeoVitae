package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.types.RitualMagnetism;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class QuarryBackfillTests {

    private QuarryBackfillTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("quarry/backfill_matches_surrounding_stone", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos hole = new BlockPos(2, 1, 2);

            helper.setBlock(hole.north(), Blocks.DEEPSLATE);
            helper.setBlock(hole.south(), Blocks.DEEPSLATE);

            BlockState filler = RitualMagnetism.fillerFor(level, helper.absolutePos(hole));
            if (!filler.is(Blocks.DEEPSLATE)) {
                helper.fail("Backfill picked " + filler + " instead of the surrounding deepslate");
                return;
            }
            helper.succeed();
        });

        r.add("quarry/backfill_never_copies_player_blocks", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos hole = new BlockPos(2, 1, 2);

            // Valuables and built blocks must never be duplicated into the hole.
            helper.setBlock(hole.north(), Blocks.DIAMOND_BLOCK);
            helper.setBlock(hole.south(), Blocks.CHEST);
            helper.setBlock(hole.east(), Blocks.OAK_PLANKS);

            BlockState filler = RitualMagnetism.fillerFor(level, helper.absolutePos(hole));
            if (!filler.is(Blocks.STONE)) {
                helper.fail("Backfill copied a non-natural neighbour: " + filler);
                return;
            }
            helper.succeed();
        });

        r.add("quarry/backfill_falls_back_to_stone_in_open_air", 60, helper -> {
            BlockState filler = RitualMagnetism.fillerFor(helper.getLevel(),
                    helper.absolutePos(new BlockPos(2, 3, 2)));
            if (!filler.is(Blocks.STONE)) {
                helper.fail("Expected a plain stone fallback, got " + filler);
                return;
            }
            helper.succeed();
        });
    }
}
