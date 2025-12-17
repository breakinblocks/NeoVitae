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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializer for flask effect transform recipes (converting effects into other effects).
 */
public class FlaskEffectTransformSerializer implements RecipeSerializer<FlaskEffectTransformRecipe> {

    // Codec for effect + duration pair
    private static final Codec<Pair<Holder<MobEffect>, Integer>> EFFECT_PAIR_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(Pair::getFirst),
            Codec.INT.fieldOf("duration").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    public static final MapCodec<FlaskEffectTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskEffectTransformRecipe::getInput),
            EFFECT_PAIR_CODEC.listOf().fieldOf("outputEffects").forGetter(FlaskEffectTransformRecipe::getOutputEffects),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().listOf().fieldOf("inputEffects").forGetter(FlaskEffectTransformRecipe::getInputEffects),
            Codec.INT.fieldOf("syphon").forGetter(FlaskEffectTransformRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskEffectTransformRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskEffectTransformRecipe::getMinimumTier)
    ).apply(instance, FlaskEffectTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskEffectTransformRecipe> STREAM_CODEC = StreamCodec.of(
            FlaskEffectTransformSerializer::toNetwork,
            FlaskEffectTransformSerializer::fromNetwork
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

    @Override
    public MapCodec<FlaskEffectTransformRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskEffectTransformRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
