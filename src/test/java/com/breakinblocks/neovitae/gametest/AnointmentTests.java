package com.breakinblocks.neovitae.gametest;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.AnointmentApplyRecipe;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class AnointmentTests {

    private AnointmentTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("anointment_applies_via_smithing", 20, helper -> {
            ItemStack anointment = new ItemStack(NVItems.SILK_TOUCH_ANOINTMENT.get());
            ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
            SmithingRecipeInput input = new SmithingRecipeInput(anointment, tool, ItemStack.EMPTY);

            AnointmentApplyRecipe recipe = new AnointmentApplyRecipe();
            if (!recipe.matches(input, helper.getLevel())) {
                helper.fail("Anointment smithing recipe should match anointment + pickaxe");
                return;
            }

            ItemStack result = recipe.assemble(input);
            if (result.isEmpty()) {
                helper.fail("Assembling the anointment smithing recipe produced nothing");
                return;
            }
            if (result.get(NVDataComponents.ANOINTMENT_HOLDER.get()) == null) {
                helper.fail("Result item has no anointment holder after smithing");
                return;
            }

            SmithingRecipeInput noTemplate = new SmithingRecipeInput(new ItemStack(Items.PAPER), tool, ItemStack.EMPTY);
            if (recipe.matches(noTemplate, helper.getLevel())) {
                helper.fail("Recipe should not match without an anointment in the template slot");
                return;
            }

            SmithingRecipeInput badBase = new SmithingRecipeInput(anointment, new ItemStack(Items.APPLE), ItemStack.EMPTY);
            if (recipe.matches(badBase, helper.getLevel())) {
                helper.fail("Recipe should not match with a non-tool base");
                return;
            }

            helper.succeed();
        });
    }
}
