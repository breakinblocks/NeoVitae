package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Recipe that transforms one or more effects into different effects.
 * For example, converting fire resistance into levitation.
 */
public class FlaskEffectTransformRecipe extends FlaskRecipe {

    private static final Codec<Pair<Holder<MobEffect>, Integer>> EFFECT_PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(Pair::getFirst),
            Codec.INT.fieldOf("duration").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    public static final MapCodec<FlaskEffectTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(FlaskEffectTransformRecipe::getInput),
            EFFECT_PAIR_CODEC.listOf().fieldOf("outputEffects").forGetter(FlaskEffectTransformRecipe::getOutputEffects),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().listOf().fieldOf("inputEffects").forGetter(FlaskEffectTransformRecipe::getInputEffects),
            Codec.INT.fieldOf("syphon").forGetter(FlaskEffectTransformRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskEffectTransformRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskEffectTransformRecipe::getMinimumTier)
    ).apply(instance, FlaskEffectTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskEffectTransformRecipe> STREAM_CODEC = StreamCodec.of(
            FlaskEffectTransformRecipe::toNetwork,
            FlaskEffectTransformRecipe::fromNetwork
    );

    private static FlaskEffectTransformRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        // Read inputs
        List<Ingredient> inputs = RecipeSerializerUtils.INGREDIENT_LIST_CODEC.decode(buffer);

        // Read output effects
        int outputSize = buffer.readInt();
        List<Pair<Holder<MobEffect>, Integer>> outputEffects = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            Holder<MobEffect> effect = ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).decode(buffer);
            int duration = buffer.readInt();
            outputEffects.add(Pair.of(effect, duration));
        }

        // Read input effects
        int inputEffectSize = buffer.readInt();
        List<Holder<MobEffect>> inputEffects = new ArrayList<>();
        for (int i = 0; i < inputEffectSize; i++) {
            inputEffects.add(ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).decode(buffer));
        }

        int syphon = buffer.readInt();
        int ticks = buffer.readInt();
        int minimumTier = buffer.readInt();
        return new FlaskEffectTransformRecipe(inputs, outputEffects, inputEffects, syphon, ticks, minimumTier);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, FlaskEffectTransformRecipe recipe) {
        // Write inputs
        RecipeSerializerUtils.INGREDIENT_LIST_CODEC.encode(buffer, recipe.getInput());

        // Write output effects
        buffer.writeInt(recipe.getOutputEffects().size());
        for (Pair<Holder<MobEffect>, Integer> pair : recipe.getOutputEffects()) {
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).encode(buffer, pair.getFirst());
            buffer.writeInt(pair.getSecond());
        }

        // Write input effects
        buffer.writeInt(recipe.getInputEffects().size());
        for (Holder<MobEffect> effect : recipe.getInputEffects()) {
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).encode(buffer, effect);
        }

        buffer.writeInt(recipe.getSyphon());
        buffer.writeInt(recipe.getTicks());
        buffer.writeInt(recipe.getMinimumTier());
    }

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

        for (Pair<Holder<MobEffect>, Integer> output : outputEffects) {
            boolean found = false;
            for (int i = 0; i < workingEffects.size(); i++) {
                EffectHolder holder = workingEffects.get(i);
                if (holder.matches(output.getFirst())) {
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
    public RecipeSerializer<? extends Recipe<FlaskInput>> getSerializer() {
        return NVRecipes.FLASK_EFFECT_TRANSFORM_SERIALIZER.get();
    }
}
