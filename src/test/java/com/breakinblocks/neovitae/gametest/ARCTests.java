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
import com.breakinblocks.neovitae.common.blockentity.ARCBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class ARCTests {

    private static ARCBlockEntity placeARC(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARC_BLOCK.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof ARCBlockEntity arc)) {
            helper.fail("Expected ARCBlockEntity at " + pos);
            return null;
        }
        return arc;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arcPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        ARCBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (arc == null) return;
            if (arc.getProgressForGui() != 0) {
                helper.fail("Fresh ARC should have 0 progress");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void arcSmeltsIronOre(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        ARCBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.arcInv.setStackInSlot(ARCBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.arcInv.setStackInSlot(ARCBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_IRON));

            helper.runAfterDelay(250, () -> {
                ItemStack output = arc.arcInv.getStackInSlot(ARCBlockEntity.OUTPUT_SLOT);
                if (output.isEmpty()) {
                    helper.fail("ARC should have smelted iron ore, output is empty");
                }
                if (!output.is(Items.IRON_INGOT)) {
                    helper.fail("Expected iron ingot, got " + output);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 150)
    public void arcDoesNotCraftWithoutTool(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        ARCBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.arcInv.setStackInSlot(ARCBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_IRON));

            helper.runAfterDelay(110, () -> {
                ItemStack output = arc.arcInv.getStackInSlot(ARCBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("ARC should not smelt without tool, got " + output);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 150)
    public void arcDoesNotCraftWithoutInput(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        ARCBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.arcInv.setStackInSlot(ARCBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));

            helper.runAfterDelay(110, () -> {
                ItemStack output = arc.arcInv.getStackInSlot(ARCBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("ARC should not produce output without input, got " + output);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void arcConsumesInput(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        ARCBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.arcInv.setStackInSlot(ARCBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.arcInv.setStackInSlot(ARCBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_IRON));

            helper.runAfterDelay(250, () -> {
                ItemStack input = arc.arcInv.getStackInSlot(ARCBlockEntity.INPUT_SLOT);
                if (!input.isEmpty()) {
                    helper.fail("Input should be consumed after smelting, has " + input);
                }
                helper.succeed();
            });
        });
    }
}
