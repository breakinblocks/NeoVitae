package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SpawnerCaptureTests {

    private static ItemStack captureZombieSpawner(GameTestHelper helper, BlockPos relative) {
        helper.setBlock(relative, Blocks.SPAWNER);
        if (!(helper.getBlockEntity(relative) instanceof SpawnerBlockEntity source)) {
            return ItemStack.EMPTY;
        }
        source.setEntityId(EntityType.ZOMBIE, helper.getLevel().getRandom());

        ItemStack stack = new ItemStack(Blocks.SPAWNER);
        source.saveToItem(stack, helper.getLevel().registryAccess());
        return stack;
    }

    private static String spawnedTypeAt(GameTestHelper helper, BlockPos relative) {
        if (!(helper.getBlockEntity(relative) instanceof SpawnerBlockEntity placed)) {
            return "";
        }
        CompoundTag saved = placed.saveCustomOnly(helper.getLevel().registryAccess());
        return saved.getCompound("SpawnData").getCompound("entity").getString("id");
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void capturedSpawnerKeepsItsMob(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos target = new BlockPos(3, 1, 1);

        ItemStack stack = captureZombieSpawner(helper, source);
        if (stack.isEmpty()) {
            helper.fail("could not read the source spawner");
            return;
        }
        stack.set(NVDataComponents.CAPTURED_SPAWNER.get(), true);

        helper.setBlock(target, Blocks.SPAWNER);
        BlockItem.updateCustomBlockEntityTag(helper.getLevel(), helper.makeMockPlayer(GameType.SURVIVAL),
                helper.absolutePos(target), stack);

        String id = spawnedTypeAt(helper, target);
        if (!"minecraft:zombie".equals(id)) {
            helper.fail("captured spawner lost its mob on placement, got '" + id + "'");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void unmarkedSpawnerItemStaysOpOnly(GameTestHelper helper) {
        BlockPos source = new BlockPos(1, 1, 1);
        BlockPos target = new BlockPos(3, 1, 1);

        ItemStack stack = captureZombieSpawner(helper, source);
        if (stack.isEmpty()) {
            helper.fail("could not read the source spawner");
            return;
        }

        helper.setBlock(target, Blocks.SPAWNER);
        BlockItem.updateCustomBlockEntityTag(helper.getLevel(), helper.makeMockPlayer(GameType.SURVIVAL),
                helper.absolutePos(target), stack);

        if ("minecraft:zombie".equals(spawnedTypeAt(helper, target))) {
            helper.fail("a spawner item without the capture marker set its own data");
            return;
        }
        helper.succeed();
    }
}
