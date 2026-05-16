package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiraInfernalisBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.TeleposerBlockEntity;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class MinorSystemTests {

    private MinorSystemTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("minor/blood_tank_stores_and_retrieves_fluid", 30, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.BLOOD_TANK.block().get().defaultBlockState());

            helper.runAfterDelay(1, () -> {
                BlockPos absPos = helper.absolutePos(pos);
                ResourceHandler<FluidResource> handler = helper.getLevel()
                        .getCapability(Capabilities.Fluid.BLOCK, absPos, null);
                if (handler == null) {
                    helper.fail("No fluid handler");
                    return;
                }

                FluidResource essentia = FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get());
                int filled;
                try (Transaction tx = Transaction.openRoot()) {
                    filled = handler.insert(0, essentia, 5000, tx);
                    tx.commit();
                }
                if (filled != 5000) {
                    helper.fail("Should fill 5000mB, filled " + filled);
                    return;
                }

                int drained;
                try (Transaction tx = Transaction.openRoot()) {
                    drained = handler.extract(0, essentia, 2000, tx);
                    tx.commit();
                }
                if (drained != 2000) {
                    helper.fail("Should drain 2000mB, drained " + drained);
                    return;
                }

                int remaining = handler.getAmountAsInt(0);
                if (remaining != 3000) {
                    helper.fail("Should have 3000mB remaining, has " + remaining);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("minor/blood_tank_respects_capacity", 30, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.BLOOD_TANK.block().get().defaultBlockState());

            helper.runAfterDelay(1, () -> {
                BlockPos absPos = helper.absolutePos(pos);
                ResourceHandler<FluidResource> handler = helper.getLevel()
                        .getCapability(Capabilities.Fluid.BLOCK, absPos, null);
                BloodTankBlockEntity tank = helper.getBlockEntity(pos, BloodTankBlockEntity.class);
                if (handler == null || tank == null) {
                    helper.fail("Missing handler or tank");
                    return;
                }

                int capacity = tank.getCapacity();
                FluidResource essentia = FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get());
                int filled;
                try (Transaction tx = Transaction.openRoot()) {
                    filled = handler.insert(0, essentia, capacity + 5000, tx);
                    tx.commit();
                }
                if (filled > capacity) {
                    helper.fail("Should not exceed capacity " + capacity + ", filled " + filled);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("minor/pylon_places_and_initializes", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.SPIRA_INFERNALIS.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                SpiraInfernalisBlockEntity pylon = helper.getBlockEntity(pos, SpiraInfernalisBlockEntity.class);
                if (pylon == null) {
                    helper.fail("Expected SpiraInfernalisBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("minor/teleposer_places_and_initializes", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.TELEPOSER.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                TeleposerBlockEntity teleposer = helper.getBlockEntity(pos, TeleposerBlockEntity.class);
                if (teleposer == null) {
                    helper.fail("Expected TeleposerBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("minor/incense_altar_places_and_initializes", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            helper.setBlock(pos, NVBlocks.INCENSE_ALTAR.block().get().defaultBlockState());

            helper.runAfterDelay(5, () -> {
                if (helper.getBlockEntity(pos, BlockEntity.class) == null) {
                    helper.fail("Expected IncenseAltarBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
