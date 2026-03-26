package com.breakinblocks.neovitae.api.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Abstract base class for Blood Altar recipes.
 * <p>
 * Blood Altar recipes transform items using Life Essence (LP) at various altar tiers.
 * The altar tier determines which recipes are available, and runes affect crafting speed
 * and efficiency.
 * </p>
 *
 * <h2>Component Transfer</h2>
 * <p>
 * Recipes can optionally copy data components from the input item to the output item.
 * This is useful for recipes where the input carries data that should persist, such as:
 * </p>
 * <ul>
 *   <li>Bound items (binding data)</li>
 *   <li>Items with custom enchantments</li>
 *   <li>Items with stored NBT-like data via components</li>
 * </ul>
 * <p>
 * Enable this by setting {@code copyInputComponents: true} in the recipe JSON, or by
 * using the appropriate constructor when creating recipes programmatically.
 * </p>
 *
 * <h2>JSON Format</h2>
 * <pre>{@code
 * {
 *   "type": "neovitae:blood_altar_recipe",
 *   "input": {"item": "minecraft:diamond"},
 *   "output": {"id": "neovitae:weak_blood_orb"},
 *   "minTier": 1,
 *   "bloodNeeded": 2000,
 *   "craftSpeed": 5,
 *   "drainSpeed": 1,
 *   "copyInputComponents": false  // Optional, defaults to false
 * }
 * }</pre>
 */
public abstract class BloodAltarRecipe implements Recipe<BloodAltarInput> {

    public static final String RECIPE_TYPE_NAME = "blood_altar_recipe";

    private final Ingredient input;
    private final ItemStack result;
    private final int minTier;
    private final int totalBlood;
    private final int craftSpeed;
    private final int drainSpeed;
    private final boolean copyInputComponents;

    /**
     * Creates a Blood Altar recipe with component copying disabled (default behavior).
     *
     * @param input       The input ingredient
     * @param result      The output item stack
     * @param minTier     Minimum altar tier required (0-5)
     * @param totalBlood  Total LP required to complete the craft
     * @param craftSpeed  LP consumed per tick while crafting
     * @param drainSpeed  Progress lost per tick when altar runs out of LP
     */
    public BloodAltarRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed) {
        this(input, result, minTier, totalBlood, craftSpeed, drainSpeed, false);
    }

    /**
     * Creates a Blood Altar recipe with explicit component copying control.
     *
     * @param input               The input ingredient
     * @param result              The output item stack
     * @param minTier             Minimum altar tier required (0-5)
     * @param totalBlood          Total LP required to complete the craft
     * @param craftSpeed          LP consumed per tick while crafting
     * @param drainSpeed          Progress lost per tick when altar runs out of LP
     * @param copyInputComponents If true, data components from the input item will be
     *                            copied to the output item. The output's base components
     *                            are preserved, with input components applied as a patch.
     */
    public BloodAltarRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed, boolean copyInputComponents) {
        this.input = input;
        this.result = result;
        this.minTier = minTier;
        this.totalBlood = totalBlood;
        this.craftSpeed = craftSpeed;
        this.drainSpeed = drainSpeed;
        this.copyInputComponents = copyInputComponents;
    }

    /**
     * @return The input ingredient for this recipe
     */
    public Ingredient getInput() {
        return input;
    }

    /**
     * @return LP consumed per tick while the altar is crafting
     */
    public int getCraftSpeed() {
        return craftSpeed;
    }

    /**
     * @return Minimum altar tier required (0 = no altar structure, 1-5 = tiered altars)
     */
    public int getMinTier() {
        return minTier;
    }

    /**
     * @return Total LP required to complete the craft
     */
    public int getTotalBlood() {
        return totalBlood;
    }

    /**
     * @return Progress lost per tick when the altar runs out of LP mid-craft
     */
    public int getDrainSpeed() {
        return drainSpeed;
    }

    /**
     * @return Whether this recipe copies data components from the input item to the output.
     *         When true, components like enchantments, binding data, or custom mod data
     *         will be transferred from the input to the crafted result.
     */
    public boolean shouldCopyInputComponents() {
        return copyInputComponents;
    }

    /**
     * Gets a copy of the base result item. Note: if {@link #shouldCopyInputComponents()}
     * returns true, the actual output may have additional components from the input.
     * Use {@link #assemble(BloodAltarInput, HolderLookup.Provider)} for the actual output.
     *
     * @return A copy of the result item stack
     */
    public ItemStack getResult() {
        return result.copy();
    }

    /**
     * Checks if the given input matches this recipe.
     *
     * @param recipeInput The altar input containing the item and current tier
     * @param level       The current level
     * @return true if the altar tier is sufficient and the input item matches the ingredient
     */
    @Override
    public boolean matches(BloodAltarInput recipeInput, Level level) {
        return minTier <= recipeInput.getAltarTier() && input.test(recipeInput.getItem(0));
    }

    /**
     * Assembles the output item stack for this recipe.
     * <p>
     * If {@link #shouldCopyInputComponents()} returns true, data components from the
     * input item will be copied to the output as a patch. This preserves the output's
     * base components while adding/overwriting with input components.
     * </p>
     *
     * @param recipeInput The altar input containing the item being crafted
     * @param registries  Registry access for component serialization
     * @return The crafted output item, potentially with copied components
     */
    @Override
    public ItemStack assemble(BloodAltarInput recipeInput, HolderLookup.Provider registries) {
        ItemStack output = result.copy();
        if (copyInputComponents) {
            ItemStack inputStack = recipeInput.getItem(0);
            // Apply input components as a patch to preserve output's base components
            DataComponentPatch inputPatch = inputStack.getComponentsPatch();
            output.applyComponents(inputPatch);
        }
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract RecipeType<?> getType();
}
