package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.common.item.NVItems;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class BloodAltarTests {

    private static BloodAltarTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.BLOOD_ALTAR.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof BloodAltarTile altar)) {
            helper.fail("Expected BloodAltarTile at " + pos);
            return null;
        }
        return altar;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        BloodAltarTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

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
        BloodAltarTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            int filled = altar.fill(
                    new net.neoforged.neoforge.fluids.FluidStack(
                            com.breakinblocks.neovitae.common.fluid.NVFluids.LIFE_ESSENCE_SOURCE.get(), 500),
                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            if (filled <= 0) {
                helper.fail("Altar should accept Life Essence, but fill returned " + filled);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void altarCraftsBlankSlate(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        BloodAltarTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            // Add LP directly to mainTank (blank slate needs 1000)
            altar.addSacrificeLP(2000, false);
            altar.inv.setStackInSlot(0, new ItemStack(Items.STONE));

            helper.runAfterDelay(250, () -> {
                ItemStack result = altar.inv.getStackInSlot(0);
                if (!result.is(NVItems.SLATE_BLANK.get())) {
                    helper.fail("Expected blank slate, got " + result + " (progress=" + altar.getProgress() + ", mainTank=" + altar.getMainTank() + ")");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarDoesNotCraftWithoutLP(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        BloodAltarTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            // No LP in altar, place stone
            altar.inv.setStackInSlot(0, new ItemStack(Items.STONE));

            helper.runAfterDelay(40, () -> {
                ItemStack result = altar.inv.getStackInSlot(0);
                if (!result.is(Items.STONE)) {
                    helper.fail("Altar should not craft without LP, but item changed to " + result);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void altarDoesNotCraftWithEmptySlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        BloodAltarTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) return;
            altar.addSacrificeLP(2000, false);

            helper.runAfterDelay(40, () -> {
                if (altar.isActive()) {
                    helper.fail("Altar should not be active with empty slot");
                }
                helper.succeed();
            });
        });
    }
}
