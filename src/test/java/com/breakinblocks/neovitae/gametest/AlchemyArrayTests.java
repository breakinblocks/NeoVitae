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
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AlchemyArrayTests {

    private static AlchemyArrayBlockEntity placeArray(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof AlchemyArrayBlockEntity array)) {
            helper.fail("Expected AlchemyArrayBlockEntity at " + pos);
            return null;
        }
        return array;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arrayPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyArrayBlockEntity array = placeArray(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (array == null) return;
            if (array.isActive) {
                helper.fail("Fresh array should not be active");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arrayDetectsRecipe(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyArrayBlockEntity array = placeArray(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (array == null) return;
            array.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
            array.inv.setStackInSlot(1, new ItemStack(NVItems.TABULA_RASA.get()));
            array.attemptCraft();

            helper.runAfterDelay(10, () -> {
                if (!array.isActive) {
                    helper.fail("Array should activate with valid recipe (redstone + blank slate)");
                    return;
                }
                if (array.arrayEffect == null) {
                    helper.fail("Array should have a crafting effect assigned");
                    return;
                }
                helper.succeed();
            });
        });
    }

    // Crafting effect has 200 tick limit
    @GameTest(template = "empty_5x5x7", timeoutTicks = 260)
    public void arrayCraftsDivinationSigil(GameTestHelper helper) {
        BlockPos arrayPos = new BlockPos(3, 1, 2);
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyArrayBlockEntity array = placeArray(helper, arrayPos);

        helper.runAfterDelay(1, () -> {
            if (array == null) return;
            array.inv.setStackInSlot(0, new ItemStack(Items.REDSTONE));
            array.inv.setStackInSlot(1, new ItemStack(NVItems.TABULA_RASA.get()));
            array.attemptCraft();
            helper.runAfterDelay(220, () -> {
                BlockPos absPos = helper.absolutePos(arrayPos);
                if (!helper.getLevel().getBlockState(absPos).isAir()) {
                    helper.fail("Array should be replaced with air after crafting completes (counter=" + array.activeCounter + ")");
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arrayDoesNotActivateWithoutIngredients(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyArrayBlockEntity array = placeArray(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(30, () -> {
            if (array == null) return;
            if (array.isActive) {
                helper.fail("Empty array should not be active");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arrayDoesNotActivateWithInvalidRecipe(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AlchemyArrayBlockEntity array = placeArray(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (array == null) return;
            array.inv.setStackInSlot(0, new ItemStack(Items.DIRT));
            array.inv.setStackInSlot(1, new ItemStack(Items.COBBLESTONE));

            helper.runAfterDelay(30, () -> {
                if (array.isActive) {
                    helper.fail("Array should not activate with invalid recipe");
                }
                helper.succeed();
            });
        });
    }
}
