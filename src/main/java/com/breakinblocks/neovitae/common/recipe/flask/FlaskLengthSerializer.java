package com.breakinblocks.neovitae.common.recipe.flask;

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

/**
 * Serializer for flask length recipes (increasing effect duration modifier).
 */
public class FlaskLengthSerializer implements RecipeSerializer<FlaskLengthRecipe> {

    public static final MapCodec<FlaskLengthRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskLengthRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskLengthRecipe::getTargetEffect),
            Codec.DOUBLE.fieldOf("lengthDurationMod").forGetter(FlaskLengthRecipe::getLengthDurationMod),
            Codec.INT.fieldOf("syphon").forGetter(FlaskLengthRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskLengthRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskLengthRecipe::getMinimumTier)
    ).apply(instance, FlaskLengthRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<MobEffect>> MOB_EFFECT_CODEC =
            ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key());

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskLengthRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskLengthRecipe::getInput,
            MOB_EFFECT_CODEC, FlaskLengthRecipe::getTargetEffect,
            ByteBufCodecs.DOUBLE, FlaskLengthRecipe::getLengthDurationMod,
            ByteBufCodecs.INT, FlaskLengthRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskLengthRecipe::getTicks,
            ByteBufCodecs.INT, FlaskLengthRecipe::getMinimumTier,
            FlaskLengthRecipe::new
    );

    @Override
    public MapCodec<FlaskLengthRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskLengthRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
