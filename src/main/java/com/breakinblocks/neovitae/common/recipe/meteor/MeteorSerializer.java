package com.breakinblocks.neovitae.common.recipe.meteor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;

import java.util.List;

/**
 * Serializer for meteor recipes.
 * Uses Codec for JSON and StreamCodec for network sync.
 */
public class MeteorSerializer implements RecipeSerializer<MeteorRecipe> {

    public static final MapCodec<MeteorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MeteorRecipe::getInput),
            Codec.INT.fieldOf("syphon").forGetter(MeteorRecipe::getSyphon),
            Codec.FLOAT.fieldOf("explosion").forGetter(MeteorRecipe::getExplosionRadius),
            MeteorLayer.CODEC.listOf().fieldOf("layers").forGetter(MeteorRecipe::getLayerList)
    ).apply(instance, MeteorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MeteorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, MeteorRecipe::getInput,
            ByteBufCodecs.INT, MeteorRecipe::getSyphon,
            ByteBufCodecs.FLOAT, MeteorRecipe::getExplosionRadius,
            MeteorLayer.STREAM_CODEC.apply(ByteBufCodecs.list()), MeteorRecipe::getLayerList,
            MeteorRecipe::new
    );

    @Override
    public MapCodec<MeteorRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MeteorRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
