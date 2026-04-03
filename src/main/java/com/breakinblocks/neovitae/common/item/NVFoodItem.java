package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A food item that applies effects on consumption with optional probability.
 * Supports drink animation override for potions/bottles.
 */
public class NVFoodItem extends Item {

    private final List<FoodEffect> effects;
    private final UseAnim useAnim;
    private final int useDuration;

    private NVFoodItem(Properties properties, List<FoodEffect> effects, UseAnim useAnim, int useDuration) {
        super(properties);
        this.effects = effects;
        this.useAnim = useAnim;
        this.useDuration = useDuration;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            for (FoodEffect effect : effects) {
                if (effect.probability >= 1.0f || level.random.nextFloat() < effect.probability) {
                    entity.addEffect(new MobEffectInstance(effect.effect.get(), effect.duration, effect.amplifier));
                }
            }
        }
        return result;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return useAnim;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useDuration;
    }

    public static Builder builder(int nutrition, float saturation) {
        return new Builder(nutrition, saturation);
    }

    private record FoodEffect(Supplier<net.minecraft.core.Holder<MobEffect>> effect, int duration, int amplifier, float probability) {}

    public static class Builder {
        private final FoodProperties.Builder foodBuilder;
        private final List<FoodEffect> effects = new ArrayList<>();
        private Item.Properties properties = new Item.Properties();
        private UseAnim useAnim = UseAnim.EAT;
        private int useDuration = 32;

        private Builder(int nutrition, float saturation) {
            this.foodBuilder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation);
        }

        public Builder alwaysEdible() {
            foodBuilder.alwaysEdible();
            return this;
        }

        public Builder stacksTo(int max) {
            properties.stacksTo(max);
            return this;
        }

        public Builder drinkable() {
            this.useAnim = UseAnim.DRINK;
            return this;
        }

        public Builder useDuration(int ticks) {
            this.useDuration = ticks;
            return this;
        }

        public Builder effect(Supplier<net.minecraft.core.Holder<MobEffect>> effect, int duration, int amplifier, float probability) {
            effects.add(new FoodEffect(effect, duration, amplifier, probability));
            return this;
        }

        public Builder effect(Supplier<net.minecraft.core.Holder<MobEffect>> effect, int duration, int amplifier) {
            return effect(effect, duration, amplifier, 1.0f);
        }

        public NVFoodItem build() {
            properties.food(foodBuilder.build());
            return new NVFoodItem(properties, effects, useAnim, useDuration);
        }
    }
}
