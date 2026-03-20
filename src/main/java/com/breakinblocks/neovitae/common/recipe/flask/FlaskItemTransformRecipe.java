package com.breakinblocks.neovitae.common.recipe.flask;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffects;
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
 * Recipe that transforms the flask item itself (e.g., regular flask to splash flask).
 * Effects and durability are preserved on the new flask type.
 */
public class FlaskItemTransformRecipe extends FlaskRecipe {

    public static final MapCodec<FlaskItemTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("input").forGetter(FlaskItemTransformRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(FlaskItemTransformRecipe::getOutputItem),
            Codec.INT.fieldOf("syphon").forGetter(FlaskItemTransformRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(FlaskItemTransformRecipe::getTicks),
            Codec.INT.optionalFieldOf("upgradeLevel", 0).forGetter(FlaskItemTransformRecipe::getMinimumTier)
    ).apply(instance, FlaskItemTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlaskItemTransformRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, FlaskItemTransformRecipe::getInput,
            ItemStack.STREAM_CODEC, FlaskItemTransformRecipe::getOutputItem,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getSyphon,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getTicks,
            ByteBufCodecs.INT, FlaskItemTransformRecipe::getMinimumTier,
            FlaskItemTransformRecipe::new
    );

    private final ItemStack outputItem;

    public FlaskItemTransformRecipe(List<Ingredient> input, ItemStack outputItem, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.outputItem = outputItem;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only transform if the item type is different
        return !flaskStack.is(outputItem.getItem());
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = outputItem.copy();

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
        effects.add(EffectHolder.create(MobEffects.MOVEMENT_SPEED, 3600, 0));
        return effects;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.FLASK_ITEM_TRANSFORM_SERIALIZER.get();
    }
}
