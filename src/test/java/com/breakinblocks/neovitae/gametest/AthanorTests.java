package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class AthanorTests {

    private AthanorTests() {}

    private static AthanorBlockEntity placeARC(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ATHANOR_BLOCK.block().get().defaultBlockState());
        AthanorBlockEntity arc = helper.getBlockEntity(pos, AthanorBlockEntity.class);
        if (arc == null) {
            helper.fail("Expected AthanorBlockEntity at " + pos);
        }
        return arc;
    }

    public static void register(NVTestRegistrar r) {
        r.add("athanor/places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (arc == null) return;
                if (arc.getProgressForGui() != 0) {
                    helper.fail("Fresh ARC should have 0 progress");
                }
                helper.succeed();
            });
        });

        r.add("athanor/smelts_iron_ore", 300, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (arc == null) return;

                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

                helper.runAfterDelay(250, () -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    if (output.isEmpty()) {
                        helper.fail("ARC should have smelted iron ore, output is empty");
                    }
                    if (!output.is(Items.IRON_INGOT)) {
                        helper.fail("Expected iron ingot, got " + output);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("athanor/smelts_from_non_first_input_slot", 300, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (arc == null) return;

                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START + 3, new ItemStack(Items.RAW_IRON));

                helper.runAfterDelay(250, () -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    if (output.isEmpty()) {
                        helper.fail("ARC should smelt input from a non-first slot, output is empty");
                    }
                    if (!output.is(Items.IRON_INGOT)) {
                        helper.fail("Expected iron ingot, got " + output);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("athanor/does_not_craft_without_tool", 150, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (arc == null) return;

                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

                helper.runAfterDelay(110, () -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    if (!output.isEmpty()) {
                        helper.fail("ARC should not smelt without tool, got " + output);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("athanor/does_not_craft_without_input", 150, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (arc == null) return;

                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));

                helper.runAfterDelay(110, () -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    if (!output.isEmpty()) {
                        helper.fail("ARC should not produce output without input, got " + output);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("athanor/consumes_input", 300, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (arc == null) return;

                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

                helper.runAfterDelay(250, () -> {
                    ItemStack input = arc.athanorInv.getStackInSlot(AthanorBlockEntity.INPUT_START);
                    if (!input.isEmpty()) {
                        helper.fail("Input should be consumed after smelting, has " + input);
                    }
                    helper.succeed();
                });
            });
        });
    }
}
