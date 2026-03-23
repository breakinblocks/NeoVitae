package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyTableBlockEntity;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AlchemyTableTests {

    private static AlchemyTableBlockEntity placeTable(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ALCHEMY_TABLE.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof AlchemyTableBlockEntity table)) {
            helper.fail("Expected AlchemyTableBlockEntity at " + pos);
            return null;
        }
        return table;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tablePlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyTableBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (table == null) return;
            if (table.burnTime != 0) {
                helper.fail("Fresh table should have 0 burn time, got " + table.burnTime);
            }
            if (table.isSlave()) {
                helper.fail("Standalone table should not be slave");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableDoesNotCraftWithoutOrb(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyTableBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            // Place random items but no orb
            table.inv.setStackInSlot(0, new ItemStack(Items.SUGAR));
            table.inv.setStackInSlot(1, new ItemStack(Items.WATER_BUCKET));

            helper.runAfterDelay(40, () -> {
                ItemStack output = table.inv.getStackInSlot(AlchemyTableBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("Table should not craft without orb, got " + output);
                }
                if (table.burnTime > 0) {
                    helper.fail("Table should not progress without valid recipe/orb");
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableDoesNotCraftWithInvalidRecipe(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyTableBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            // Random items that don't form any recipe
            table.inv.setStackInSlot(0, new ItemStack(Items.DIRT));
            table.inv.setStackInSlot(1, new ItemStack(Items.COBBLESTONE));
            table.inv.setStackInSlot(2, new ItemStack(Items.GRAVEL));

            helper.runAfterDelay(40, () -> {
                if (table.burnTime > 0) {
                    helper.fail("Table should not progress with invalid recipe, burnTime=" + table.burnTime);
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableRejectsNonOrbInOrbSlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyTableBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            boolean valid = table.inv.isItemValid(AlchemyTableBlockEntity.ORB_SLOT, new ItemStack(Items.DIAMOND));
            if (valid) {
                helper.fail("Orb slot should reject non-orb items");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableRejectsItemsInOutputSlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyTableBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            boolean valid = table.inv.isItemValid(AlchemyTableBlockEntity.OUTPUT_SLOT, new ItemStack(Items.DIAMOND));
            if (valid) {
                helper.fail("Output slot should reject items");
            }
            helper.succeed();
        });
    }
}
