package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SpiritusRoutingTests {

    private static final SpiritusType[] TEST_TYPES = { SpiritusType.RUINA, SpiritusType.VINDICTA };

    private static final BlockPos MASTER_POS = new BlockPos(1, 1, 1);
    private static final BlockPos ACCUMULATOR_POS = new BlockPos(3, 1, 1);
    private static final BlockPos OUTPUT_POS = new BlockPos(3, 1, 3);

    private record Network(MasterRoutingNodeBlockEntity master,
                           SpiritAccumulatorBlockEntity accumulator,
                           OutputRoutingNodeBlockEntity output) {}

    private static Network buildNetwork(GameTestHelper helper) {
        helper.setBlock(MASTER_POS, NVBlocks.MASTER_ROUTING_NODE.block().get().defaultBlockState());
        helper.setBlock(ACCUMULATOR_POS, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());
        helper.setBlock(OUTPUT_POS, NVBlocks.OUTPUT_ROUTING_NODE.block().get().defaultBlockState());

        BlockEntity masterBe = helper.getBlockEntity(MASTER_POS);
        BlockEntity accumulatorBe = helper.getBlockEntity(ACCUMULATOR_POS);
        BlockEntity outputBe = helper.getBlockEntity(OUTPUT_POS);
        if (!(masterBe instanceof MasterRoutingNodeBlockEntity master)
                || !(accumulatorBe instanceof SpiritAccumulatorBlockEntity accumulator)
                || !(outputBe instanceof OutputRoutingNodeBlockEntity output)) {
            helper.fail("Failed to place routing network blocks");
            return null;
        }

        RoutingLinkHelper.bindToMaster(helper.getLevel(), accumulator, helper.absolutePos(ACCUMULATOR_POS),
                master, helper.absolutePos(MASTER_POS));
        RoutingLinkHelper.bindToMaster(helper.getLevel(), output, helper.absolutePos(OUTPUT_POS),
                master, helper.absolutePos(MASTER_POS));

        for (SpiritusType type : TEST_TYPES) {
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), helper.absolutePos(ACCUMULATOR_POS), type, 1_000_000);
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), helper.absolutePos(OUTPUT_POS), type, 1_000_000);
        }
        return new Network(master, accumulator, output);
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 300)
    public void networkStocksOutputChunk(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Network net = buildNetwork(helper);
            if (net == null) return;

            net.accumulator().attuneTo(SpiritusType.RUINA);
            net.accumulator().insertSpiritus(SpiritusType.RUINA, 500);
            net.output().setSpiritusExport(SpiritusType.RUINA, 25);

            helper.succeedWhen(() -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.RUINA);
                helper.assertTrue(!(Math.abs(chunkAmount - 25) > 0.0001), "Output chunk should be stocked to 25 ruina, has " + chunkAmount);
                helper.assertTrue(!(Math.abs(net.accumulator().getStored() - 475) > 0.0001), "Accumulator should have paid 25, stored=" + net.accumulator().getStored());
            });
        });
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 200)
    public void wrongTypeIsNotExported(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Network net = buildNetwork(helper);
            if (net == null) return;

            net.accumulator().attuneTo(SpiritusType.VINDICTA);
            net.accumulator().insertSpiritus(SpiritusType.VINDICTA, 500);
            net.output().setSpiritusExport(SpiritusType.RUINA, 50);

            helper.runAfterDelay(120, () -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.RUINA);
                if (chunkAmount > 0) {
                    helper.fail("Ruina export must not draw from a vindicta accumulator, chunk=" + chunkAmount);
                    return;
                }
                if (net.accumulator().getStored() != 500) {
                    helper.fail("Accumulator should be untouched, stored=" + net.accumulator().getStored());
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 320)
    public void stockClampsToChunkMaxWithoutLoss(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Network net = buildNetwork(helper);
            if (net == null) return;

            net.accumulator().attuneTo(SpiritusType.RUINA);
            net.accumulator().insertSpiritus(SpiritusType.RUINA, 500);
            net.output().setSpiritusExport(SpiritusType.RUINA, 1000);

            helper.succeedWhen(() -> {
                double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.RUINA);
                double chunkMax = WorldSpiritusHandler.getMaxSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.RUINA);
                helper.assertTrue(!(chunkAmount > chunkMax + 0.0001), "Chunk exceeded its max: " + chunkAmount + " > " + chunkMax);
                helper.assertTrue(!(net.accumulator().getStored() >= 500), "Accumulator should have delivered into the chunk, stored=" + net.accumulator().getStored());
            });
        });
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 300)
    public void networkDrainKeepsAttunement(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Network net = buildNetwork(helper);
            if (net == null) return;

            net.accumulator().attuneTo(SpiritusType.RUINA);
            net.accumulator().insertSpiritus(SpiritusType.RUINA, 25);
            net.output().setSpiritusExport(SpiritusType.RUINA, 25);

            helper.succeedWhen(() -> {
                helper.assertTrue(!(net.accumulator().getStored() != 0), "Accumulator should be fully drained, stored=" + net.accumulator().getStored());
                helper.assertTrue(!(net.accumulator().getAttunedType() != SpiritusType.RUINA), "Network drain must keep the attunement, type=" + net.accumulator().getAttunedType());
            });
        });
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 120)
    public void accumulatorAutolinksToMaster(GameTestHelper helper) {
        helper.setBlock(MASTER_POS, NVBlocks.MASTER_ROUTING_NODE.block().get().defaultBlockState());

        helper.runAfterDelay(10, () -> {
            helper.setBlock(ACCUMULATOR_POS, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());

            helper.runAfterDelay(20, () -> {
                BlockEntity be = helper.getBlockEntity(ACCUMULATOR_POS);
                if (!(be instanceof SpiritAccumulatorBlockEntity accumulator)) {
                    helper.fail("No accumulator");
                    return;
                }
                if (!accumulator.getMasterPos().equals(helper.absolutePos(MASTER_POS))) {
                    helper.fail("Accumulator should autolink to the master, masterPos=" + accumulator.getMasterPos());
                    return;
                }
                helper.succeed();
            });
        });
    }
}
