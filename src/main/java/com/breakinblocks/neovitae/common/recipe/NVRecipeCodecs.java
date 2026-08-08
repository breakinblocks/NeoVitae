package com.breakinblocks.neovitae.common.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NVRecipeCodecs {

    private NVRecipeCodecs() {
    }

    private static final Codec<List<String>> MODERN_ENTRIES = Codec.withAlternative(
            Codec.STRING.listOf(),
            Codec.STRING.flatComapMap(List::of, list -> readOnly())
    );

    public static final Codec<Ingredient> INGREDIENT =
            Codec.withAlternative(Ingredient.CODEC_NONEMPTY, rewriting("item", Ingredient.CODEC_NONEMPTY));

    public static final Codec<FluidIngredient> FLUID_INGREDIENT =
            Codec.withAlternative(FluidIngredient.CODEC_NON_EMPTY, rewriting("fluid", FluidIngredient.CODEC_NON_EMPTY));

    public static final Codec<SizedFluidIngredient> SIZED_FLUID_INGREDIENT = RecordCodecBuilder.create(inst -> inst.group(
            FLUID_INGREDIENT.fieldOf("ingredient").forGetter(SizedFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", FluidType.BUCKET_VOLUME).forGetter(SizedFluidIngredient::amount)
    ).apply(inst, SizedFluidIngredient::new));

    private static <A> DataResult<A> readOnly() {
        return DataResult.error(() -> "Modern 26.1 recipe syntax is accepted on read but never written");
    }

    private static <A> Codec<A> rewriting(String directField, Codec<A> target) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return MODERN_ENTRIES.decode(ops, input).flatMap(decoded -> {
                    List<T> entries = new ArrayList<>();
                    for (String name : decoded.getFirst()) {
                        boolean tag = name.startsWith("#");
                        String field = tag ? "tag" : directField;
                        String value = tag ? name.substring(1) : name;
                        entries.add(ops.createMap(Map.of(ops.createString(field), ops.createString(value))));
                    }
                    if (entries.isEmpty()) {
                        return DataResult.error(() -> "Ingredient must not be empty");
                    }
                    T rewritten = entries.size() == 1 ? entries.getFirst() : ops.createList(entries.stream());
                    return target.parse(ops, rewritten).map(value -> Pair.of(value, decoded.getSecond()));
                });
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return readOnly();
            }
        };
    }
}
