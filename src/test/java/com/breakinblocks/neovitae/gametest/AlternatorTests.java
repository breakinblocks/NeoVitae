package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.dungeon.BlockAlternator;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AlternatorTests {

    private static DungeonAlternatorBlockEntity placeAlternator(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, DungeonBlocks.ALTERNATOR.block().get().defaultBlockState());
        if (!(helper.getBlockEntity(pos) instanceof DungeonAlternatorBlockEntity alternator)) {
            helper.fail("Expected alternator block entity at " + pos);
            return null;
        }
        return alternator;
    }

    private static boolean lampLit(GameTestHelper helper, BlockPos pos) {
        return helper.getBlockState(pos).getValue(RedstoneLampBlock.LIT);
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void defaultAlternatorIsInert(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 2);
        BlockPos lampPos = new BlockPos(3, 1, 2);
        placeAlternator(helper, alternatorPos);
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());

        helper.runAfterDelay(40, () -> {
            helper.assertBlockProperty(alternatorPos, BlockAlternator.RUNNING, false);
            helper.assertBlockProperty(alternatorPos, BlockAlternator.ACTIVE, false);
            if (lampLit(helper, lampPos)) {
                helper.fail("Unconfigured alternator powered its neighbor");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void delayOneIsConstantSignal(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 2);
        BlockPos lampPos = new BlockPos(3, 1, 2);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.setDelay(1);

        helper.succeedWhen(() -> {
            helper.assertBlockProperty(alternatorPos, BlockAlternator.RUNNING, true);
            helper.assertBlockProperty(alternatorPos, BlockAlternator.ACTIVE, true);
            if (!lampLit(helper, lampPos)) {
                helper.fail("Lamp beside a delay-1 alternator should be lit");
            }
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void timerPulses(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 2);
        BlockPos lampPos = new BlockPos(3, 1, 2);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.setDelay(20);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    if (!lampLit(helper, lampPos)) helper.fail("Waiting for pulse on");
                })
                .thenWaitUntil(() -> {
                    if (lampLit(helper, lampPos)) helper.fail("Waiting for pulse off");
                })
                .thenWaitUntil(() -> {
                    if (!lampLit(helper, lampPos)) helper.fail("Waiting for second pulse");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void linkedReceiverActsPowered(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 1);
        BlockPos lampPos = new BlockPos(2, 1, 5);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.addReceiver(helper.absolutePos(lampPos));
        alternator.setDelay(1);

        helper.succeedWhen(() -> {
            if (!lampLit(helper, lampPos)) {
                helper.fail("Linked lamp should light as if directly powered");
            }
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void linkedReceiverEmitsToNeighbors(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 1);
        BlockPos receiverPos = new BlockPos(2, 1, 4);
        BlockPos lampPos = new BlockPos(2, 1, 5);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(receiverPos, Blocks.STONE.defaultBlockState());
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.addReceiver(helper.absolutePos(receiverPos));
        alternator.setDelay(1);

        helper.succeedWhen(() -> {
            if (!lampLit(helper, lampPos)) {
                helper.fail("Lamp beside a linked receiver should light");
            }
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void unlinkDropsReceiverPower(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 1);
        BlockPos lampPos = new BlockPos(2, 1, 5);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.addReceiver(helper.absolutePos(lampPos));
        alternator.setDelay(1);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    if (!lampLit(helper, lampPos)) helper.fail("Waiting for linked lamp to light");
                })
                .thenExecute(() -> alternator.removeReceiver(helper.absolutePos(lampPos)))
                .thenWaitUntil(() -> {
                    if (lampLit(helper, lampPos)) helper.fail("Unlinked lamp should turn off");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void hardSignalPausesAndResumes(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 1);
        BlockPos leverPos = new BlockPos(2, 2, 1);
        BlockPos lampPos = new BlockPos(2, 1, 5);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        helper.setBlock(leverPos, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR));
        helper.setBlock(lampPos, Blocks.REDSTONE_LAMP.defaultBlockState());
        alternator.addReceiver(helper.absolutePos(lampPos));
        alternator.setDelay(1);

        helper.startSequence()
                .thenWaitUntil(() -> {
                    if (!lampLit(helper, lampPos)) helper.fail("Waiting for linked lamp to light");
                })
                .thenExecute(() -> helper.pullLever(leverPos))
                .thenWaitUntil(() -> {
                    helper.assertBlockProperty(alternatorPos, BlockAlternator.RUNNING, false);
                    if (lampLit(helper, lampPos)) helper.fail("Lamp should go dark while paused");
                })
                .thenExecute(() -> helper.pullLever(leverPos))
                .thenWaitUntil(() -> {
                    helper.assertBlockProperty(alternatorPos, BlockAlternator.RUNNING, true);
                    if (!lampLit(helper, lampPos)) helper.fail("Lamp should relight after resume");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void receiverCapEnforced(GameTestHelper helper) {
        BlockPos alternatorPos = new BlockPos(2, 1, 1);
        DungeonAlternatorBlockEntity alternator = placeAlternator(helper, alternatorPos);
        for (int i = 0; i < 8; i++) {
            if (!alternator.addReceiver(helper.absolutePos(new BlockPos(i % 4, 1, 3 + i / 4)))) {
                helper.fail("Receiver " + i + " should have been accepted");
            }
        }
        if (alternator.addReceiver(helper.absolutePos(new BlockPos(4, 1, 5)))) {
            helper.fail("Ninth receiver should have been rejected");
        }
        helper.succeed();
    }
}
