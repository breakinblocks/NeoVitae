package com.breakinblocks.neovitae.common.recipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared utility codecs for recipe serialization.
 * Eliminates duplicate List encoding/decoding across recipe serializers.
 */
public final class RecipeSerializerUtils {

    private RecipeSerializerUtils() {}

    /**
     * StreamCodec for List<Ingredient> that handles encoding/decoding automatically.
     * Replaces manual loops in flask recipe serializers.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_CODEC =
            ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC);
}
