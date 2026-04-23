package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class AraVitaeTests {

    private AraVitaeTests() {}

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = helper.getBlockEntity(pos, AraVitaeTile.class);
        if (altar == null) {
            helper.fail("Expected AraVitaeTile at " + pos);
        }
        return altar;
    }

    public static void register(NVTestRegistrar r) {
        r.add("ara_vitae/places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                if (altar.getTier() != 0) {
                    helper.fail("Standalone altar should be tier 0, got " + altar.getTier());
                }
                helper.succeed();
            });
        });

        r.add("ara_vitae/accepts_fluid_input", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                ResourceHandler<FluidResource> handler = altar.getFluidHandler();
                int filled;
                try (Transaction tx = Transaction.openRoot()) {
                    filled = handler.insert(0, FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get()), 500, tx);
                    tx.commit();
                }
                if (filled <= 0) {
                    helper.fail("Altar should accept Life Essence, inserted=" + filled);
                }
                helper.succeed();
            });
        });

        r.add("ara_vitae/crafts_blank_slate", 300, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.addSacrificeEV(2000, false);
                altar.inv.setStackInSlot(0, new ItemStack(Items.STONE));

                helper.runAfterDelay(250, () -> {
                    ItemStack result = altar.inv.getStackInSlot(0);
                    if (!result.is(NVItems.SLATE_BLANK.get())) {
                        helper.fail("Expected blank slate, got " + result + " (progress=" + altar.getProgress() + ", mainTank=" + altar.getMainTank() + ")");
                    }
                    helper.succeed();
                });
            });
        });

        r.add("ara_vitae/does_not_craft_without_lp", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.inv.setStackInSlot(0, new ItemStack(Items.STONE));

                helper.runAfterDelay(40, () -> {
                    ItemStack result = altar.inv.getStackInSlot(0);
                    if (!result.is(Items.STONE)) {
                        helper.fail("Altar should not craft without EV, but item changed to " + result);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("ara_vitae/does_not_craft_with_empty_slot", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.addSacrificeEV(2000, false);

                helper.runAfterDelay(40, () -> {
                    if (altar.isActive()) {
                        helper.fail("Altar should not be active with empty slot");
                    }
                    helper.succeed();
                });
            });
        });
    }
}
