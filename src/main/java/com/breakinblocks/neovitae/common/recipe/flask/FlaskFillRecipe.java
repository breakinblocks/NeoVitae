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
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe that refills an empty/damaged flask with existing effects.
 * This restores durability (uses) to the flask.
 */
public class FlaskFillRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskFillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskFillRecipe::getInput),
            Codec.INT.fieldOf("max").forGetter(FlaskFillRecipe::getMaxEffects),
            Codec.INT.fieldOf("syphon").forGetter(FlaskFillRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskFillRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(FlaskFillRecipe::getMinimumTier)
    ).apply(instance, FlaskFillRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskFillRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskFillRecipe::getInput,
            ByteBufCodecs.INT, FlaskFillRecipe::getMaxEffects,
            ByteBufCodecs.INT, FlaskFillRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskFillRecipe::getTicks,
            ByteBufCodecs.INT, FlaskFillRecipe::getMinimumTier,
            FlaskFillRecipe::new
    );

    private final int maxEffects;

    public FlaskFillRecipe(List<Ingredient> input, int maxEffects, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.maxEffects = maxEffects;
    }

    public int getMaxEffects() {
        return maxEffects;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only fill a flask that has effects
        return !flaskEffects.isEmpty();
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();

        // Limit effects to maxEffects
        List<EffectHolder> limitedEffects = new ArrayList<>();
        for (int i = 0; i < Math.min(flaskEffects.size(), maxEffects); i++) {
            limitedEffects.add(flaskEffects.get(i));
        }

        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(limitedEffects));

        // Reset durability
        copyStack.setDamageValue(0);

        return copyStack;
    }

    @Nonnull
    @Override
    public ItemStack getExampleFlask() {
        ItemStack flaskStack = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        // Show damaged flask with effects for fill recipe
        flaskStack.setDamageValue(8);
        List<EffectHolder> exampleEffects = getExampleEffects();
        if (!exampleEffects.isEmpty()) {
            ItemAlchemyFlask.setFlaskEffects(flaskStack, new FlaskEffects(exampleEffects));
        }
        return flaskStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        return createDefaultExampleEffects();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLASK_FILL_SERIALIZER.get();
    }
}
