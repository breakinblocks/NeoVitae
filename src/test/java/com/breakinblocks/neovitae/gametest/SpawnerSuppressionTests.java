package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.world.SpawnerSuppressionData;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.SpawnerSuppression;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;

public final class SpawnerSuppressionTests {

    private SpawnerSuppressionTests() {}

    /** Far enough away that the chunk is never loaded, standing in for an unloaded ritual stone. */
    private static final BlockPos UNLOADED_MASTER = new BlockPos(24_000_000, 64, 24_000_000);

    public static void register(NVTestRegistrar r) {
        r.add("spawner/suppressed_trial_spawner_does_not_tick", 100, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos pos = new BlockPos(2, 1, 2);
            BlockPos abs = helper.absolutePos(pos);

            helper.setBlock(pos, Blocks.TRIAL_SPAWNER);
            if (!(level.getBlockEntity(abs) instanceof TrialSpawnerBlockEntity spawner)) {
                helper.fail("Trial spawner block entity was not created");
                return;
            }
            spawner.setEntityId(EntityType.ZOMBIE, level.getRandom());
            SpawnerSuppression.add(level, abs, UNLOADED_MASTER);

            helper.runAfterDelay(10, () -> {
                TrialSpawnerState suppressed = level.getBlockState(abs).getValue(TrialSpawnerBlock.STATE);
                SpawnerSuppression.remove(level, abs);
                if (suppressed != TrialSpawnerState.INACTIVE) {
                    helper.fail("Suppressed trial spawner advanced to " + suppressed);
                    return;
                }
                helper.runAfterDelay(10, () -> {
                    TrialSpawnerState released = level.getBlockState(abs).getValue(TrialSpawnerBlock.STATE);
                    if (released == TrialSpawnerState.INACTIVE) {
                        helper.fail("Released trial spawner never resumed its state machine");
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("spawner/suppression_survives_an_unloaded_ritual_stone", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos abs = helper.absolutePos(new BlockPos(2, 1, 2));

            SpawnerSuppression.add(level, abs, UNLOADED_MASTER);
            boolean suppressed = SpawnerSuppression.isSuppressed(level, abs);
            SpawnerSuppression.remove(level, abs);

            if (!suppressed) {
                helper.fail("Suppression was dropped while the ritual stone's chunk was unloaded");
                return;
            }
            helper.succeed();
        });

        r.add("spawner/suppression_dropped_when_ritual_stone_is_gone", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos spawner = helper.absolutePos(new BlockPos(2, 1, 2));
            BlockPos master = helper.absolutePos(new BlockPos(2, 1, 4));

            helper.setBlock(new BlockPos(2, 1, 4), Blocks.STONE);
            SpawnerSuppression.add(level, spawner, master);

            if (SpawnerSuppression.isSuppressed(level, spawner)) {
                SpawnerSuppression.remove(level, spawner);
                helper.fail("Suppression held on to a position whose ritual stone no longer exists");
                return;
            }
            helper.succeed();
        });

        r.add("spawner/suppression_round_trips_through_save_data", 60, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos spawner = helper.absolutePos(new BlockPos(2, 1, 2));

            SpawnerSuppression.add(level, spawner, UNLOADED_MASTER);
            SpawnerSuppressionData data = level.getDataStorage().computeIfAbsent(SpawnerSuppressionData.TYPE);
            BlockPos stored = data.entries().get(spawner);
            SpawnerSuppression.remove(level, spawner);

            if (stored == null) {
                helper.fail("Suppressed spawner was never written to the level's save data");
                return;
            }
            if (!stored.equals(UNLOADED_MASTER)) {
                helper.fail("Saved suppression points at " + stored + " instead of " + UNLOADED_MASTER);
                return;
            }
            helper.succeed();
        });
    }
}
