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
import com.breakinblocks.neovitae.will.IDemonWill;

import java.util.List;

/**
 * Loot function that sets the will amount on demon will items.
 */
public class SetWillRange extends LootItemConditionalFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetWillRange.class);

    public static final MapCodec<SetWillRange> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(
                    NumberProviders.CODEC.fieldOf("will").forGetter(func -> func.willRange)
            ).apply(instance, SetWillRange::new)
    );

    private final NumberProvider willRange;

    private SetWillRange(List<LootItemCondition> conditions, NumberProvider willRange) {
        super(conditions);
        this.willRange = willRange;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return BMLootFunctions.SET_WILL_RANGE.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof IDemonWill demonWill) {
            float will = willRange.getFloat(context);
            return demonWill.createWill(will);
        } else {
            LOGGER.warn("Couldn't set will of loot item {}", stack);
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> withRange(NumberProvider range) {
        return simpleBuilder(conditions -> new SetWillRange(conditions, range));
    }

    public static LootItemConditionalFunction.Builder<?> withRange(float min, float max) {
        return withRange(UniformGenerator.between(min, max));
    }

    /**
     * Builder alias for consistency with other loot functions.
     */
    public static LootItemConditionalFunction.Builder<?> builder(NumberProvider range) {
        return withRange(range);
    }
}
