package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.block.AthanorBlock;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.util.AthanorOutputHandler;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AthanorTests {

    private static AthanorBlockEntity placeARC(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ATHANOR_BLOCK.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof AthanorBlockEntity arc)) {
            helper.fail("Expected AthanorBlockEntity at " + pos);
            return null;
        }
        return arc;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void arcPlacesAndInitializes(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

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
        AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

            helper.succeedWhen(() -> {
                ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                helper.assertTrue(!(output.isEmpty()), "ARC should have smelted iron ore, output is empty");
                helper.assertTrue(output.is(Items.IRON_INGOT), "Expected iron ingot, got " + output);
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void arcSmeltsFromNonFirstInputSlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START + 3, new ItemStack(Items.RAW_IRON));

            helper.succeedWhen(() -> {
                ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                helper.assertTrue(!(output.isEmpty()), "ARC should smelt input from a non-first slot, output is empty");
                helper.assertTrue(output.is(Items.IRON_INGOT), "Expected iron ingot, got " + output);
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 150)
    public void arcDoesNotCraftWithoutTool(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 150)
    public void arcDoesNotCraftWithoutInput(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void arcConsumesInput(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (arc == null) return;

            arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

            helper.succeedWhen(() -> {
                ItemStack input = arc.athanorInv.getStackInSlot(AthanorBlockEntity.INPUT_START);
                helper.assertTrue(input.isEmpty(), "Input should be consumed after smelting, has " + input);
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void athanorWorkingWithoutSpiritusGlowsRed(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        AthanorBlockEntity arc = placeARC(helper, new BlockPos(3, 1, 2));
        helper.runAfterDelay(1, () -> {
            if (arc == null) return;
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get()));
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON, 8));
            helper.succeedWhen(() -> {
                BlockState state = helper.getBlockState(new BlockPos(3, 1, 2));
                helper.assertTrue(state.getValue(AthanorBlock.LIT), "The Athanor should be lit while smelting");
                helper.assertTrue(state.getValue(AthanorBlock.GLOW) == 0, "Smelting uses no Spiritus, so the grill should glow plain red");
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void athanorGlowMaskListsEverySpiritusTypeInTheRecipe(GameTestHelper helper) {
        var holder = helper.getLevel().getRecipeManager().getAllRecipesFor(NVRecipes.ATHANOR_TYPE.get()).stream()
                .filter(h -> h.id().getPath().equals("athanor/orb/prismatic_spiritus_gem"))
                .findFirst();
        helper.assertTrue(holder.isPresent(), "The prismatic gem recipe should load");
        int mask = AthanorBlockEntity.spiritusMask(holder.get().value());
        helper.assertTrue(mask == (1 << SpiritusType.values().length) - 1,
                "A recipe using all five Spiritus types should light every bit, got " + Integer.toBinaryString(mask));
        helper.assertTrue(AthanorBlock.GLOW.getPossibleValues().contains(mask), "The glow property should hold every mask");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void athanorMaterialRecipesNeverMultiplyTheirOwnProduct(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getAllRecipesFor(NVRecipes.ATHANOR_TYPE.get());
        for (MaterialDefinition mat : MaterialRegistry.getAllMaterials()) {
            if (mat.getSmeltTo() == null) continue;
            ItemStack product = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(mat.getSmeltTo())));
            if (product.isEmpty()) continue;
            Set<ResourceLocation> chain = new HashSet<>();
            for (String stage : mat.getStages()) {
                chain.add(NeoVitae.rl(mat.getItemId(stage)));
            }
            for (var holder : recipes) {
                AthanorRecipe recipe = holder.value();
                if (recipe.getInputs().stream().noneMatch(input -> input.test(product))) continue;
                int yield = 0;
                for (ItemStack stack : recipe.getGuaranteedOutput()) {
                    if (chain.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) yield += stack.getCount();
                }
                if (recipe.isSpiritusBoosted() && yield > 0) yield++;
                helper.assertTrue(yield <= 1, holder.id() + " turns one " + mat.getSmeltTo()
                        + " into " + yield + " " + mat.getName() + " pieces, which smelt back into more than it took");
            }
        }
        helper.succeed();
    }
    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void outputRoomCheckCountsEachFreeSlotOnce(GameTestHelper helper) {
        ItemStack[] slots = new ItemStack[AthanorBlockEntity.NUM_OUTPUTS];
        for (int i = 0; i < slots.length - 1; i++) {
            slots[i] = new ItemStack(Items.ENCHANTED_BOOK);
        }
        slots[slots.length - 1] = ItemStack.EMPTY;
        AthanorOutputHandler handler = new AthanorOutputHandler(slots, 64);

        List<ItemStack> lastDisenchant = List.of(new ItemStack(Items.ENCHANTED_BOOK), new ItemStack(Items.DIAMOND_PICKAXE));
        helper.assertTrue(!handler.canTransferAllItemsToSlots(lastDisenchant, true),
                "Two unstackable outputs must not both fit in one free slot");
        helper.assertTrue(handler.canTransferAllItemsToSlots(List.of(new ItemStack(Items.DIAMOND_PICKAXE)), true),
                "One unstackable output should fit in the one free slot");
        helper.assertTrue(handler.getStackInSlot(slots.length - 1).isEmpty(), "A simulated transfer must not change the slots");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void outputOverflowIsNotDuplicated(GameTestHelper helper) {
        ItemStack[] slots = new ItemStack[AthanorBlockEntity.NUM_OUTPUTS];
        slots[0] = new ItemStack(Items.IRON_INGOT, 60);
        for (int i = 1; i < slots.length; i++) {
            slots[i] = ItemStack.EMPTY;
        }
        AthanorOutputHandler handler = new AthanorOutputHandler(slots, 64);

        helper.assertTrue(handler.canTransferAllItemsToSlots(List.of(new ItemStack(Items.IRON_INGOT, 8)), false),
                "Eight ingots should fit across a partial stack and an empty slot");
        int total = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            total += handler.getStackInSlot(i).getCount();
        }
        helper.assertTrue(total == 68, "Expected 68 ingots after adding 8 to 60, got " + total);
        helper.succeed();
    }
}
