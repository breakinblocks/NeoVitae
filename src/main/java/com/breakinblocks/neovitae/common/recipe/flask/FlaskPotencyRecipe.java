package com.breakinblocks.neovitae.common.recipe.flask;

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
 * Recipe that increases the potency (amplifier) of a specific effect in a flask.
 * Also adjusts the amplifier duration modifier.
 */
public class FlaskPotencyRecipe extends FlaskRecipe {

    private final Holder<MobEffect> targetEffect;
    private final int amplifier;
    private final double ampDurationMod;

    public FlaskPotencyRecipe(List<Ingredient> input, Holder<MobEffect> targetEffect, int amplifier, double ampDurationMod, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.targetEffect = targetEffect;
        this.amplifier = amplifier;
        this.ampDurationMod = ampDurationMod;
    }

    public Holder<MobEffect> getTargetEffect() {
        return targetEffect;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public double getAmpDurationMod() {
        return ampDurationMod;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only increase potency if the effect exists and current amplifier is less than target
        // (or equal amplifier but lower amp duration mod)
        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(targetEffect)) {
                return holder.amplifier() < amplifier ||
                        (holder.amplifier() == amplifier && holder.ampDurationMod() < ampDurationMod);
            }
        }
        return false;
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        for (int i = 0; i < flaskEffects.size(); i++) {
            if (flaskEffects.get(i).matches(targetEffect)) {
                return i + 1;
            }
        }
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();
        List<EffectHolder> newEffects = new ArrayList<>();

        for (EffectHolder holder : flaskEffects) {
            if (holder.matches(targetEffect)) {
                newEffects.add(holder.withPotency(amplifier, ampDurationMod));
            } else {
                newEffects.add(holder);
            }
        }

        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(newEffects));
        return copyStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        List<EffectHolder> effects = new ArrayList<>();
        effects.add(EffectHolder.create(targetEffect, 3600, 0));
        return effects;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.FLASK_POTENCY_SERIALIZER.get();
    }
}
