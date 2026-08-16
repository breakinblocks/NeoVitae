package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

public final class SpiritusRoutingTests {

    private SpiritusRoutingTests() {}

    private static final BlockPos MASTER_POS = new BlockPos(1, 1, 1);
    private static final BlockPos ACCUMULATOR_POS = new BlockPos(3, 1, 1);
    private static final BlockPos OUTPUT_POS = new BlockPos(3, 1, 3);

    private record Network(MasterRoutingNodeBlockEntity master,
                           SpiritAccumulatorBlockEntity accumulator,
                           OutputRoutingNodeBlockEntity output) {}

    private static Network buildNetwork(GameTestHelper helper, SpiritusType... usedTypes) {
        helper.setBlock(MASTER_POS, NVBlocks.MASTER_ROUTING_NODE.block().get().defaultBlockState());
        helper.setBlock(ACCUMULATOR_POS, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());
        helper.setBlock(OUTPUT_POS, NVBlocks.OUTPUT_ROUTING_NODE.block().get().defaultBlockState());

        MasterRoutingNodeBlockEntity master = helper.getBlockEntity(MASTER_POS, MasterRoutingNodeBlockEntity.class);
        SpiritAccumulatorBlockEntity accumulator = helper.getBlockEntity(ACCUMULATOR_POS, SpiritAccumulatorBlockEntity.class);
        OutputRoutingNodeBlockEntity output = helper.getBlockEntity(OUTPUT_POS, OutputRoutingNodeBlockEntity.class);
        if (master == null || accumulator == null || output == null) {
            helper.fail("Failed to place routing network blocks");
            return null;
        }

        RoutingLinkHelper.bindToMaster(helper.getLevel(), accumulator, helper.absolutePos(ACCUMULATOR_POS),
                master, helper.absolutePos(MASTER_POS));
        RoutingLinkHelper.bindToMaster(helper.getLevel(), output, helper.absolutePos(OUTPUT_POS),
                master, helper.absolutePos(MASTER_POS));

        for (SpiritusType type : usedTypes) {
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), helper.absolutePos(ACCUMULATOR_POS), type, 1_000_000);
            WorldSpiritusHandler.drainSpiritusFromChunk(helper.getLevel(), helper.absolutePos(OUTPUT_POS), type, 1_000_000);
        }
        return new Network(master, accumulator, output);
    }

    public static void register(NVTestRegistrar r) {
        r.add("spiritus_routing/network_stocks_output_chunk", 300, helper -> {
            helper.runAfterDelay(1, () -> {
                Network net = buildNetwork(helper, SpiritusType.RUINA);
                if (net == null) return;

                net.accumulator().insertSpiritus(SpiritusType.RUINA, 500);
                net.output().setSpiritusExport(SpiritusType.RUINA, 50);

                helper.runAfterDelay(180, () -> {
                    double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.RUINA);
                    if (Math.abs(chunkAmount - 50) > 0.0001) {
                        helper.fail("Output chunk should be stocked to 50 ruina, has " + chunkAmount);
                        return;
                    }
                    if (Math.abs(net.accumulator().getStored() - 450) > 0.0001) {
                        helper.fail("Accumulator should have paid 50, stored=" + net.accumulator().getStored());
                        return;
                    }
                    helper.succeed();
                });
            });
        });


        r.add("spiritus_routing/wrong_type_is_not_exported", 200, helper -> {
            helper.runAfterDelay(1, () -> {
                Network net = buildNetwork(helper, SpiritusType.NIHILUM, SpiritusType.RAW);
                if (net == null) return;

                net.accumulator().insertSpiritus(SpiritusType.NIHILUM, 500);
                net.output().setSpiritusExport(SpiritusType.RAW, 50);

                helper.runAfterDelay(120, () -> {
                    if (net.accumulator().getStored() != 500) {
                        helper.fail("A raw export must not draw from a nihilum accumulator, stored="
                                + net.accumulator().getStored());
                        return;
                    }
                    helper.succeed();
                });
            });
        });


        r.add("spiritus_routing/stock_clamps_to_chunk_max_without_loss", 320, helper -> {
            helper.runAfterDelay(1, () -> {
                Network net = buildNetwork(helper, SpiritusType.INVICTUS);
                if (net == null) return;

                net.accumulator().insertSpiritus(SpiritusType.INVICTUS, 500);
                net.output().setSpiritusExport(SpiritusType.INVICTUS, 1000);

                helper.runAfterDelay(220, () -> {
                    double chunkAmount = WorldSpiritusHandler.getCurrentSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.INVICTUS);
                    double chunkMax = WorldSpiritusHandler.getMaxSpiritus(helper.getLevel(), helper.absolutePos(OUTPUT_POS), SpiritusType.INVICTUS);
                    if (chunkAmount > chunkMax + 0.0001) {
                        helper.fail("Chunk exceeded its max: " + chunkAmount + " > " + chunkMax);
                        return;
                    }
                    if (net.accumulator().getStored() >= 500) {
                        helper.fail("Accumulator should have delivered into the chunk, stored=" + net.accumulator().getStored());
                        return;
                    }
                    helper.succeed();
                });
            });
        });


        r.add("spiritus_routing/network_drain_keeps_attunement", 300, helper -> {
            helper.runAfterDelay(1, () -> {
                Network net = buildNetwork(helper, SpiritusType.VINDICTA);
                if (net == null) return;

                net.accumulator().insertSpiritus(SpiritusType.VINDICTA, 50);
                net.output().setSpiritusExport(SpiritusType.VINDICTA, 50);

                helper.runAfterDelay(180, () -> {
                    if (net.accumulator().getStored() != 0) {
                        helper.fail("Accumulator should be fully drained, stored=" + net.accumulator().getStored());
                        return;
                    }
                    if (net.accumulator().getAttunedType() != SpiritusType.VINDICTA) {
                        helper.fail("Network drain must keep the attunement, type=" + net.accumulator().getAttunedType());
                        return;
                    }
                    helper.succeed();
                });
            });
        });


        r.add("spiritus_routing/accumulator_autolinks_to_master", 120, helper -> {
            helper.setBlock(MASTER_POS, NVBlocks.MASTER_ROUTING_NODE.block().get().defaultBlockState());

            helper.runAfterDelay(10, () -> {
                helper.setBlock(ACCUMULATOR_POS, NVBlocks.SPIRIT_ACCUMULATOR.block().get().defaultBlockState());

                helper.runAfterDelay(20, () -> {
                    SpiritAccumulatorBlockEntity accumulator =
                            helper.getBlockEntity(ACCUMULATOR_POS, SpiritAccumulatorBlockEntity.class);
                    if (accumulator == null) {
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
        });
    }
}
