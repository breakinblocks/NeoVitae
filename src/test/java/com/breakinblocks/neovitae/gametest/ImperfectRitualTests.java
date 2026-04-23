package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.ImperfectRitualStoneBlockEntity;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

public final class ImperfectRitualTests {

    private ImperfectRitualTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("imperfect_ritual/stone_places_and_initializes", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.IMPERFECT_RITUAL_STONE.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                ImperfectRitualStoneBlockEntity stone = helper.getBlockEntity(pos, ImperfectRitualStoneBlockEntity.class);
                if (stone == null) {
                    helper.fail("Expected ImperfectRitualStoneBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("imperfect_ritual/coal_block_matches_night", 60, helper -> {
            helper.runAfterDelay(5, () -> {
                if (RitualRegistry.findRitualForBlock(Blocks.COAL_BLOCK.defaultBlockState()) == null) {
                    helper.fail("Coal block should match an imperfect ritual (night)");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("imperfect_ritual/dirt_does_not_match", 60, helper -> {
            helper.runAfterDelay(5, () -> {
                if (RitualRegistry.findRitualForBlock(Blocks.DIRT.defaultBlockState()) != null) {
                    helper.fail("Dirt should not match any imperfect ritual");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
