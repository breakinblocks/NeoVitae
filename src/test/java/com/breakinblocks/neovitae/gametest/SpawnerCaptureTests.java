package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;

public final class SpawnerCaptureTests {

    private SpawnerCaptureTests() {}

    private static ItemStack captureZombieSpawner(GameTestHelper helper, BlockPos relative) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.SPAWNER);
        if (!(level.getBlockEntity(abs) instanceof SpawnerBlockEntity source)) {
            return ItemStack.EMPTY;
        }
        source.setEntityId(EntityType.ZOMBIE, level.getRandom());

        ItemStack stack = new ItemStack(Blocks.SPAWNER);
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        source.saveCustomOnly(output);
        BlockItem.setBlockEntityData(stack, source.getType(), output);
        return stack;
    }

    private static String spawnedTypeAt(GameTestHelper helper, BlockPos relative) {
        ServerLevel level = helper.getLevel();
        BlockEntity placed = level.getBlockEntity(helper.absolutePos(relative));
        if (!(placed instanceof SpawnerBlockEntity)) {
            return "";
        }
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        placed.saveCustomOnly(output);
        return output.buildResult()
                .getCompoundOrEmpty("SpawnData")
                .getCompoundOrEmpty("entity")
                .getStringOr("id", "");
    }

    public static void register(NVTestRegistrar r) {
        r.add("spawner/captured_spawner_keeps_its_mob", 60, helper -> {
            BlockPos source = new BlockPos(1, 1, 1);
            BlockPos target = new BlockPos(3, 1, 1);

            ItemStack stack = captureZombieSpawner(helper, source);
            if (stack.isEmpty()) {
                helper.fail("Could not read the source spawner");
                return;
            }
            stack.set(NVDataComponents.CAPTURED_SPAWNER.get(), true);

            helper.setBlock(target, Blocks.SPAWNER);
            BlockItem.updateCustomBlockEntityTag(helper.getLevel(), helper.makeMockPlayer(GameType.SURVIVAL),
                    helper.absolutePos(target), stack);

            String id = spawnedTypeAt(helper, target);
            if (!"minecraft:zombie".equals(id)) {
                helper.fail("Captured spawner lost its mob on placement, got '" + id + "'");
                return;
            }
            helper.succeed();
        });

        r.add("spawner/unmarked_spawner_item_stays_op_only", 60, helper -> {
            BlockPos source = new BlockPos(1, 1, 1);
            BlockPos target = new BlockPos(3, 1, 1);

            ItemStack stack = captureZombieSpawner(helper, source);
            if (stack.isEmpty()) {
                helper.fail("Could not read the source spawner");
                return;
            }

            helper.setBlock(target, Blocks.SPAWNER);
            BlockItem.updateCustomBlockEntityTag(helper.getLevel(), helper.makeMockPlayer(GameType.SURVIVAL),
                    helper.absolutePos(target), stack);

            if ("minecraft:zombie".equals(spawnedTypeAt(helper, target))) {
                helper.fail("A spawner item without the capture marker set its own data");
                return;
            }
            helper.succeed();
        });
    }
}
