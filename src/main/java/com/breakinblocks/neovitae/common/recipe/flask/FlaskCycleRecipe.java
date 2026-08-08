package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipeCodecs;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Recipe that cycles the order of effects in a flask.
 * This moves the first effect(s) to the end of the list.
 */
public class FlaskCycleRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskCycleRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NVRecipeCodecs.INGREDIENT.listOf().fieldOf("input").forGetter(FlaskCycleRecipe::getInput),
            Codec.INT.fieldOf("count").forGetter(FlaskCycleRecipe::getNumCycles),
            Codec.INT.fieldOf("syphon").forGetter(FlaskCycleRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskCycleRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(FlaskCycleRecipe::getMinimumTier)
    ).apply(instance, FlaskCycleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskCycleRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskCycleRecipe::getInput,
            ByteBufCodecs.INT, FlaskCycleRecipe::getNumCycles,
            ByteBufCodecs.INT, FlaskCycleRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskCycleRecipe::getTicks,
            ByteBufCodecs.INT, FlaskCycleRecipe::getMinimumTier,
            FlaskCycleRecipe::new
    );

    private final int numCycles;

    public FlaskCycleRecipe(List<Ingredient> input, int numCycles, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.numCycles = numCycles;
    }

    public int getNumCycles() {
        return numCycles;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Need at least 2 effects to cycle
        return flaskEffects.size() >= 2;
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();
        FlaskEffects effects = ItemAlchemyFlask.getFlaskEffects(flaskStack);
        FlaskEffects cycledEffects = effects.cycled(numCycles);
        ItemAlchemyFlask.setFlaskEffects(copyStack, cycledEffects);
        return copyStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        return createDefaultExampleEffects();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLASK_CYCLE_SERIALIZER.get();
    }
}
