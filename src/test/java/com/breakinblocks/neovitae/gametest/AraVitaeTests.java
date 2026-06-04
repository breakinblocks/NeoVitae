package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AraVitaeTests {

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof AraVitaeTile altar)) {
            helper.fail("Expected AraVitaeTile at " + pos);
            return null;
        }
        return altar;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            if (altar.getTier() != 0) {
                helper.fail("Standalone altar should be tier 0, got " + altar.getTier());
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarAcceptsFluidInput(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            int filled = altar.fill(
                    new FluidStack(
                            NVFluids.ESSENTIA_VITAE_SOURCE.get(), 500),
                    IFluidHandler.FluidAction.EXECUTE);
            if (filled <= 0) {
                helper.fail("Altar should accept Life Essence, but fill returned " + filled);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void altarCraftsBlankSlate(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            altar.addSacrificeEV(2000, false);
            altar.inv.setStackInSlot(0, new ItemStack(Items.DEEPSLATE));

            helper.runAfterDelay(250, () -> {
                ItemStack result = altar.inv.getStackInSlot(0);
                if (!result.is(NVItems.TABULA_RASA.get())) {
                    helper.fail("Expected blank slate, got " + result + " (progress=" + altar.getProgress() + ", mainTank=" + altar.getMainTank() + ")");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarDoesNotCraftWithoutLP(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarDoesNotCraftWithEmptySlot(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarSlotCapsAtOne(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            ItemStack remainder = altar.inv.insertItem(0, new ItemStack(Items.DEEPSLATE, 64), false);
            int held = altar.inv.getStackInSlot(0).getCount();
            if (held != 1) {
                helper.fail("Altar slot should cap at 1 item, holds " + held);
            }
            if (remainder.getCount() != 63) {
                helper.fail("Altar should reject the surplus, returned " + remainder.getCount() + " (expected 63)");
            }
            helper.succeed();
        });
    }
}
