package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.routing.*;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;
import com.breakinblocks.neovitae.api.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.fluid.NVFluids;

import java.util.LinkedList;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class RoutingNodeTests {

    private static final int TICK_RATE = 20;

    // ==================== Helpers ====================

    private static <T extends BlockEntity> T placeAndGet(GameTestHelper helper, BlockPos pos,
                                                          net.minecraft.world.level.block.Block block, Class<T> type) {
        helper.setBlock(pos, block.defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!type.isInstance(be)) {
            helper.fail("Expected " + type.getSimpleName() + " at " + pos + " but got " + (be == null ? "null" : be.getClass().getSimpleName()));
        }
        return type.cast(be);
    }

    private static void connectToMaster(GameTestHelper helper, IRoutingNode node,
                                         IMasterRoutingNode master, BlockPos nodePos, BlockPos masterPos) {
        node.connectMasterToRemainingNode(helper.getLevel(), new LinkedList<>(), master);
        master.addConnection(nodePos, masterPos);
        master.addNodeToList(node);
        node.addConnection(masterPos);
    }

    /**
     * Layout: [srcChest] [input] [master] [output] [dstChest] at Y=1, with pass-all filters installed.
     */
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
        BlockPos absInput = helper.absolutePos(inputPos);
        BlockPos absOutput = helper.absolutePos(outputPos);

        connectToMaster(helper, input, master, absInput, absMaster);
        connectToMaster(helper, output, master, absOutput, absMaster);

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        return new RoutingTestContext(srcChestPos, inputPos, masterPos, outputPos, dstChestPos);
    }

    record RoutingTestContext(BlockPos srcChest, BlockPos input, BlockPos master, BlockPos output, BlockPos dstChest) {}

    /**
     * Describes how a routing node side should be configured.
     * A null spec means "disable this side" (blocks all routing).
     */
    record FilterSpec(FilterMode mode, net.minecraft.world.item.Item[] items) {}

    private static FilterSpec createWhitelistFilter(net.minecraft.world.item.Item... filterItems) {
        return new FilterSpec(FilterMode.WHITELIST, filterItems);
    }

    private static FilterSpec createBlacklistFilter(net.minecraft.world.item.Item... filterItems) {
        return new FilterSpec(FilterMode.BLACKLIST, filterItems);
    }

    /**
     * Apply a FilterSpec to a routing node side. A null spec disables the side entirely.
     */
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
        int limit = Math.min(spec.items().length, SideFilterConfig.GHOST_SLOTS);
        for (int i = 0; i < limit; i++) {
            cfg.setItemGhost(i, new ItemStack(spec.items()[i]));
        }
        node.setChanged();
    }

    private static int countItem(net.minecraft.world.Container container, net.minecraft.world.item.Item item) {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    // ==================== Core Transfer Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void basicItemTransfer(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

        helper.runAfterDelay(TICK_RATE * 3, () -> {
            ChestBlockEntity dstChest = (ChestBlockEntity) helper.getBlockEntity(ctx.dstChest);
            int dstCount = countItem(dstChest, Items.DIAMOND);

            if (dstCount == 0) {
                helper.fail("No diamonds transferred to destination chest");
            }

            int srcCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.srcChest), Items.DIAMOND);
            if (srcCount + dstCount != 32) {
                helper.fail("Items lost! src=" + srcCount + " dst=" + dstCount + " (expected total 32)");
            }

            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void fullTransferCompletes(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.IRON_INGOT, 64));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.dstChest), Items.IRON_INGOT);
            int srcCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.srcChest), Items.IRON_INGOT);

            if (srcCount > 0) {
                helper.fail("Source still has " + srcCount + " iron ingots, destination has " + dstCount);
            }
            if (dstCount != 64) {
                helper.fail("Destination has " + dstCount + " iron ingots, expected 64");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void emptySourceNoCrash(GameTestHelper helper) {
        setupLinearNetwork(helper);
        helper.runAfterDelay(TICK_RATE * 2, helper::succeed);
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void multipleItemTypes(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
        srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));
        srcChest.setItem(2, new ItemStack(Items.GOLD_INGOT, 16));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            ChestBlockEntity dstChest = (ChestBlockEntity) helper.getBlockEntity(ctx.dstChest);
            int diamonds = countItem(dstChest, Items.DIAMOND);
            int emeralds = countItem(dstChest, Items.EMERALD);
            int gold = countItem(dstChest, Items.GOLD_INGOT);

            if (diamonds != 16 || emeralds != 16 || gold != 16) {
                helper.fail("Transfer incomplete: diamonds=" + diamonds + " emeralds=" + emeralds + " gold=" + gold);
            }
            helper.succeed();
        });
    }

    // ==================== Network Topology Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void redstoneDisablesTransfer(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        helper.setBlock(new BlockPos(2, 1, 0), Blocks.REDSTONE_BLOCK.defaultBlockState());

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

        helper.runAfterDelay(TICK_RATE * 3, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.dstChest), Items.DIAMOND);
            if (dstCount > 0) {
                helper.fail("Items transferred despite redstone signal: " + dstCount + " diamonds in dst");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void relayNodeNetwork(GameTestHelper helper) {
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

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(srcChestPos);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(dstChestPos), Items.DIAMOND);
            if (dstCount != 16) {
                helper.fail("Relay network transfer failed: " + dstCount + "/16 diamonds arrived");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void disconnectedNoTransfer(GameTestHelper helper) {
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

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(srcChestPos);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

        helper.runAfterDelay(TICK_RATE * 2, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(dstChestPos), Items.DIAMOND);
            if (dstCount > 0) {
                helper.fail("Items transferred without master node: " + dstCount);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void twoOutputsSplit(GameTestHelper helper) {
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

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output1, Direction.EAST, passAll);
        setNodeFilter(output2, Direction.EAST, passAll);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(srcChestPos);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

        helper.runAfterDelay(TICK_RATE * 10, () -> {
            int count1 = countItem((ChestBlockEntity) helper.getBlockEntity(dst1ChestPos), Items.DIAMOND);
            int count2 = countItem((ChestBlockEntity) helper.getBlockEntity(dst2ChestPos), Items.DIAMOND);

            if (count1 + count2 != 64) {
                helper.fail("Not all items transferred: dst1=" + count1 + " dst2=" + count2 + " (expected total 64)");
            }
            helper.succeed();
        });
    }

    // ==================== Filter Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void whitelistFilterOnlyMatchingItems(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        InputRoutingNodeBlockEntity input = (InputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.input);
        setNodeFilter(input, Direction.WEST, createWhitelistFilter(Items.DIAMOND));

        OutputRoutingNodeBlockEntity output = (OutputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.output);
        setNodeFilter(output, Direction.EAST, createWhitelistFilter(Items.DIAMOND));

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
        srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            ChestBlockEntity dstChest = (ChestBlockEntity) helper.getBlockEntity(ctx.dstChest);
            ChestBlockEntity srcAfter = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);

            int dstDiamonds = countItem(dstChest, Items.DIAMOND);
            int dstEmeralds = countItem(dstChest, Items.EMERALD);
            int srcEmeralds = countItem(srcAfter, Items.EMERALD);

            if (dstDiamonds != 16) helper.fail("Expected 16 diamonds in dst, got " + dstDiamonds);
            if (dstEmeralds > 0) helper.fail("Emeralds should not have transferred (whitelist), but " + dstEmeralds + " arrived");
            if (srcEmeralds != 16) helper.fail("Source should still have 16 emeralds, has " + srcEmeralds);
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void blacklistFilterBlocksMatchingItems(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        InputRoutingNodeBlockEntity input = (InputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.input);
        setNodeFilter(input, Direction.WEST, createBlacklistFilter(Items.EMERALD));

        OutputRoutingNodeBlockEntity output = (OutputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.output);
        setNodeFilter(output, Direction.EAST, createBlacklistFilter(Items.EMERALD));

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));
        srcChest.setItem(1, new ItemStack(Items.EMERALD, 16));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            ChestBlockEntity dstChest = (ChestBlockEntity) helper.getBlockEntity(ctx.dstChest);
            ChestBlockEntity srcAfter = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);

            int dstDiamonds = countItem(dstChest, Items.DIAMOND);
            int dstEmeralds = countItem(dstChest, Items.EMERALD);
            int srcEmeralds = countItem(srcAfter, Items.EMERALD);

            if (dstDiamonds != 16) helper.fail("Expected 16 diamonds in dst, got " + dstDiamonds);
            if (dstEmeralds > 0) helper.fail("Emeralds should be blocked (blacklist), but " + dstEmeralds + " arrived");
            if (srcEmeralds != 16) helper.fail("Source should still have 16 emeralds, has " + srcEmeralds);
            helper.succeed();
        });
    }

    // ==================== Priority Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void higherPriorityOutputReceivesFirst(GameTestHelper helper) {
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 5; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
        }

        BlockPos srcChestPos = new BlockPos(1, 1, 2);
        BlockPos inputPos = new BlockPos(2, 1, 2);
        BlockPos masterPos = new BlockPos(3, 1, 2);
        BlockPos lowPriorityOutputPos = new BlockPos(4, 1, 1);
        BlockPos highPriorityOutputPos = new BlockPos(4, 1, 3);
        BlockPos lowDstPos = new BlockPos(5, 1, 1);
        BlockPos highDstPos = new BlockPos(5, 1, 3);

        helper.setBlock(srcChestPos, Blocks.CHEST.defaultBlockState());
        InputRoutingNodeBlockEntity input = placeAndGet(helper, inputPos, NVBlocks.INPUT_ROUTING_NODE.block().get(), InputRoutingNodeBlockEntity.class);
        MasterRoutingNodeBlockEntity master = placeAndGet(helper, masterPos, NVBlocks.MASTER_ROUTING_NODE.block().get(), MasterRoutingNodeBlockEntity.class);
        OutputRoutingNodeBlockEntity lowOutput = placeAndGet(helper, lowPriorityOutputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
        OutputRoutingNodeBlockEntity highOutput = placeAndGet(helper, highPriorityOutputPos, NVBlocks.OUTPUT_ROUTING_NODE.block().get(), OutputRoutingNodeBlockEntity.class);
        helper.setBlock(lowDstPos, Blocks.CHEST.defaultBlockState());
        helper.setBlock(highDstPos, Blocks.CHEST.defaultBlockState());

        BlockPos absMaster = helper.absolutePos(masterPos);
        connectToMaster(helper, input, master, helper.absolutePos(inputPos), absMaster);
        connectToMaster(helper, lowOutput, master, helper.absolutePos(lowPriorityOutputPos), absMaster);
        connectToMaster(helper, highOutput, master, helper.absolutePos(highPriorityOutputPos), absMaster);

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(lowOutput, Direction.EAST, passAll);
        setNodeFilter(highOutput, Direction.EAST, passAll);

        // Set priorities: high=5, low=0 (Direction.EAST = index 5 in 3DDataValue)
        highOutput.priorities[Direction.EAST.get3DDataValue()] = 5;
        lowOutput.priorities[Direction.EAST.get3DDataValue()] = 0;

        // Only 16 items — should all go to high priority output
        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(srcChestPos);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 16));

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int highCount = countItem((ChestBlockEntity) helper.getBlockEntity(highDstPos), Items.DIAMOND);
            int lowCount = countItem((ChestBlockEntity) helper.getBlockEntity(lowDstPos), Items.DIAMOND);

            if (highCount != 16) {
                helper.fail("High priority should get all 16 diamonds, got " + highCount + " (low got " + lowCount + ")");
            }
            helper.succeed();
        });
    }

    // ==================== Upgrade Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void stackUpgradeIncreasesTransferRate(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        // Add stack upgrades to master (doubles base transfer from 16 to 32 per tick)
        MasterRoutingNodeBlockEntity master = (MasterRoutingNodeBlockEntity) helper.getBlockEntity(ctx.master);
        master.setItem(MasterRoutingNodeBlockEntity.SLOT_STACK_UPGRADE, new ItemStack(Items.STONE, 1));

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

        // After one tick cycle, more than 16 should transfer (base=16, +16 per upgrade = 32)
        helper.runAfterDelay(TICK_RATE + 1, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.dstChest), Items.DIAMOND);
            if (dstCount <= 16) {
                helper.fail("Stack upgrade should increase transfer rate above 16, got " + dstCount);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void speedUpgradeReducesTickRate(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        // Add 10 speed upgrades (base tick rate 20, -10 = tick every 10)
        MasterRoutingNodeBlockEntity master = (MasterRoutingNodeBlockEntity) helper.getBlockEntity(ctx.master);
        master.setItem(MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE, new ItemStack(Items.STONE, 10));

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 64));

        // After 15 ticks (less than default 20), items should have transferred
        helper.runAfterDelay(15, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.dstChest), Items.DIAMOND);
            if (dstCount == 0) {
                helper.fail("Speed upgrade should allow transfer before tick 20, but nothing transferred by tick 15");
            }
            helper.succeed();
        });
    }

    // ==================== Fluid Transfer Tests ====================

    /**
     * Places a Blood Tank at the given position and sets its tier via NBT reload.
     */
    private static BloodTankBlockEntity placeBloodTank(GameTestHelper helper, BlockPos pos, int tier) {
        BloodTankBlockEntity tank = placeAndGet(helper, pos, NVBlocks.BLOOD_TANK.block().get(), BloodTankBlockEntity.class);
        net.minecraft.nbt.CompoundTag tag = tank.saveWithoutMetadata(helper.getLevel().registryAccess());
        tag.putInt("tier", tier);
        tank.loadWithComponents(tag, helper.getLevel().registryAccess());
        return tank;
    }

    private static int getFluidAmount(GameTestHelper helper, BlockPos pos) {
        IFluidHandler handler = helper.getLevel().getCapability(
                Capabilities.Fluid.BLOCK, helper.absolutePos(pos), null);
        if (handler == null) return 0;
        FluidStack fluid = handler.getFluidInTank(0);
        return fluid.isEmpty() ? 0 : fluid.getAmount();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void basicFluidTransfer(GameTestHelper helper) {
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

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        // Fill source tank with 4000 mB of Life Essence
        IFluidHandler srcHandler = helper.getLevel().getCapability(
                Capabilities.Fluid.BLOCK, helper.absolutePos(srcTankPos), null);
        if (srcHandler == null) {
            helper.fail("Source tank has no fluid handler");
            return;
        }
        srcHandler.fill(new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 4000), IFluidHandler.FluidAction.EXECUTE);

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int srcAmount = getFluidAmount(helper, srcTankPos);
            int dstAmount = getFluidAmount(helper, dstTankPos);

            if (dstAmount == 0) {
                helper.fail("No fluid transferred to destination tank (src has " + srcAmount + " mB)");
            }
            if (srcAmount + dstAmount != 4000) {
                helper.fail("Fluid lost! src=" + srcAmount + " dst=" + dstAmount + " (expected total 4000)");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void fluidTransferToEmptyTank(GameTestHelper helper) {
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

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        // Fill source, leave dest empty — tests the empty tank output filter bypass
        IFluidHandler srcHandler = helper.getLevel().getCapability(
                Capabilities.Fluid.BLOCK, helper.absolutePos(srcTankPos), null);
        srcHandler.fill(new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 2000), IFluidHandler.FluidAction.EXECUTE);

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int dstAmount = getFluidAmount(helper, dstTankPos);
            if (dstAmount == 0) {
                helper.fail("Fluid did not transfer to initially empty destination tank");
            }
            helper.succeed();
        });
    }

    // ==================== Energy Transfer Tests ====================

    private static TestEnergyBlock.TestEnergyBlockEntity placeEnergyBlock(GameTestHelper helper, BlockPos pos) {
        return placeAndGet(helper, pos, NVGameTestSetup.TEST_ENERGY_BLOCK.get(), TestEnergyBlock.TestEnergyBlockEntity.class);
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void basicEnergyTransfer(GameTestHelper helper) {
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

        FilterSpec passAll = createBlacklistFilter();
        setNodeFilter(input, Direction.WEST, passAll);
        setNodeFilter(output, Direction.EAST, passAll);

        srcEnergy.storage.receiveEnergy(50000, false);

        helper.runAfterDelay(TICK_RATE * 8, () -> {
            int srcAmount = srcEnergy.storage.getEnergyStored();
            int dstAmount = dstEnergy.storage.getEnergyStored();

            if (dstAmount == 0) {
                helper.fail("No energy transferred (src=" + srcAmount + " FE)");
            }
            if (srcAmount + dstAmount != 50000) {
                helper.fail("Energy lost! src=" + srcAmount + " dst=" + dstAmount + " (expected total 50000)");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void energyNoFilterNoTransfer(GameTestHelper helper) {
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

        // No filters installed
        srcEnergy.storage.receiveEnergy(50000, false);

        helper.runAfterDelay(TICK_RATE * 3, () -> {
            if (dstEnergy.storage.getEnergyStored() > 0) {
                helper.fail("Energy transferred without filter installed: " + dstEnergy.storage.getEnergyStored());
            }
            helper.succeed();
        });
    }

    // ==================== Edge Case Tests ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 80)
    public void noFilterNoTransfer(GameTestHelper helper) {
        RoutingTestContext ctx = setupLinearNetwork(helper);

        InputRoutingNodeBlockEntity input = (InputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.input);
        OutputRoutingNodeBlockEntity output = (OutputRoutingNodeBlockEntity) helper.getBlockEntity(ctx.output);
        setNodeFilter(input, Direction.WEST, (FilterSpec) null);
        setNodeFilter(output, Direction.EAST, (FilterSpec) null);

        ChestBlockEntity srcChest = (ChestBlockEntity) helper.getBlockEntity(ctx.srcChest);
        srcChest.setItem(0, new ItemStack(Items.DIAMOND, 32));

        helper.runAfterDelay(TICK_RATE * 3, () -> {
            int dstCount = countItem((ChestBlockEntity) helper.getBlockEntity(ctx.dstChest), Items.DIAMOND);
            if (dstCount > 0) {
                helper.fail("Items transferred without any filter installed: " + dstCount);
            }
            helper.succeed();
        });
    }
}
