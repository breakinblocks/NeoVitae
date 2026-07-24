package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.harvest.HarvestHandlerCactusFlower;
import com.breakinblocks.neovitae.ritual.harvest.HarvestHandlerTall;

import java.util.ArrayList;
import java.util.List;

public final class HarvestTests {

    private HarvestTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("cactus_flower_harvested_leaves_cactus", 40, helper -> {
            BlockPos sandPos = new BlockPos(2, 0, 2);
            BlockPos cactusPos = sandPos.above();
            BlockPos flowerPos = cactusPos.above();

            helper.setBlock(sandPos, Blocks.SAND);
            helper.setBlock(cactusPos, Blocks.CACTUS);
            helper.setBlock(flowerPos, Blocks.CACTUS_FLOWER);

            helper.runAfterDelay(2, () -> {
                ServerLevel level = helper.getLevel();
                BlockPos absFlower = helper.absolutePos(flowerPos);
                BlockState flowerState = level.getBlockState(absFlower);

                HarvestHandlerCactusFlower handler = new HarvestHandlerCactusFlower();
                if (!handler.test(level, absFlower, flowerState)) {
                    helper.fail("Handler did not recognize the cactus flower");
                    return;
                }

                List<ItemStack> drops = new ArrayList<>();
                if (!handler.harvest(level, absFlower, flowerState, drops, null)) {
                    helper.fail("Handler did not harvest the cactus flower");
                    return;
                }
                if (drops.stream().noneMatch(s -> s.is(Items.CACTUS_FLOWER))) {
                    helper.fail("Harvest produced no cactus flower drop: " + drops);
                    return;
                }

                helper.assertBlockPresent(Blocks.AIR, flowerPos);
                helper.assertBlockPresent(Blocks.CACTUS, cactusPos);
                helper.succeed();
            });
        });

        r.add("bamboo_harvest_breaks_top_not_cascade", 40, helper -> {
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
        });
    }
}
