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

import java.util.List;

/**
 * Serializer for flask potency recipes (increasing effect amplifier).
 */
public class FlaskPotencySerializer implements RecipeSerializer<FlaskPotencyRecipe> {

    public static final MapCodec<FlaskPotencyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskPotencyRecipe::getInput),
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(FlaskPotencyRecipe::getTargetEffect),
            Codec.INT.fieldOf("amplifier").forGetter(FlaskPotencyRecipe::getAmplifier),
            Codec.DOUBLE.fieldOf("ampDurationMod").forGetter(FlaskPotencyRecipe::getAmpDurationMod),
            Codec.INT.fieldOf("syphon").forGetter(FlaskPotencyRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskPotencyRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskPotencyRecipe::getMinimumTier)
    ).apply(instance, FlaskPotencyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskPotencyRecipe> STREAM_CODEC = StreamCodec.of(
            FlaskPotencySerializer::toNetwork,
            FlaskPotencySerializer::fromNetwork
    );

    private static FlaskPotencyRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        List<Ingredient> inputs = RecipeSerializerUtils.INGREDIENT_LIST_CODEC.decode(buffer);
        Holder<MobEffect> effect = ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).decode(buffer);
        int amplifier = buffer.readInt();
        double ampDurationMod = buffer.readDouble();
        int syphon = buffer.readInt();
        int ticks = buffer.readInt();
        int minimumTier = buffer.readInt();
        return new FlaskPotencyRecipe(inputs, effect, amplifier, ampDurationMod, syphon, ticks, minimumTier);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, FlaskPotencyRecipe recipe) {
        RecipeSerializerUtils.INGREDIENT_LIST_CODEC.encode(buffer, recipe.getInput());
        ByteBufCodecs.holderRegistry(BuiltInRegistries.MOB_EFFECT.key()).encode(buffer, recipe.getTargetEffect());
        buffer.writeInt(recipe.getAmplifier());
        buffer.writeDouble(recipe.getAmpDurationMod());
        buffer.writeInt(recipe.getSyphon());
        buffer.writeInt(recipe.getTicks());
        buffer.writeInt(recipe.getMinimumTier());
    }

    @Override
    public MapCodec<FlaskPotencyRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FlaskPotencyRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
