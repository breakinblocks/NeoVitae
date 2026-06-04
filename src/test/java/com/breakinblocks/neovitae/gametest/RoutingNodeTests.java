package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingConduitBlockEntity;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

import java.lang.reflect.Field;
import java.util.LinkedList;

public final class RoutingNodeTests {

    private RoutingNodeTests() {}

    private static final int TICK_RATE = 20;

    private static <T extends BlockEntity> T placeAndGet(GameTestHelper helper, BlockPos pos,
                                                         Block block, Class<T> type) {
        helper.setBlock(pos, block.defaultBlockState());
        T be = helper.getBlockEntity(pos, type);
        if (be == null) {
            helper.fail("Expected " + type.getSimpleName() + " at " + pos);
        }
        return be;
    }

    private static void connectToMaster(GameTestHelper helper, IRoutingNode node,
                                        IMasterRoutingNode master, BlockPos nodePos, BlockPos masterPos) {
        node.connectMasterToRemainingNode(helper.getLevel(), new LinkedList<>(), master);
        master.addConnection(nodePos, masterPos);
        master.addNodeToList(node);
        node.addConnection(masterPos);
    }

    record RoutingTestContext(BlockPos srcChest, BlockPos input, BlockPos master, BlockPos output, BlockPos dstChest) {}

    record FilterSpec(FilterMode mode, Item[] items) {}

    private static FilterSpec whitelist(Item... items) {
        return new FilterSpec(FilterMode.WHITELIST, items);
    }

    private static FilterSpec blacklist(Item... items) {
        return new FilterSpec(FilterMode.BLACKLIST, items);
    }

    private static void setNodeFilter(FilteredRoutingNodeBlockEntity node, Direction side, FilterSpec spec) {
        SideFilterConfig cfg = node.getSideFilter(side);
        if (spec == null) {
            cfg.setEnabled(false);
            cfg.clearItemGhosts();
            node.setChanged();
            return;
        }
        cfg.setEnabled(true);
        cfg.setItemMode(spec.mode());
        cfg.clearItemGhosts();
        int limit = Math.min(spec.items().length, SideFilterConfig.PAGE_SIZE);
        for (int i = 0; i < limit; i++) {
            cfg.setItemGhost(i, new ItemStack(spec.items()[i]));
        }
        node.setChanged();
    }

    private static int countItem(Container container, Item item) {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private static RoutingTestContext setupLinearNetwork(GameTestHelper helper) {
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 3; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
        }

        BlockPos srcChestPos = new BlockPos(1, 1, 1);
        BlockPos inputPos = new BlockPos(2, 1, 1);
        BlockPos masterPos = new BlockPos(3, 1, 1);
        BlockPos outputPos = new BlockPos(4, 1, 1);
        BlockPos dstChestPos = new BlockPos(5, 1, 1);

        helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
        InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
        MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
        OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
        helper.setBlock(dstChestPos, Blocks.CHEST.defaultBlockState());

        BlockPos absMaster = helper.absolutePos(masterPos);
        connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
        connectToMaster(helper, output, master, helper.absolutePos(outputPos), absMaster);

        FilterSpec passAll = blacklist();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        return new RoutingTestContext(srcChestPos, inputPos, masterPos, outputPos, dstChestPos);
    }

    private static BloodTankBlockEntity placeBloodTank(GameTestHelper helper, BlockPos pos, int tier) {
        BloodTankBlockEntity tank = placeAndGet(helper, pos, NVBlocks.BLOOD_TANK.block().get(), BloodTankBlockEntity.class);
        try {
            Field tierField = BloodTankBlockEntity.class.getDeclaredField("tier");
            tierField.setAccessible(true);
            tierField.setInt(tank, tier);
            java.lang.reflect.Method update = BloodTankBlockEntity.class.getDeclaredMethod("updateCapacity");
            update.setAccessible(true);
            update.invoke(tank);
        } catch (ReflectiveOperationException e) {
            helper.fail("Failed to set blood tank tier via reflection: " + e.getMessage());
        }
        return tank;
    }

    private static int getFluidAmount(GameTestHelper helper, BlockPos pos) {
        ResourceHandler<FluidResource> handler = helper.getLevel().getCapability(
                Capabilities.Fluid.BLOCK, helper.absolutePos(pos), null);
        if (handler == null) return 0;
        return handler.getAmountAsInt(0);
    }

    private static TestEnergyBlock.TestEnergyBlockEntity placeEnergyBlock(GameTestHelper helper, BlockPos pos) {
        return placeAndGet(helper, pos, NVGameTestSetup.TEST_ENERGY_BLOCK.get(), TestEnergyBlock.TestEnergyBlockEntity.class);
    }

    public static void register(NVTestRegistrar r) {
        r.add("routing/basic_item_transfer", 80, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

            helper.runAfterDelay(TICK_RATE * 3, () -> {
                ChestBlockEntity dstChest = helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class);
                int dstCount = countItem(dstChest, Items.DIAMOND);
                if (dstCount == 0) helper.fail("No diamonds transferred to destination chest");
                int srcCount = countItem(helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class), Items.DIAMOND);
                if (srcCount + dstCount != 32) {
                    helper.fail("Items lost! src=" + srcCount + " dst=" + dstCount + " (expected total 32)");
                }
                helper.succeed();
            });
        });

        r.add("routing/full_transfer_completes", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.IRON_INGOT, 64));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int dstCount = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.IRON_INGOT);
                int srcCount = countItem(helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class), Items.IRON_INGOT);
                if (srcCount > 0) helper.fail("Source still has " + srcCount + " iron ingots, destination has " + dstCount);
                if (dstCount != 64) helper.fail("Destination has " + dstCount + " iron ingots, expected 64");
                helper.succeed();
            });
        });

        r.add("routing/empty_source_no_crash", 60, helper -> {
            setupLinearNetwork(helper);
            helper.runAfterDelay(TICK_RATE * 2, helper::succeed);
        });

        r.add("routing/output_keep_amount", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            OutputRoutingNodeBlockEntity output = helper.getBlockEntity(ctx.output, OutputRoutingNodeBlockEntity.class);
            SideFilterConfig cfg = output.getSideFilter(Direction.EAST);
            cfg.setEnabled(true);
            cfg.setItemMode(FilterMode.WHITELIST);
            cfg.clearItemGhosts();
            cfg.setItemGhost(0, new ItemStack(Items.COBBLESTONE));
            cfg.setItemAmount(0, 10);
            output.setChanged();

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.COBBLESTONE, 64));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int dst = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.COBBLESTONE);
                int src = countItem(helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class), Items.COBBLESTONE);
                if (dst != 10) helper.fail("Output keep-amount should fill destination to 10, got dst=" + dst + " src=" + src);
                if (src != 54) helper.fail("Output keep-amount should leave 54 in source, got src=" + src);
                helper.succeed();
            });
        });

        r.add("routing/input_keep_amount", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            InputRoutingNodeBlockEntity input = helper.getBlockEntity(ctx.input, InputRoutingNodeBlockEntity.class);
            SideFilterConfig cfg = input.getSideFilter(Direction.WEST);
            cfg.setEnabled(true);
            cfg.setItemMode(FilterMode.WHITELIST);
            cfg.clearItemGhosts();
            cfg.setItemGhost(0, new ItemStack(Items.COBBLESTONE));
            cfg.setItemAmount(0, 10);
            input.setChanged();

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.COBBLESTONE, 64));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int dst = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.COBBLESTONE);
                int src = countItem(helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class), Items.COBBLESTONE);
                if (src != 10) helper.fail("Input keep-amount should leave 10 in source, got src=" + src + " dst=" + dst);
                if (dst != 54) helper.fail("Input keep-amount should pull 54 to destination, got dst=" + dst);
                helper.succeed();
            });
        });

        r.add("routing/filter_entries_beyond_old_cap", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            Item[] blocked = {
                    Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT,
                    Items.COAL, Items.REDSTONE, Items.LAPIS_LAZULI, Items.QUARTZ,
                    Items.COPPER_INGOT, Items.AMETHYST_SHARD, Items.NETHERITE_SCRAP, Items.NETHERITE_INGOT
            };
            OutputRoutingNodeBlockEntity output = helper.getBlockEntity(ctx.output, OutputRoutingNodeBlockEntity.class);
            setNodeFilter(output, Direction.EAST, blacklist(blocked));

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.NETHERITE_INGOT, 4));
            srcChest.setItem(1, new ItemStack(Items.DIRT, 4));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                ChestBlockEntity dstChest = helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class);
                int dirtDst = countItem(dstChest, Items.DIRT);
                int netheriteDst = countItem(dstChest, Items.NETHERITE_INGOT);
                if (dirtDst != 4) helper.fail("Unblocked item should pass, dirt in dst=" + dirtDst);
                if (netheriteDst != 0) helper.fail("Blacklist entry at index 11 should block, netherite in dst=" + netheriteDst);
                helper.succeed();
            });
        });

        r.add("routing/multiple_item_types", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
            srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));
            srcChest.setItem(2, new ItemStack(Items.GOLD_INGOT, 16));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                ChestBlockEntity dstChest = helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class);
                int diamonds = countItem(dstChest, Items.DIAMOND);
                int emeralds = countItem(dstChest, Items.EMERALD);
                int gold = countItem(dstChest, Items.GOLD_INGOT);
                if (diamonds != 16 || emeralds != 16 || gold != 16) {
                    helper.fail("Transfer incomplete: diamonds=" + diamonds + " emeralds=" + emeralds + " gold=" + gold);
                }
                helper.succeed();
            });
        });

        r.add("routing/redstone_disables_transfer", 80, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            helper.setBlock(new BlockPos(2, 1, 0), Blocks.REDSTONE_BLOCK.defaultBlockState());

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

            helper.runAfterDelay(TICK_RATE * 3, () -> {
                int dstCount = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount > 0) helper.fail("Items transferred despite redstone signal: " + dstCount + " diamonds in dst");
                helper.succeed();
            });
        });

        r.add("routing/relay_node_network", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcChestPos = new BlockPos(0, 1, 1);
            BlockPos inputPos = new BlockPos(1, 1, 1);
            BlockPos relayPos = new BlockPos(2, 1, 1);
            BlockPos masterPos = new BlockPos(3, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstChestPos = new BlockPos(5, 1, 1);

            helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            RoutingConduitBlockEntity relay = placeAndGet(helper, relayPos, NVBlocks.ROUTING_CONDUIT.block().get(), RoutingConduitBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            helper.setBlock(dstChestPos, Blocks.CHEST.defaultBlockState());

            BlockPos absMaster = helper.absolutePos(masterPos);
            BlockPos absInput = helper.absolutePos(inputPos);
            BlockPos absRelay = helper.absolutePos(relayPos);
            BlockPos absOutput = helper.absolutePos(outputPos);

            connectToMaster(helper, relay, master, absRelay, absMaster);
            master.addNodeToList(input);
            input.addConnection(absRelay);
            relay.addConnection(absInput);
            master.addConnection(absRelay, absInput);
            input.connectMasterToRemainingNode(helper.getLevel(), new LinkedList<>(), master);
            connectToMaster(helper, output, master, absOutput, absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(output, Direction.EAST, passAll);

            ChestBlockEntity srcChest = helper.getBlockEntity(srcChestPos, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int dstCount = countItem(helper.getBlockEntity(dstChestPos, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount != 16) helper.fail("Relay network transfer failed: " + dstCount + "/16 diamonds arrived");
                helper.succeed();
            });
        });

        r.add("routing/disconnected_no_transfer", 60, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcChestPos = new BlockPos(1, 1, 1);
            BlockPos inputPos = new BlockPos(2, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstChestPos = new BlockPos(5, 1, 1);

            helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
            placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            helper.setBlock(dstChestPos, Blocks.CHEST.defaultBlockState());

            ChestBlockEntity srcChest = helper.getBlockEntity(srcChestPos, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

            helper.runAfterDelay(TICK_RATE * 2, () -> {
                int dstCount = countItem(helper.getBlockEntity(dstChestPos, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount > 0) helper.fail("Items transferred without master node: " + dstCount);
                helper.succeed();
            });
        });

        r.add("routing/two_outputs_split", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 5; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcChestPos = new BlockPos(1, 1, 2);
            BlockPos inputPos = new BlockPos(2, 1, 2);
            BlockPos masterPos = new BlockPos(3, 1, 2);
            BlockPos output1Pos = new BlockPos(4, 1, 1);
            BlockPos output2Pos = new BlockPos(4, 1, 3);
            BlockPos dst1ChestPos = new BlockPos(5, 1, 1);
            BlockPos dst2ChestPos = new BlockPos(5, 1, 3);

            helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output1 = placeAndGet(helper, output1Pos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output2 = placeAndGet(helper, output2Pos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            helper.setBlock(dst1ChestPos, Blocks.CHEST.defaultBlockState());
            helper.setBlock(dst2ChestPos, Blocks.CHEST.defaultBlockState());

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, output1, master, helper.absolutePos(output1Pos), absMaster);
            connectToMaster(helper, output2, master, helper.absolutePos(output2Pos), absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(output1, Direction.EAST, passAll);
            setNodeFilter(output2, Direction.EAST, passAll);

            ChestBlockEntity srcChest = helper.getBlockEntity(srcChestPos, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

            helper.runAfterDelay(TICK_RATE * 10, () -> {
                int count1 = countItem(helper.getBlockEntity(dst1ChestPos, ChestBlockEntity.class), Items.DIAMOND);
                int count2 = countItem(helper.getBlockEntity(dst2ChestPos, ChestBlockEntity.class), Items.DIAMOND);
                if (count1 + count2 != 64) {
                    helper.fail("Not all items transferred: dst1=" + count1 + " dst2=" + count2 + " (expected total 64)");
                }
                helper.succeed();
            });
        });

        r.add("routing/whitelist_filter", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);

            InputRoutingNodeBlockEntity input = helper.getBlockEntity(ctx.input, InputRoutingNodeBlockEntity.class);
            setNodeFilter(input, Direction.WEST, whitelist(Items.DIAMOND));
            OutputRoutingNodeBlockEntity output = helper.getBlockEntity(ctx.output, OutputRoutingNodeBlockEntity.class);
            setNodeFilter(output, Direction.EAST, whitelist(Items.DIAMOND));

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
            srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                ChestBlockEntity dstChest = helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class);
                ChestBlockEntity srcAfter = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
                int dstDiamonds = countItem(dstChest, Items.DIAMOND);
                int dstEmeralds = countItem(dstChest, Items.EMERALD);
                int srcEmeralds = countItem(srcAfter, Items.EMERALD);
                if (dstDiamonds != 16) helper.fail("Expected 16 diamonds in dst, got " + dstDiamonds);
                if (dstEmeralds > 0) helper.fail("Emeralds should not have transferred (whitelist), but " + dstEmeralds + " arrived");
                if (srcEmeralds != 16) helper.fail("Source should still have 16 emeralds, has " + srcEmeralds);
                helper.succeed();
            });
        });

        r.add("routing/blacklist_filter", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);

            InputRoutingNodeBlockEntity input = helper.getBlockEntity(ctx.input, InputRoutingNodeBlockEntity.class);
            setNodeFilter(input, Direction.WEST, blacklist(Items.EMERALD));
            OutputRoutingNodeBlockEntity output = helper.getBlockEntity(ctx.output, OutputRoutingNodeBlockEntity.class);
            setNodeFilter(output, Direction.EAST, blacklist(Items.EMERALD));

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
            srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                ChestBlockEntity dstChest = helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class);
                ChestBlockEntity srcAfter = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
                int dstDiamonds = countItem(dstChest, Items.DIAMOND);
                int dstEmeralds = countItem(dstChest, Items.EMERALD);
                int srcEmeralds = countItem(srcAfter, Items.EMERALD);
                if (dstDiamonds != 16) helper.fail("Expected 16 diamonds in dst, got " + dstDiamonds);
                if (dstEmeralds > 0) helper.fail("Emeralds should be blocked (blacklist), but " + dstEmeralds + " arrived");
                if (srcEmeralds != 16) helper.fail("Source should still have 16 emeralds, has " + srcEmeralds);
                helper.succeed();
            });
        });

        r.add("routing/higher_priority_output_first", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 5; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcChestPos = new BlockPos(1, 1, 2);
            BlockPos inputPos = new BlockPos(2, 1, 2);
            BlockPos masterPos = new BlockPos(3, 1, 2);
            BlockPos lowOutputPos = new BlockPos(4, 1, 1);
            BlockPos highOutputPos = new BlockPos(4, 1, 3);
            BlockPos lowDstPos = new BlockPos(5, 1, 1);
            BlockPos highDstPos = new BlockPos(5, 1, 3);

            helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity lowOutput = placeAndGet(helper, lowOutputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity highOutput = placeAndGet(helper, highOutputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            helper.setBlock(lowDstPos, Blocks.CHEST.defaultBlockState());
            helper.setBlock(highDstPos, Blocks.CHEST.defaultBlockState());

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, lowOutput, master, helper.absolutePos(lowOutputPos), absMaster);
            connectToMaster(helper, highOutput, master, helper.absolutePos(highOutputPos), absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(lowOutput, Direction.EAST, passAll);
            setNodeFilter(highOutput, Direction.EAST, passAll);

            highOutput.priorities[Direction.EAST.get3DDataValue()] = 5;
            lowOutput.priorities[Direction.EAST.get3DDataValue()] = 0;

            ChestBlockEntity srcChest = helper.getBlockEntity(srcChestPos, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int highCount = countItem(helper.getBlockEntity(highDstPos, ChestBlockEntity.class), Items.DIAMOND);
                int lowCount = countItem(helper.getBlockEntity(lowDstPos, ChestBlockEntity.class), Items.DIAMOND);
                if (highCount != 16) {
                    helper.fail("High priority should get all 16 diamonds, got " + highCount + " (low got " + lowCount + ")");
                }
                helper.succeed();
            });
        });

        r.add("routing/stack_upgrade_increases_rate", 200, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            MasterRoutingNodeBlockEntity master = helper.getBlockEntity(ctx.master, MasterRoutingNodeBlockEntity.class);
            master.setItem(MasterRoutingNodeBlockEntity.SLOT_STACK_UPGRADE, new ItemStack(Items.STONE, 1));

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

            helper.runAfterDelay(TICK_RATE + 1, () -> {
                int dstCount = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount <= 16) {
                    helper.fail("Stack upgrade should increase transfer rate above 16, got " + dstCount);
                }
                helper.succeed();
            });
        });

        r.add("routing/speed_upgrade_reduces_tick_rate", 60, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);
            MasterRoutingNodeBlockEntity master = helper.getBlockEntity(ctx.master, MasterRoutingNodeBlockEntity.class);
            master.setItem(MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE, new ItemStack(Items.STONE, 10));

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

            helper.runAfterDelay(15, () -> {
                int dstCount = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount == 0) {
                    helper.fail("Speed upgrade should allow transfer before tick 20, but nothing transferred by tick 15");
                }
                helper.succeed();
            });
        });

        r.add("routing/basic_fluid_transfer", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcTankPos = new BlockPos(1, 1, 1);
            BlockPos inputPos = new BlockPos(2, 1, 1);
            BlockPos masterPos = new BlockPos(3, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstTankPos = new BlockPos(5, 1, 1);

            placeBloodTank(helper, srcTankPos, 1);
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            placeBloodTank(helper, dstTankPos, 1);

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, output, master, helper.absolutePos(outputPos), absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(output, Direction.EAST, passAll);

            ResourceHandler<FluidResource> srcHandler = helper.getLevel().getCapability(
                    Capabilities.Fluid.BLOCK, helper.absolutePos(srcTankPos), null);
            if (srcHandler == null) {
                helper.fail("Source tank has no fluid handler");
                return;
            }
            try (Transaction tx = Transaction.openRoot()) {
                srcHandler.insert(0, FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get()), 4000, tx);
                tx.commit();
            }

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int srcAmount = getFluidAmount(helper, srcTankPos);
                int dstAmount = getFluidAmount(helper, dstTankPos);
                if (dstAmount == 0) helper.fail("No fluid transferred to destination tank (src has " + srcAmount + " mB)");
                if (srcAmount + dstAmount != 4000) {
                    helper.fail("Fluid lost! src=" + srcAmount + " dst=" + dstAmount + " (expected total 4000)");
                }
                helper.succeed();
            });
        });

        r.add("routing/fluid_transfer_to_empty_tank", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcTankPos = new BlockPos(1, 1, 1);
            BlockPos inputPos = new BlockPos(2, 1, 1);
            BlockPos masterPos = new BlockPos(3, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstTankPos = new BlockPos(5, 1, 1);

            placeBloodTank(helper, srcTankPos, 1);
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            placeBloodTank(helper, dstTankPos, 1);

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, output, master, helper.absolutePos(outputPos), absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(output, Direction.EAST, passAll);

            ResourceHandler<FluidResource> srcHandler = helper.getLevel().getCapability(
                    Capabilities.Fluid.BLOCK, helper.absolutePos(srcTankPos), null);
            try (Transaction tx = Transaction.openRoot()) {
                srcHandler.insert(0, FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get()), 2000, tx);
                tx.commit();
            }

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                if (getFluidAmount(helper, dstTankPos) == 0) {
                    helper.fail("Fluid did not transfer to initially empty destination tank");
                }
                helper.succeed();
            });
        });

        r.add("routing/basic_energy_transfer", 200, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcPos = new BlockPos(1, 1, 1);
            BlockPos inputPos = new BlockPos(2, 1, 1);
            BlockPos masterPos = new BlockPos(3, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstPos = new BlockPos(5, 1, 1);

            TestEnergyBlock.TestEnergyBlockEntity srcEnergy = placeEnergyBlock(helper, srcPos);
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            TestEnergyBlock.TestEnergyBlockEntity dstEnergy = placeEnergyBlock(helper, dstPos);

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, output, master, helper.absolutePos(outputPos), absMaster);

            FilterSpec passAll = blacklist();
            setNodeFilter(input, Direction.WEST, passAll);
            setNodeFilter(output, Direction.EAST, passAll);

            try (Transaction tx = Transaction.openRoot()) {
                srcEnergy.storage.insert(50000, tx);
                tx.commit();
            }

            helper.runAfterDelay(TICK_RATE * 8, () -> {
                int srcAmount = srcEnergy.storage.getAmountAsInt();
                int dstAmount = dstEnergy.storage.getAmountAsInt();
                if (dstAmount == 0) helper.fail("No energy transferred (src=" + srcAmount + " FE)");
                if (srcAmount + dstAmount != 50000) {
                    helper.fail("Energy lost! src=" + srcAmount + " dst=" + dstAmount + " (expected total 50000)");
                }
                helper.succeed();
            });
        });

        r.add("routing/energy_no_filter_no_transfer", 80, helper -> {
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 3; z++) {
                    helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                }
            }

            BlockPos srcPos = new BlockPos(1, 1, 1);
            BlockPos inputPos = new BlockPos(2, 1, 1);
            BlockPos masterPos = new BlockPos(3, 1, 1);
            BlockPos outputPos = new BlockPos(4, 1, 1);
            BlockPos dstPos = new BlockPos(5, 1, 1);

            TestEnergyBlock.TestEnergyBlockEntity srcEnergy = placeEnergyBlock(helper, srcPos);
            InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
            MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = placeAndGet(helper, outputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
            TestEnergyBlock.TestEnergyBlockEntity dstEnergy = placeEnergyBlock(helper, dstPos);

            BlockPos absMaster = helper.absolutePos(masterPos);
            connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
            connectToMaster(helper, output, master, helper.absolutePos(outputPos), absMaster);

            try (Transaction tx = Transaction.openRoot()) {
                srcEnergy.storage.insert(50000, tx);
                tx.commit();
            }

            helper.runAfterDelay(TICK_RATE * 3, () -> {
                if (dstEnergy.storage.getAmountAsInt() > 0) {
                    helper.fail("Energy transferred without filter installed: " + dstEnergy.storage.getAmountAsInt());
                }
                helper.succeed();
            });
        });

        r.add("routing/no_filter_no_transfer", 80, helper -> {
            RoutingTestContext ctx = setupLinearNetwork(helper);

            InputRoutingNodeBlockEntity input = helper.getBlockEntity(ctx.input, InputRoutingNodeBlockEntity.class);
            OutputRoutingNodeBlockEntity output = helper.getBlockEntity(ctx.output, OutputRoutingNodeBlockEntity.class);
            setNodeFilter(input, Direction.WEST, (FilterSpec) null);
            setNodeFilter(output, Direction.EAST, (FilterSpec) null);

            ChestBlockEntity srcChest = helper.getBlockEntity(ctx.srcChest, ChestBlockEntity.class);
            srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

            helper.runAfterDelay(TICK_RATE * 3, () -> {
                int dstCount = countItem(helper.getBlockEntity(ctx.dstChest, ChestBlockEntity.class), Items.DIAMOND);
                if (dstCount > 0) helper.fail("Items transferred without any filter installed: " + dstCount);
                helper.succeed();
            });
        });
    }
}
