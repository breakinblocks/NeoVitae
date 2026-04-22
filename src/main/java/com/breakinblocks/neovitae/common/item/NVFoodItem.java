package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;

/**
 * A food item that applies effects on consumption with optional probability.
 * Supports drink animation override for potions/bottles.
 */
public class NVFoodItem extends Item {

    private final List<FoodEffect> effects;
    private final ItemUseAnimation useAnim;
    private final int useDuration;

    private NVFoodItem(Properties properties, List<FoodEffect> effects, ItemUseAnimation useAnim, int useDuration) {
        super(properties);
        this.effects = effects;
        this.useAnim = useAnim;
        this.useDuration = useDuration;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            for (FoodEffect effect : effects) {
                if (effect.probability >= 1.0f || level.getRandom().nextFloat() < effect.probability) {
                    entity.addEffect(new MobEffectInstance(effect.effect.get(), effect.duration, effect.amplifier));
                }
            }
        }
        return result;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return useAnim;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return useDuration;
    }

    public static Builder builder(int nutrition, float saturation) {
        return new Builder(nutrition, saturation);
    }

    private record FoodEffect(Supplier<Holder<MobEffect>> effect, int duration, int amplifier, float probability) {}

    public static class Builder {
        private final FoodProperties.Builder foodBuilder;
        private final List<FoodEffect> effects = new ArrayList<>();
        private ItemUseAnimation useAnim = ItemUseAnimation.EAT;
        private int useDuration = 32;
        private int maxStack = 64;

        private Builder(int nutrition, float saturation) {
            this.foodBuilder = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation);
        }

        public Builder alwaysEdible() {
            foodBuilder.alwaysEdible();
            return this;
        }

        public Builder stacksTo(int max) {
            this.maxStack = max;
            return this;
        }

        public Builder drinkable() {
            this.useAnim = ItemUseAnimation.DRINK;
            return this;
        }

        public Builder useDuration(int ticks) {
            this.useDuration = ticks;
            return this;
        }

        public Builder effect(Supplier<Holder<MobEffect>> effect, int duration, int amplifier, float probability) {
            effects.add(new FoodEffect(effect, duration, amplifier, probability));
            return this;
        }

        public Builder effect(Supplier<Holder<MobEffect>> effect, int duration, int amplifier) {
            return effect(effect, duration, amplifier, 1.0f);
        }

        public NVFoodItem build(Item.Properties props) {
            props.stacksTo(maxStack).food(foodBuilder.build());
            return new NVFoodItem(props, effects, useAnim, useDuration);
        }
    }
}
