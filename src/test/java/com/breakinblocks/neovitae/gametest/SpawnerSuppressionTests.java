package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.world.SpawnerSuppressionData;
import com.breakinblocks.neovitae.ritual.SpawnerSuppression;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SpawnerSuppressionTests {

    private static final BlockPos UNLOADED_MASTER = new BlockPos(24_000_000, 64, 24_000_000);

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void suppressedTrialSpawnerDoesNotTick(GameTestHelper helper) {
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
            TrialSpawnerState suppressedState = level.getBlockState(abs).getValue(TrialSpawnerBlock.STATE);
            SpawnerSuppression.remove(level, abs);
            if (suppressedState != TrialSpawnerState.INACTIVE) {
                helper.fail("Suppressed trial spawner advanced to " + suppressedState);
                return;
            }
            helper.runAfterDelay(10, () -> {
                TrialSpawnerState releasedState = level.getBlockState(abs).getValue(TrialSpawnerBlock.STATE);
                if (releasedState == TrialSpawnerState.INACTIVE) {
                    helper.fail("Released trial spawner never resumed its state machine");
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void suppressedVanillaSpawnerKeepsTicking(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = new BlockPos(2, 1, 2);
        BlockPos abs = helper.absolutePos(pos);

        helper.setBlock(pos, Blocks.SPAWNER);
        SpawnerSuppression.add(level, abs, UNLOADED_MASTER);

        helper.runAfterDelay(10, () -> {
            SpawnerSuppression.remove(level, abs);
            helper.assertBlockPresent(Blocks.SPAWNER, pos);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void suppressionSurvivesAnUnloadedRitualStone(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void suppressionRoundTripsThroughSaveData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawner = helper.absolutePos(new BlockPos(2, 1, 2));

        SpawnerSuppression.add(level, spawner, UNLOADED_MASTER);
        SpawnerSuppressionData data = level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(SpawnerSuppressionData::new, SpawnerSuppressionData::load),
                SpawnerSuppressionData.ID);
        CompoundTag saved = data.save(new CompoundTag(), level.registryAccess());
        SpawnerSuppression.remove(level, spawner);

        SpawnerSuppressionData reloaded = SpawnerSuppressionData.load(saved, level.registryAccess());
        BlockPos master = reloaded.entries().get(spawner);
        if (master == null) {
            helper.fail("Suppressed spawner did not survive a save and reload");
            return;
        }
        if (!master.equals(UNLOADED_MASTER)) {
            helper.fail("Reloaded suppression points at " + master + " instead of " + UNLOADED_MASTER);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void suppressionIsDroppedWhenTheRitualStoneIsGone(GameTestHelper helper) {
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
        if (SpawnerSuppression.isSuppressed(level, spawner)) {
            helper.fail("Stale suppression entry was not cleaned up");
            return;
        }
        helper.succeed();
    }
}
