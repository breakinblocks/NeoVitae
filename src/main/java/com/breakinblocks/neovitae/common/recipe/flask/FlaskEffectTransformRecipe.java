package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe that transforms one or more effects into different effects.
 * For example, converting fire resistance into levitation.
 */
public class FlaskEffectTransformRecipe extends FlaskRecipe {

    private final List<Pair<Holder<MobEffect>, Integer>> outputEffects; // Effect -> base duration
    private final List<Holder<MobEffect>> inputEffects;

    public FlaskEffectTransformRecipe(List<Ingredient> input, List<Pair<Holder<MobEffect>, Integer>> outputEffects, List<Holder<MobEffect>> inputEffects, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.outputEffects = outputEffects;
        this.inputEffects = inputEffects;
    }

    public List<Pair<Holder<MobEffect>, Integer>> getOutputEffects() {
        return outputEffects;
    }

    public List<Holder<MobEffect>> getInputEffects() {
        return inputEffects;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        if (flaskEffects.size() < inputEffects.size()) {
            return false;
        }

        // Count how many output effects already exist with sufficient duration
        int duplicateCount = getDuplicateEffectCount(flaskEffects);
        if (duplicateCount >= outputEffects.size()) {
            return false;
        }

        // Check all input effects are present
        List<Holder<MobEffect>> remainingInputs = new ArrayList<>(inputEffects);

        for (EffectHolder holder : flaskEffects) {
            for (int i = 0; i < remainingInputs.size(); i++) {
                if (holder.matches(remainingInputs.get(i))) {
                    remainingInputs.remove(i);
                    break;
                }
            }
        }

        return remainingInputs.isEmpty();
    }

    private int getDuplicateEffectCount(List<EffectHolder> flaskEffects) {
        int count = 0;
        List<Pair<Holder<MobEffect>, Integer>> remainingOutputs = new ArrayList<>(outputEffects);

        for (EffectHolder holder : flaskEffects) {
            for (int i = 0; i < remainingOutputs.size(); i++) {
                Pair<Holder<MobEffect>, Integer> output = remainingOutputs.get(i);
                if (holder.matches(output.getFirst()) && holder.baseDuration() >= output.getSecond()) {
                    remainingOutputs.remove(i);
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        int priority = 0;
        for (int i = 0; i < flaskEffects.size(); i++) {
            EffectHolder holder = flaskEffects.get(i);
            for (Holder<MobEffect> inputEffect : inputEffects) {
                if (holder.matches(inputEffect)) {
                    priority += i + 1;
                    break;
                }
            }
        }
        return priority;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();

        boolean savePotencies = outputEffects.size() == 1 && inputEffects.size() == 1;
        int savedAmplifier = 0;
        double savedAmpMod = 1.0;
        double savedLengthMod = 1.0;

        // Remove input effects
        List<EffectHolder> workingEffects = new ArrayList<>(flaskEffects);

        for (Holder<MobEffect> inputEffect : inputEffects) {
            for (int i = 0; i < workingEffects.size(); i++) {
                EffectHolder holder = workingEffects.get(i);
                if (holder.matches(inputEffect)) {
                    if (savePotencies) {
                        savedAmplifier = holder.amplifier();
                        savedAmpMod = holder.ampDurationMod();
                        savedLengthMod = holder.lengthDurationMod();
                    }
                    workingEffects.remove(i);
                    break;
                }
            }
        }

        // Check if outputs already exist and update them, otherwise add new
        for (Pair<Holder<MobEffect>, Integer> output : outputEffects) {
            boolean found = false;
            for (int i = 0; i < workingEffects.size(); i++) {
                EffectHolder holder = workingEffects.get(i);
                if (holder.matches(output.getFirst())) {
                    // Update duration if new is higher
                    if (holder.baseDuration() < output.getSecond()) {
                        workingEffects.set(i, holder.withBaseDuration(output.getSecond()));
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                workingEffects.add(new EffectHolder(output.getFirst(), output.getSecond(), savedAmplifier, savedAmpMod, savedLengthMod));
            }
        }

        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(workingEffects));
        return copyStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        List<EffectHolder> effects = new ArrayList<>();
        for (Holder<MobEffect> inputEffect : inputEffects) {
            effects.add(EffectHolder.create(inputEffect, 3600, 0));
        }
        return effects;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.FLASK_EFFECT_TRANSFORM_SERIALIZER.get();
    }
}
