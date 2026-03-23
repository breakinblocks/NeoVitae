package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.ImperfectRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class ImperfectRitualTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void imperfectStonePlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.IMPERFECT_RITUAL_STONE.block().get().defaultBlockState());

        helper.runAfterDelay(5, () -> {
            BlockEntity be = helper.getBlockEntity(new BlockPos(3, 1, 2));
            if (!(be instanceof ImperfectRitualStoneBlockEntity)) {
                helper.fail("Expected ImperfectRitualStoneBlockEntity");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void coalBlockMatchesNightRitual(GameTestHelper helper) {
        helper.runAfterDelay(5, () -> {
            var result = RitualRegistry.findRitualForBlock(Blocks.COAL_BLOCK.defaultBlockState());
            if (result == null) {
                helper.fail("Coal block should match an imperfect ritual (night)");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void dirtDoesNotMatchAnyRitual(GameTestHelper helper) {
        helper.runAfterDelay(5, () -> {
            var result = RitualRegistry.findRitualForBlock(Blocks.DIRT.defaultBlockState());
            if (result != null) {
                helper.fail("Dirt should not match any imperfect ritual");
                return;
            }
            helper.succeed();
        });
    }
}
