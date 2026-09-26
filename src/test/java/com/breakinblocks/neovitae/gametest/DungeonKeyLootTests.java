package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class DungeonKeyLootTests {
    private static final int ROLLS = 1000;
    private static final long SEED = 7331L;
    private static final List<String> ROOM_TABLES = List.of(
            "standard_dungeon/decent_alchemy",
            "standard_dungeon/decent_loot",
            "standard_dungeon/decent_smithy",
            "standard_dungeon/enchanting_loot",
            "standard_dungeon/great_loot",
            "standard_dungeon/poor_loot",
            "standard_dungeon/strong_alchemy");

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void entranceCacheRollsTwoOrThreeStandardKeys(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            LootTable table = table(helper, "standard_dungeon/entrance_chest");
            if (table == LootTable.EMPTY) {
                helper.fail("standard_dungeon/entrance_chest did not load");
                return;
            }
            LootParams params = chestParams(helper.getLevel());
            RandomSource random = RandomSource.create(SEED);
            for (int i = 0; i < ROLLS; i++) {
                int keys = standardKeys(table.getRandomItems(params, random));
                if (keys < 2 || keys > 3) {
                    helper.fail("Entrance cache rolled " + keys + " Standard Keys on roll " + i);
                    return;
                }
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void trialSpawnersOutdropChestsForStandardKeys(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            ServerLevel level = helper.getLevel();
            LootParams chest = chestParams(level);
            LootParams spawner = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
            for (String path : ROOM_TABLES) {
                LootTable table = table(helper, path);
                if (table == LootTable.EMPTY) {
                    helper.fail(path + " did not load");
                    return;
                }
                double chestRate = keyRate(table, chest);
                double spawnerRate = keyRate(table, spawner);
                if (chestRate < 0.15 || chestRate > 0.40) {
                    helper.fail(path + " chest Standard Key rate " + chestRate + " is outside 0.15-0.40");
                    return;
                }
                if (spawnerRate < 0.45) {
                    helper.fail(path + " trial spawner Standard Key rate " + spawnerRate + " is below 0.45");
                    return;
                }
            }
            double foremanRate = keyRate(table(helper, "foreman/treasure"), spawner);
            if (foremanRate < 0.45) {
                helper.fail("Foreman treasure Standard Key rate " + foremanRate + " is below 0.45");
                return;
            }
            helper.succeed();
        });
    }

    private static LootTable table(GameTestHelper helper, String path) {
        return helper.getLevel().getServer().reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, NeoVitae.rl("chests/" + path)));
    }

    private static LootParams chestParams(ServerLevel level) {
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
                .create(LootContextParamSets.CHEST);
    }

    private static double keyRate(LootTable table, LootParams params) {
        RandomSource random = RandomSource.create(SEED);
        int hits = 0;
        for (int i = 0; i < ROLLS; i++) {
            if (standardKeys(table.getRandomItems(params, random)) > 0) {
                hits++;
            }
        }
        return hits / (double) ROLLS;
    }

    private static int standardKeys(List<ItemStack> drops) {
        int keys = 0;
        for (ItemStack drop : drops) {
            if (drop.is(NVItems.STANDARD_KEY.get())) {
                keys += drop.getCount();
            }
        }
        return keys;
    }
}
