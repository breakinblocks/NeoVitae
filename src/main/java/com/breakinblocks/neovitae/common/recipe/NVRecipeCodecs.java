package com.breakinblocks.neovitae.common.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public final class NVRecipeCodecs {

    private NVRecipeCodecs() {
    }

    private static final Codec<String> LEGACY_ITEM_ENTRY = Codec.withAlternative(
            Identifier.CODEC.fieldOf("item").codec().flatComapMap(Identifier::toString, name -> readOnly()),
            Identifier.CODEC.fieldOf("tag").codec().flatComapMap(tag -> "#" + tag, name -> readOnly())
    );

    private static final Codec<String> LEGACY_FLUID_ENTRY = Codec.withAlternative(
            Identifier.CODEC.fieldOf("fluid").codec().flatComapMap(Identifier::toString, name -> readOnly()),
            Identifier.CODEC.fieldOf("tag").codec().flatComapMap(tag -> "#" + tag, name -> readOnly())
    );

    public static final Codec<Ingredient> INGREDIENT =
            Codec.withAlternative(Ingredient.CODEC, rewriting(LEGACY_ITEM_ENTRY, Ingredient.CODEC));

    public static final Codec<FluidIngredient> FLUID_INGREDIENT =
            Codec.withAlternative(FluidIngredient.CODEC, rewriting(LEGACY_FLUID_ENTRY, FluidIngredient.CODEC));

    public static final Codec<SizedFluidIngredient> SIZED_FLUID_INGREDIENT = RecordCodecBuilder.create(inst -> inst.group(
            FLUID_INGREDIENT.fieldOf("ingredient").forGetter(SizedFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", FluidType.BUCKET_VOLUME).forGetter(SizedFluidIngredient::amount)
    ).apply(inst, SizedFluidIngredient::new));

    private static <A> DataResult<A> readOnly() {
        return DataResult.error(() -> "Legacy 1.21.1 recipe syntax is accepted on read but never written");
    }

    private static <A> Codec<A> rewriting(Codec<String> entry, Codec<A> target) {
        Codec<List<String>> entries = Codec.withAlternative(
                entry.listOf(),
                entry.flatComapMap(List::of, list -> readOnly())
        );
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return entries.decode(ops, input).flatMap(decoded -> {
                    List<String> names = decoded.getFirst();
                    T rewritten = names.size() == 1
                            ? ops.createString(names.getFirst())
                            : ops.createList(names.stream().map(ops::createString));
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
