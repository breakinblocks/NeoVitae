package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
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
import net.minecraft.world.item.crafting.Recipe;

/**
 * Recipe that transforms the flask item itself (e.g., regular flask to splash flask).
 * Effects and durability are preserved on the new flask type.
 */
public class FlaskItemTransformRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskItemTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(FlaskItemTransformRecipe::getInput),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(FlaskItemTransformRecipe::getOutputTemplate),
            Codec.INT.fieldOf("syphon").forGetter(FlaskItemTransformRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskItemTransformRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskItemTransformRecipe::getMinimumTier)
    ).apply(instance, FlaskItemTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskItemTransformRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskItemTransformRecipe::getInput,
            ItemStackTemplate.STREAM_CODEC, FlaskItemTransformRecipe::getOutputTemplate,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getTicks,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getMinimumTier,
            FlaskItemTransformRecipe::new
    );

    private final ItemStackTemplate outputTemplate;

    public FlaskItemTransformRecipe(List<Ingredient> input, ItemStackTemplate outputTemplate, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.outputTemplate = outputTemplate;
    }

    public ItemStackTemplate getOutputTemplate() {
        return outputTemplate;
    }

    public ItemStack getOutputItem() {
        return outputTemplate.create();
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        return !flaskStack.is(outputTemplate.item().value());
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = outputTemplate.create();

        // Transfer effects from old flask
        if (!flaskEffects.isEmpty()) {
            ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(flaskEffects));
        }

        // Transfer damage value
        copyStack.setDamageValue(flaskStack.getDamageValue());

        return copyStack;
    }

    @Nonnull
    @Override
    public ItemStack getExampleFlask() {
        ItemStack flaskStack = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        List<EffectHolder> exampleEffects = getExampleEffects();
        if (!exampleEffects.isEmpty()) {
            ItemAlchemyFlask.setFlaskEffects(flaskStack, new FlaskEffects(exampleEffects));
        }
        return flaskStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        List<EffectHolder> effects = new ArrayList<>();
        effects.add(EffectHolder.create(MobEffects.SPEED, 3600, 0));
        return effects;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FlaskInput>> getSerializer() {
        return NVRecipes.FLASK_ITEM_TRANSFORM_SERIALIZER.get();
    }
}
