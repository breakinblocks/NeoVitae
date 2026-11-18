package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.AthanorOutputHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

                helper.succeedWhen(() -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(!(output.isEmpty()), "ARC should have smelted iron ore, output is empty");
                    helper.assertTrue(output.is(Items.IRON_INGOT), "Expected iron ingot, got " + output);
                });
            });
        });

        r.add("athanor/working_without_spiritus_glows_red", 300, helper -> {
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
        });

        r.add("athanor/glow_mask_lists_every_spiritus_type_in_the_recipe", 20, helper -> {
            var holder = helper.getLevel().recipeAccess().recipeMap().byType(NVRecipes.ATHANOR_TYPE.get()).stream()
                    .filter(h -> h.id().identifier().getPath().equals("athanor/orb/prismatic_spiritus_gem"))
                    .findFirst();
            helper.assertTrue(holder.isPresent(), "The prismatic gem recipe should load");
            int mask = AthanorBlockEntity.spiritusMask(holder.get().value());
            helper.assertTrue(mask == (1 << SpiritusType.values().length) - 1,
                    "A recipe using all five Spiritus types should light every bit, got " + Integer.toBinaryString(mask));
            helper.assertTrue(AthanorBlock.GLOW.getPossibleValues().contains(mask), "The glow property should hold every mask");
            helper.succeed();
        });

        r.add("athanor/material_recipes_never_multiply_their_own_product", 20, helper -> {
            var recipes = helper.getLevel().recipeAccess().recipeMap().byType(NVRecipes.ATHANOR_TYPE.get());
            for (MaterialDefinition mat : MaterialRegistry.getAllMaterials()) {
                if (mat.getSmeltTo() == null) continue;
                ItemStack product = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(mat.getSmeltTo())));
                if (product.isEmpty()) continue;
                Set<Identifier> chain = new HashSet<>();
                for (String stage : mat.getStages()) {
                    chain.add(NeoVitae.rl(mat.getItemId(stage)));
                }
                for (var holder : recipes) {
                    AthanorRecipe recipe = holder.value();
                    if (recipe.getInputs().stream().noneMatch(input -> input.test(product))) continue;
                    int yield = 0;
                    for (ItemStackTemplate output : recipe.getGuaranteedOutput()) {
                        ItemStack stack = output.create();
                        if (chain.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) yield += stack.getCount();
                    }
                    if (recipe.isSpiritusBoosted() && yield > 0) yield++;
                    helper.assertTrue(yield <= 1, holder.id().identifier() + " turns one " + mat.getSmeltTo()
                            + " into " + yield + " " + mat.getName() + " pieces, which smelt back into more than it took");
                }
            }
            helper.succeed();
        });

        r.add("athanor/smelts_from_non_first_input_slot", 300, helper -> {
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

                helper.succeedWhen(() -> {
                    ItemStack input = arc.athanorInv.getStackInSlot(AthanorBlockEntity.INPUT_START);
                    helper.assertTrue(input.isEmpty(), "Input should be consumed after smelting, has " + input);
                });
            });
        });

        r.add("athanor/output_room_check_counts_each_free_slot_once", 20, helper -> {
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
        });

        r.add("athanor/output_overflow_is_not_duplicated", 20, helper -> {
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
        });
    }
}
