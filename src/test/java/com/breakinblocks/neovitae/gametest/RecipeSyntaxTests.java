package com.breakinblocks.neovitae.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class RecipeSyntaxTests {

    private static final String MODERN = """
            {
              "tool": "#neovitae:athanor_tool/hydration",
              "inputs": ["#c:sands"],
              "guaranteed_outputs": [{"id": "minecraft:clay_ball"}],
              "chance_outputs": [{"item": {"id": "minecraft:clay_ball"}, "chance": 0.5}],
              "input_fluid": {"ingredient": "minecraft:water", "amount": 200}
            }
            """;

    private static final String LEGACY = """
            {
              "tool": {"tag": "neovitae:athanor_tool/hydration"},
              "inputs": [{"tag": "c:sands"}],
              "guaranteed_outputs": [{"id": "minecraft:clay_ball", "count": 1}],
              "chance_outputs": [{"item": {"id": "minecraft:clay_ball", "count": 1}, "chance": 0.5}],
              "input_fluid": {"ingredient": {"fluid": "minecraft:water"}, "amount": 200}
            }
            """;

    private static final String OUTPUTS_OMITTED = """
            {
              "tool": "#neovitae:athanor_tool/hydration",
              "inputs": ["minecraft:sand"],
              "guaranteed_outputs": [{"id": "minecraft:clay_ball"}]
            }
            """;

    private static final String LEGACY_ITEM_FORM = """
            {
              "tool": {"item": "minecraft:stick"},
              "inputs": [{"item": "minecraft:sand"}, {"tag": "c:sands"}],
              "guaranteed_outputs": [{"id": "minecraft:clay_ball"}]
            }
            """;

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void athanorAcceptsBothIngredientForms(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            DynamicOps<JsonElement> ops = ops(helper);

            AthanorRecipe modern = parse(helper, ops, MODERN, "modern");
            if (modern == null) return;
            AthanorRecipe legacy = parse(helper, ops, LEGACY, "legacy");
            if (legacy == null) return;

            if (!sameIngredient(modern.getTool(), legacy.getTool())) {
                helper.fail("tool differs between modern and legacy syntax");
                return;
            }
            if (modern.getInputs().size() != 1 || legacy.getInputs().size() != 1
                    || !sameIngredient(modern.getInputs().getFirst(), legacy.getInputs().getFirst())) {
                helper.fail("inputs differ between modern and legacy syntax");
                return;
            }
            if (!ItemStack.isSameItemSameComponents(
                    modern.getGuaranteedOutput().getFirst(), legacy.getGuaranteedOutput().getFirst())) {
                helper.fail("guaranteed_outputs differ between modern and legacy syntax");
                return;
            }
            if (modern.getChanceOutput().size() != 1 || legacy.getChanceOutput().size() != 1) {
                helper.fail("chance_outputs did not round-trip");
                return;
            }
            if (modern.getInputFluid().isEmpty() || legacy.getInputFluid().isEmpty()) {
                helper.fail("input_fluid missing after parse");
                return;
            }
            if (modern.getInputFluid().get().amount() != 200 || legacy.getInputFluid().get().amount() != 200) {
                helper.fail("input_fluid amount wrong");
                return;
            }
            FluidStack water = new FluidStack(Fluids.WATER, 1000);
            if (!modern.getInputFluid().get().test(water) || !legacy.getInputFluid().get().test(water)) {
                helper.fail("input_fluid does not match water");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void athanorOutputsAreOptional(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            AthanorRecipe recipe = parse(helper, ops(helper), OUTPUTS_OMITTED, "omitted chance_outputs");
            if (recipe == null) return;
            if (!recipe.getChanceOutput().isEmpty()) {
                helper.fail("omitted chance_outputs should default to empty");
                return;
            }
            if (recipe.getGuaranteedOutput().size() != 1) {
                helper.fail("guaranteed_outputs did not parse");
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void athanorAcceptsLegacyItemForm(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            AthanorRecipe recipe = parse(helper, ops(helper), LEGACY_ITEM_FORM, "legacy item form");
            if (recipe == null) return;
            if (!recipe.getTool().test(new ItemStack(Items.STICK))) {
                helper.fail("legacy {\"item\":...} tool did not match stick");
                return;
            }
            if (recipe.getInputs().size() != 2) {
                helper.fail("expected 2 inputs, got " + recipe.getInputs().size());
                return;
            }
            if (!recipe.getInputs().getFirst().test(new ItemStack(Items.SAND))) {
                helper.fail("legacy {\"item\":...} input did not match sand");
                return;
            }
            if (!recipe.getInputs().get(1).test(new ItemStack(Items.RED_SAND))) {
                helper.fail("legacy {\"tag\":...} input did not match red sand");
                return;
            }
            helper.succeed();
        });
    }

    private static DynamicOps<JsonElement> ops(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        return registries.createSerializationContext(JsonOps.INSTANCE);
    }

    private static AthanorRecipe parse(GameTestHelper helper, DynamicOps<JsonElement> ops, String json, String label) {
        DataResult<AthanorRecipe> result = AthanorRecipe.CODEC.codec().parse(ops, JsonParser.parseString(json));
        if (result.isError()) {
            helper.fail("Failed to parse " + label + " syntax: " + result.error().orElseThrow().message());
            return null;
        }
        return result.getOrThrow();
    }

    private static boolean sameIngredient(Ingredient a, Ingredient b) {
        return a.getItems().length == b.getItems().length
                && ItemStack.isSameItem(a.getItems()[0], b.getItems()[0]);
    }
}
