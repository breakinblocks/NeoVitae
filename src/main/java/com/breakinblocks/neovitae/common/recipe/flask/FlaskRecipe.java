package com.breakinblocks.neovitae.common.recipe.flask;

import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all flask recipes processed in the alchemy table.
 * Flask recipes modify the effects stored in an alchemy flask.
 */
public abstract class FlaskRecipe implements Recipe<FlaskInput> {
    public static final String RECIPE_TYPE_NAME = "flask";
    public static final int MAX_INPUTS = 5;

    @Nonnull
    protected final List<Ingredient> input;
    @Nonnegative
    private final int syphon;
    @Nonnegative
    private final int ticks;
    @Nonnegative
    private final int minimumTier;

    public FlaskRecipe(List<Ingredient> input, int syphon, int ticks, int minimumTier) {
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkArgument(syphon >= 0, "syphon cannot be negative.");
        Preconditions.checkArgument(ticks >= 0, "ticks cannot be negative.");
        Preconditions.checkArgument(minimumTier >= 0, "minimumTier cannot be negative.");

        this.input = input;
        this.syphon = syphon;
        this.ticks = ticks;
        this.minimumTier = minimumTier;
    }

    @Nonnull
    public List<Ingredient> getInput() {
        return input;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(input);
        return list;
    }

    public int getSyphon() {
        return syphon;
    }

    public int getTicks() {
        return ticks;
    }

    public int getMinimumTier() {
        return minimumTier;
    }

    /**
     * Check if this recipe can modify the given flask with its current effects.
     */
    public abstract boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects);

    /**
     * Get the priority of this recipe for the given flask effects.
     * Higher priority recipes are preferred when multiple recipes match.
     */
    public abstract int getPriority(List<EffectHolder> flaskEffects);

    /**
     * Get the output flask stack for this recipe.
     */
    @Nonnull
    public abstract ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects);

    /**
     * Get an example flask stack for JEI display purposes.
     */
    @Nonnull
    public ItemStack getExampleFlask() {
        ItemStack flaskStack = new ItemStack(BMItems.ALCHEMY_FLASK.get());
        List<EffectHolder> exampleEffects = getExampleEffects();
        if (!exampleEffects.isEmpty()) {
            ItemAlchemyFlask.setFlaskEffects(flaskStack, new FlaskEffects(exampleEffects));
        }
        return flaskStack;
    }

    /**
     * Get example effects for JEI display purposes.
     */
    public abstract List<EffectHolder> getExampleEffects();

    @Override
    public boolean matches(FlaskInput container, Level level) {
        // First check if ingredients match
        List<ItemStack> inputItems = container.items();
        List<Ingredient> remainingIngredients = new ArrayList<>(input);

        for (ItemStack stack : inputItems) {
            if (stack.isEmpty()) continue;

            boolean matched = false;
            for (int i = 0; i < remainingIngredients.size(); i++) {
                if (remainingIngredients.get(i).test(stack)) {
                    remainingIngredients.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        if (!remainingIngredients.isEmpty()) {
            return false;
        }

        // Then check if the recipe can modify the flask
        return canModifyFlask(container.flaskStack(), container.flaskEffects());
    }

    @Override
    public ItemStack assemble(FlaskInput container, HolderLookup.Provider registries) {
        return getOutput(container.flaskStack(), container.flaskEffects());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        // Return example output for JEI
        return getOutput(getExampleFlask(), getExampleEffects());
    }

    @Override
    public RecipeType<?> getType() {
        return BMRecipes.FLASK_TYPE.get();
    }

    /**
     * Create default example effects with speed, fire resistance, and haste.
     */
    protected static List<EffectHolder> createDefaultExampleEffects() {
        List<EffectHolder> effects = new ArrayList<>();
        effects.add(EffectHolder.create(MobEffects.MOVEMENT_SPEED, 3600, 0));
        effects.add(EffectHolder.create(MobEffects.FIRE_RESISTANCE, 3600, 0));
        effects.add(EffectHolder.create(MobEffects.DIG_SPEED, 3600, 0));
        return effects;
    }
}
