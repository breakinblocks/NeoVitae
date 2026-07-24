package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.ritual.harvest.HarvestHandlerTall;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class HarvestTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 40)
    public void bambooHarvestBreaksTopNotCascade(GameTestHelper helper) {
        BlockPos dirtPos = new BlockPos(2, 0, 2);
        BlockPos base = dirtPos.above();
        BlockPos mid = base.above();
        BlockPos top = mid.above();

        helper.setBlock(dirtPos, Blocks.DIRT);
        helper.setBlock(base, Blocks.BAMBOO);
        helper.setBlock(mid, Blocks.BAMBOO);
        helper.setBlock(top, Blocks.BAMBOO);

        helper.runAfterDelay(2, () -> {
            ServerLevel level = helper.getLevel();
            BlockPos absBase = helper.absolutePos(base);
            BlockState baseState = level.getBlockState(absBase);

            List<ItemStack> drops = new ArrayList<>();
            if (!new HarvestHandlerTall().harvest(level, absBase, baseState, drops, null)) {
                helper.fail("Tall handler did not harvest the bamboo column");
                return;
            }
            if (drops.stream().noneMatch(s -> s.is(Items.BAMBOO))) {
                helper.fail("Harvest produced no bamboo drop: " + drops);
                return;
            }

            helper.assertBlockPresent(Blocks.AIR, top);
            helper.assertBlockPresent(Blocks.BAMBOO, mid);
            helper.assertBlockPresent(Blocks.BAMBOO, base);
            helper.succeed();
        });
    }
}
