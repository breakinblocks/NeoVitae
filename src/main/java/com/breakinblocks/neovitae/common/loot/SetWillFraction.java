package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.IDemonWillGem;

import java.util.List;

/**
 * Loot function that sets the will fraction (percentage of max) on demon will gems.
 */
public class SetWillFraction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetWillFraction.class);

    public static final MapCodec<SetWillFraction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(
                    NumberProviders.CODEC.fieldOf("fraction").forGetter(func -> func.fractionRange)
            ).apply(instance, SetWillFraction::new)
    );

    private final NumberProvider fractionRange;

    private SetWillFraction(List<LootItemCondition> conditions, NumberProvider fractionRange) {
        super(conditions);
        this.fractionRange = fractionRange;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return BMLootFunctions.SET_WILL_FRACTION.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof IDemonWillGem gem) {
            int maxWill = gem.getMaxWill(EnumWillType.DEFAULT, stack);
            float fraction = 1.0F - fractionRange.getFloat(context);
            gem.setWill(EnumWillType.DEFAULT, stack, maxWill * fraction);
        } else {
            LOGGER.warn("Couldn't set will fraction of loot item {}", stack);
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> withRange(NumberProvider range) {
        return simpleBuilder(conditions -> new SetWillFraction(conditions, range));
    }

    public static LootItemConditionalFunction.Builder<?> withRange(float min, float max) {
        return withRange(UniformGenerator.between(min, max));
    }
}
